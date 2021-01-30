package codegen.flowgraph;

import util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FlowGraph {

    private final FlowGraphHead head;
    private final List<FlowBasicBlock> basicBlocks;
    private final FlowGraphTail tail;

    // If a new block has this label, the value in this map is a predecessor
    private final Map<String, FlowBasicBlock> predecessorMap;

    public FlowGraph(String bytecodeVersion, String source, String clazz, int stackSize, int localCount) {
        this.head = new FlowGraphHead(bytecodeVersion, source, clazz, stackSize, localCount);
        this.basicBlocks = new ArrayList<>();
        this.tail = new FlowGraphTail();
        this.predecessorMap = new HashMap<>();
    }

    // Label marks beginning of block
    public void addLabel(String label) {
        final FlowBasicBlock newBlock = new FlowBasicBlock(label);

        // Resolve missing successors/predecessors from jumps
        if (this.predecessorMap.containsKey(label)) {
            this.predecessorMap.get(label).addSuccessor(newBlock);
            newBlock.addPredecessor(this.predecessorMap.get(label));
        }

        newBlock.addPredecessor(this.getCurrentBlock()); // Obvious predecessor of new block
        this.getCurrentBlock().addSuccessor(newBlock); // Obvious successor of current block

        this.basicBlocks.add(newBlock);
    }

    // Jump means end of block
    public void addJump(String jumpInstruction, String label) {
        this.addInstruction(jumpInstruction, label);

        final FlowBasicBlock newBlock = new FlowBasicBlock();

        if (!"goto".equals(jumpInstruction)) {
            // Goto always jumps, so we don't have a direct relation in order of the code

            newBlock.addPredecessor(this.getCurrentBlock()); // Obvious predecessor of new block
            this.getCurrentBlock().addSuccessor(newBlock); // Obvious successor of current block
        }

        // Jumped successor
        final FlowBasicBlock labelBlock = this.getBlockByLabel(label);
        if (labelBlock != null) {
            // Successor exists

            this.getCurrentBlock().addSuccessor(labelBlock);
            labelBlock.addPredecessor(this.getCurrentBlock());
        } else {
            // Successor doesn't exist, so wait until it does

            this.predecessorMap.put(label, this.getCurrentBlock()); // Current node is predecessor of label-block
        }

        this.basicBlocks.add(newBlock);
    }

    public void addInstruction(String instruction, String... args) {
        if (this.basicBlocks.isEmpty()) {
            this.basicBlocks.add(new FlowBasicBlock("START")); // First block doesn't exist
        }

        this.getCurrentBlock().addInstruction(instruction, args); // Add to last block
    }

    /**
     * Entfernt leere Blöcke.
     */
    public void purgeEmptyBlocks() {
        Logger.log("\nPurging empty blocks: ");

        final Set<FlowBasicBlock> toRemove = new HashSet<>();

        // Collect removable blocks
        for (FlowBasicBlock block : this.basicBlocks) {
            if (block.isEmpty()) {
                Logger.log("Marking Block " + this.basicBlocks.indexOf(block) + " as removable.");
                toRemove.add(block);
            }
        }

        // Remove blocks + reroute predecessors/successors
        for (FlowBasicBlock block : toRemove) {

            for (FlowBasicBlock pred : block.getPredecessorSet()) {

                for (FlowBasicBlock succ : block.getSuccessorSet()) {

                    Logger.log("Rerouting Block " + this.basicBlocks.indexOf(pred) + " to Block " + this.basicBlocks.indexOf(succ));
                    pred.addSuccessor(succ);
                    succ.addPredecessor(pred);
                }

                pred.getSuccessorSet().remove(block);
            }

            for (FlowBasicBlock succ : block.getSuccessorSet()) {
                succ.getPredecessorSet().remove(block);
            }
        }

        this.basicBlocks.removeAll(toRemove);
    }

    // Getters, Setters

    private FlowBasicBlock getBlockByLabel(String label) {
        return this.basicBlocks.stream()
                               .filter(block -> block.getLabel().equals(label))
                               .findFirst()
                               .orElse(null);
    }

    private FlowBasicBlock getCurrentBlock() {
        return this.basicBlocks.get(this.basicBlocks.size() - 1);
    }

    public List<FlowBasicBlock> getBasicBlocks() {
        return this.basicBlocks;
    }

    // Print + Overrides

    public String print() {
        final String blocksString = this.basicBlocks.stream()
                                                    .map(FlowBasicBlock::toString)
                                                    .map(string -> string + "-".repeat(50) + "\n")
                                                    .collect(Collectors.joining());

        return this.head + "-".repeat(100) + "\n"
               + "-".repeat(50) + "\n" + blocksString + "-".repeat(100) + "\n"
               + this.tail;
    }

    public String printToImage() {
        final StringBuilder dot = new StringBuilder();

        dot.append("digraph dfd {\n")
           .append("node[shape=Mrecord]\n");

        for (FlowBasicBlock block : this.basicBlocks) {
            System.out.println(block);
            System.out.println("-".repeat(100));
            dot.append(block.getId())
               .append(" [label=\"{<f0> ")
               .append(this.basicBlocks.indexOf(block))
               .append(": ")
               .append(block.getLabel())
               .append("|<f1> ")
               .append(block.printInst())
               .append("}\"];\n");
        }

        dot.append("START[label=\"START\"];\n")
           .append("END[label=\"END\"];\n");

        dot.append("START -> ").append(this.basicBlocks.get(0).getId()).append(";\n");
        dot.append(this.getCurrentBlock().getId()).append(" -> END;\n");

        for (FlowBasicBlock block : this.basicBlocks) {
            // Successors
            for (FlowBasicBlock succ : block.getSuccessorSet()) {
                if (!dot.toString().contains(block.getId() + " -> " + succ.getId())) {
                    // No duplicate arrows

                    dot.append(block.getId()).append(" -> ").append(succ.getId()).append(";\n");
                }
            }

            // Predecessors
            for (FlowBasicBlock pred : block.getPredecessorSet()) {
                if (!dot.toString().contains(pred.getId() + " -> " + block.getId())) {
                    // No duplicate arrows

                    dot.append(pred.getId()).append(" -> ").append(block.getId()).append(";\n");
                }
            }
        }

        dot.append("}");

        final String dotOut = dot.toString();

        final Path dotFile = Paths.get(System.getProperty("user.dir") + "/FlowGraph.dot");
        try {
            Files.writeString(dotFile, dotOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ProcessBuilder dotCompile = new ProcessBuilder("dot", "-Tsvg", "-oFlowGraph.svg", "FlowGraph.dot");
        try {
            dotCompile.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Finished.";
    }

    @Override
    public String toString() {
        final String blocksString = this.basicBlocks.stream()
                                                    .map(FlowBasicBlock::toString)
                                                    .collect(Collectors.joining());

        return this.head
               + blocksString
               + this.tail;
    }

}
