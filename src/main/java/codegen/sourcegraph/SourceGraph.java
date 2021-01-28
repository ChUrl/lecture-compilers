package codegen.sourcegraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SourceGraph {

    private final SourceGraphHead head;
    private final List<SourceBlock> blocks;
    private final SourceGraphTail tail;

    // If a new block has this label, the value in this map is a predecessor
    private final Map<String, SourceBlock> predecessorMap;

    public SourceGraph(String bytecodeVersion, String source, String clazz, int stackSize, int localCount) {
        this.head = new SourceGraphHead(bytecodeVersion, source, clazz, stackSize, localCount);
        this.blocks = new ArrayList<>();
        this.tail = new SourceGraphTail();
        this.predecessorMap = new HashMap<>();
    }

    // Label marks beginning of block
    public void addLabel(String label) {
        final SourceBlock newBlock = new SourceBlock(label);


        // Resolve missing successors/predecessors from jumps
        if (this.predecessorMap.containsKey(label)) {
            this.predecessorMap.get(label).addSuccessor(newBlock);
            newBlock.addPredecessor(this.predecessorMap.get(label));

            // this.predecessorMap.remove(label); // Problematic if multiple gotos to same label
        }

        if (this.getCurrentBlock().isEmpty()) {
            // Replace empty blocks, we don't need them

            if (this.blocks.size() >= 2) {
                // This empty blocks successors become the previous blocks successors after replacment

                this.blocks.get(this.blocks.size() - 2).addSuccessors(this.getCurrentBlock().getSuccessors());
            }

            // Previous blocks predecessors are also the new blocks predecessors
            newBlock.addPredecessors(this.getCurrentBlock().getPredecessors());

            this.blocks.set(this.blocks.size() - 1, newBlock); // Replace
        } else {
            // Append block if last one isn't empty

            newBlock.addPredecessor(this.getCurrentBlock()); // Obvious predecessor of new block
            this.getCurrentBlock().addSuccessor(newBlock); // Obvious successor of current block

            this.blocks.add(newBlock);
        }
    }

    // Jump means end of block
    public void addJump(String jumpInstruction, String label) {
        this.addInst(jumpInstruction, label);

        final SourceBlock newBlock = new SourceBlock();
        newBlock.addPredecessor(this.getCurrentBlock()); // Obvious predecessor of new block

        if (!"goto".equals(jumpInstruction)) {
            // Goto always jumps

            this.getCurrentBlock().addSuccessor(newBlock); // Obvious successor of current block
        }

        // Jumped successor
        final SourceBlock labelBlock = this.getBlockByLabel(label);
        if (labelBlock != null) {
            // Successor exists

            this.getCurrentBlock().addSuccessor(labelBlock);
            labelBlock.addPredecessor(this.getCurrentBlock());
        } else {
            // Successor doesn't exist, so wait until it does

            this.predecessorMap.put(label, this.getCurrentBlock()); // Current node is predecessor of label-block
        }

        this.blocks.add(newBlock);
    }

    public void addInst(String instruction, String... args) {
        if (this.blocks.isEmpty()) {
            this.blocks.add(new SourceBlock()); // First block doesn't exist
        }

        this.getCurrentBlock().addLine(instruction, args); // Add to last block
    }

    public String print() {
        final String blocksString = this.blocks.stream()
                                               .map(SourceBlock::toString)
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

        for (SourceBlock block : this.blocks) {
            dot.append(block.getId())
               .append(" [label=\"{<f0> ")
               .append(block.getLabel())
               .append("|<f1> ")
               .append(block.printInst())
               .append("}\"];\n");
        }

        dot.append("START[label=\"START\"];\n")
           .append("END[label=\"END\"];\n");

        dot.append("START -> ").append(this.blocks.get(0).getId()).append(";\n");
        dot.append(this.getCurrentBlock().getId()).append(" -> END;\n");

        for (SourceBlock block : this.blocks) {
            for (SourceBlock succ : block.getSuccessors()) {
                dot.append(block.getId()).append(" -> ").append(succ.getId()).append(";\n");
            }
        }

        dot.append("}");

        final String dotOut = dot.toString();

        final Path dotFile = Paths.get(System.getProperty("user.dir") + "/DotOut.dot");
        try {
            Files.writeString(dotFile, dotOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ProcessBuilder dotCompile = new ProcessBuilder("dot", "-Tsvg", "-O", "DotOut.dot");
        try {
            dotCompile.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Finished.";
    }

    @Override
    public String toString() {
        final String blocksString = this.blocks.stream()
                                               .map(SourceBlock::toString)
                                               .collect(Collectors.joining());

        return this.head
               + blocksString
               + this.tail;
    }

    private SourceBlock getBlockByLabel(String label) {
        return this.blocks.stream()
                          .filter(block -> block.getLabel().equals(label))
                          .findFirst()
                          .orElse(null);
    }

    private SourceBlock getCurrentBlock() {
        return this.blocks.get(this.blocks.size() - 1);
    }
}
