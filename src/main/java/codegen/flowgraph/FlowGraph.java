package codegen.flowgraph;

import parser.ast.SyntaxTree;
import util.GraphvizCaller;
import util.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Die Graph-Repräsentation des Programm, erzeugt aus einem {@link SyntaxTree}.
 * Der Grundbaustein ist {@link FlowBasicBlock}, diese enthalten wiederum {@link FlowInstruction}.
 */
public class FlowGraph implements Iterable<FlowBasicBlock> {

    private final List<FlowBasicBlock> basicBlocks;

    // Only for Export to Jasmin-Assembler
    private final FlowGraphHead exportHead;
    private final FlowGraphTail exportTail;

    /**
     * Wenn ein neuer Block ein Label bekommt, welches in der Predecessor-Map vorhanden ist,
     * dann ist der hier gespeicherte Block ein Predecessor des neuen Blockes.
     * <p>
     * Einträge werden hier hinzugefügt, wenn ein Jump nach vorne passiert.
     * In diesem Fall ist der Jump-Successor noch nicht im Graph präsent.
     */
    private final Map<String, FlowBasicBlock> predecessorMap;

    public FlowGraph(String bytecodeVersion, String source, String clazz, int stackSize, int localCount) {
        this.exportHead = new FlowGraphHead(bytecodeVersion, source, clazz, stackSize, localCount);
        this.basicBlocks = new ArrayList<>();
        this.exportTail = new FlowGraphTail();
        this.predecessorMap = new HashMap<>();
    }

    /**
     * Ein Label markiert den Beginn eines neuen Blockes.
     * Es werden Predecessor/Successor-Verbindungen zum letzten Block
     * und zu Blöcken aus der {@link #predecessorMap} hergestellt.
     */
    public void addLabel(String label) {
        Logger.logInfo("Adding Label: " + label, FlowGraph.class);

        final FlowBasicBlock newBlock = new FlowBasicBlock(label);

        // Resolve missing successors/predecessors from jumps
        if (this.predecessorMap.containsKey(label)) {
            Logger.logInfo("Handling PredecessorMap Entry: " + this.predecessorMap.get(label), FlowGraph.class);

            this.predecessorMap.get(label).addSuccessorBlock(newBlock);
            newBlock.addPredecessorBlock(this.predecessorMap.get(label));
        }

        final Optional<FlowBasicBlock> currentBlock = this.getCurrentBlock();
        if (currentBlock.isPresent()) {
            newBlock.addPredecessorBlock(currentBlock.get()); // Obvious predecessor of new block
            currentBlock.get().addSuccessorBlock(newBlock); // Obvious successor of current block
        }

        this.basicBlocks.add(newBlock);
    }

    /**
     * Ein Jump markiert das Ende eines Blockes.
     * Es werden Predecessor/Successor-Verbindungen zum letzten Block hergestellt
     * und wenn nötig Einträge in der {@link #predecessorMap} angelegt.
     * <p>
     * Da GoTo immer springt, wird diese Sprunganweisung gesondert betrachtet.
     *
     * @param jumpInstruction Der verwendete Sprungbefehl.
     */
    public void addJump(String jumpInstruction, String label) {
        Logger.logInfo("Adding Jump to Label: " + label, FlowGraph.class);

        this.addInstruction(jumpInstruction, label);

        final FlowBasicBlock newBlock = new FlowBasicBlock();

        if (!"goto".equals(jumpInstruction)) {
            // Goto always jumps, so we don't have a direct relation in order of the code

            final Optional<FlowBasicBlock> currentBlock = this.getCurrentBlock();
            if (currentBlock.isPresent()) {

                newBlock.addPredecessorBlock(currentBlock.get()); // Obvious predecessor of new block
                currentBlock.get().addSuccessorBlock(newBlock); // Obvious successor of current block
            }
        }

        // Jumped successor
        final Optional<FlowBasicBlock> labelBlock = this.getBlockByLabel(label);
        final Optional<FlowBasicBlock> currentBlock = this.getCurrentBlock();

        if (labelBlock.isPresent()) {
            // Successor exists

            if (currentBlock.isPresent()) {
                currentBlock.get().addSuccessorBlock(labelBlock.get());
                labelBlock.get().addPredecessorBlock(currentBlock.get());
            }
        } else {
            // Successor doesn't exist, so wait until it does

            // Current node is predecessor of label-block
            Logger.logInfo("Adding Entry to PredecessorMap: " + currentBlock, FlowGraph.class);
            currentBlock.ifPresent(flowBasicBlock -> this.predecessorMap.put(label, flowBasicBlock));
        }

        this.basicBlocks.add(newBlock);
    }

    public void addInstruction(String instruction, String... args) {
        Logger.logInfo("Adding Instruction: " + instruction, FlowGraph.class);

        if (this.basicBlocks.isEmpty()) {
            this.basicBlocks.add(new FlowBasicBlock("START")); // First block doesn't exist
        }

        final Optional<FlowBasicBlock> currentBlock = this.getCurrentBlock();

        // Add to last block
        currentBlock.ifPresent(flowBasicBlock -> flowBasicBlock.addInstruction(instruction, args));
    }

    /**
     * Entfernt leere Blöcke.
     * Ein Block ist "leer", wenn er kein Label und keine Instructions hat.
     */
    public void purgeEmptyBlocks() {
        Logger.logDebug("Purging empty blocks", FlowGraph.class);

        final Collection<FlowBasicBlock> toRemove = new HashSet<>();

        // Collect removable blocks
        for (FlowBasicBlock block : this.basicBlocks) {
            if (block.isEmpty()) {
                Logger.logInfo("Marking Block " + this.basicBlocks.indexOf(block) + " as removable.", FlowGraph.class);
                toRemove.add(block);
            }
        }

        // Remove blocks + reroute predecessors/successors
        for (FlowBasicBlock block : toRemove) {

            // Reroute
            for (FlowBasicBlock predecessor : block.getBlockPredecessorSet()) {
                for (FlowBasicBlock successor : block.getBlockSuccessorSet()) {

                    Logger.logInfo("Rerouting Block " + this.basicBlocks.indexOf(predecessor)
                                   + " to Block " + this.basicBlocks.indexOf(successor), FlowGraph.class);
                    predecessor.addSuccessorBlock(successor);
                    successor.addPredecessorBlock(predecessor);
                }
            }

            // Remove references
            for (FlowBasicBlock predecessor : block.getBlockPredecessorSet()) {
                predecessor.removeSuccessorBlock(block);
            }

            for (FlowBasicBlock successor : block.getBlockSuccessorSet()) {
                successor.removePredecessorBlock(block);
            }
        }

        this.basicBlocks.removeAll(toRemove);

        Logger.logDebug("Successfully removed all empty blocks and rerouted graph", FlowGraph.class);
    }

    private Optional<FlowBasicBlock> getBlockByLabel(String label) {
        return this.basicBlocks.stream()
                               .filter(block -> block.getLabel().equals(label))
                               .findFirst();
    }

    /**
     * Der aktuelle Block ist immer der letzte Block.
     */
    private Optional<FlowBasicBlock> getCurrentBlock() {
        if (this.basicBlocks.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(this.basicBlocks.get(this.basicBlocks.size() - 1));
    }

    // Printing

    public String printToImage() {
        final Optional<FlowBasicBlock> currentBlock = this.getCurrentBlock();

        if (this.basicBlocks.isEmpty() || currentBlock.isEmpty()) {
            return "Empty Graph";
        }

        final StringBuilder dot = new StringBuilder();

        dot.append("digraph dfd {\n")
           .append("node[shape=Mrecord]\n");

        for (FlowBasicBlock block : this.basicBlocks) {
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
        dot.append(currentBlock.get().getId()).append(" -> END;\n");

        for (FlowBasicBlock block : this.basicBlocks) {
            // Successors

            for (FlowBasicBlock successor : block.getBlockSuccessorSet()) {
                dot.append(block.getId()).append(" -> ").append(successor.getId()).append(";\n");
            }
        }

        dot.append("}");

        GraphvizCaller.callGraphviz(dot, "FlowGraph");

        return "Finished.";
    }

    // Overrides

    @Override
    public String toString() {
        final String blocksString = this.basicBlocks.stream()
                                                    .map(FlowBasicBlock::toString)
                                                    .collect(Collectors.joining());

        return this.exportHead
               + blocksString
               + this.exportTail;
    }

    @Override
    public Iterator<FlowBasicBlock> iterator() {
        return this.basicBlocks.iterator();
    }
}
