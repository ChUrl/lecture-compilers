package codegen.flowgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FlowBasicBlock implements Iterable<FlowInstruction> {

    // Graph structure information
    private final String id;
    private final Set<FlowBasicBlock> predecessors;
    private final Set<FlowBasicBlock> successors;

    /**
     * Das Label ist das Jump-Label, über welches ein Block angesprungen werden kann.
     */
    private final String label;

    /**
     * Alle Instructions, welche zu einem Block gehören.
     * Diese werden immer sequentiell ohne Verzweigungen ausgeführt.
     */
    private final List<FlowInstruction> instructions;

    /**
     * Wird intern benutzt um die ID für eine hinzugefügte {@link FlowInstruction} zu ermitteln.
     */
    private int instNr;

    public FlowBasicBlock(String label) {
        this.label = label;
        this.id = String.valueOf(System.nanoTime());
        this.instructions = new ArrayList<>();
        this.predecessors = new HashSet<>();
        this.successors = new HashSet<>();
    }

    public FlowBasicBlock() {
        this("");
    }

    /**
     * Ermittelt ob ein BasicBlock ohne weiteres entfernbar ist.
     * Der Block darf kein Label haben, damit keine Sprünge ins Leere passieren.
     */
    public boolean isEmpty() {
        return this.instructions.isEmpty() && this.label.isBlank();
    }

    // Geteter, Setter

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public void addInstruction(String instruction, String... args) {
        this.instNr++;
        this.instructions.add(new FlowInstruction(String.valueOf(Long.parseLong(this.id) + this.instNr),
                                                  instruction, args));
    }

    public Set<FlowBasicBlock> getBlockSuccessorSet() {
        return Collections.unmodifiableSet(this.successors);
    }

    /**
     * Ermittelt alle Instructions, welche auf eine {@link FlowInstruction} folgen können.
     * Befindet sich die Instruction am Ende des Blockes, werden Instructions aus Successor-Blöcken gesucht.
     */
    public Set<FlowInstruction> getInstructionSuccessorSet(FlowInstruction inst) {
        final int index = this.instructions.indexOf(inst);

        if (index == -1) {
            return Collections.emptySet();
        }

        if (index >= 0 && index < this.instructions.size() - 1) {
            // Instruction is in the beginning or in the middle

            return Set.of(this.instructions.get(index + 1));
        }

        // Instruction is at the end
        return this.successors.stream()
                              .map(FlowBasicBlock::getFirstInstruction)
                              .filter(Optional::isPresent)
                              .map(Optional::get)
                              .collect(Collectors.toUnmodifiableSet());
    }

    public boolean addSuccessorBlock(FlowBasicBlock successor) {
        return this.successors.add(successor);
    }

    public boolean removeSuccessorBlock(FlowBasicBlock successor) {
        return this.successors.remove(successor);
    }

    public Set<FlowBasicBlock> getBlockPredecessorSet() {
        return Collections.unmodifiableSet(this.predecessors);
    }

    /**
     * Ermittelt alle Instructions, welche Predecessor einer {@link FlowInstruction} sein können.
     * Befindet sich die Instruction am Anfang des Blockes, werden Instructions aus Predecessor-Blöcken gesucht.
     */
    public Set<FlowInstruction> getInstructionPredecessorSet(FlowInstruction inst) {
        final int index = this.instructions.indexOf(inst);

        if (index == -1) {
            return Collections.emptySet();
        }

        if (index > 0 && index <= this.instructions.size() - 1) {
            // Instruction is in the middle or at the end

            return Set.of(this.instructions.get(index - 1));
        }

        // Instruction is at the beginning
        return this.predecessors.stream()
                                .map(FlowBasicBlock::getLastInstruction)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean addPredecessorBlock(FlowBasicBlock predecessor) {
        return this.predecessors.add(predecessor);
    }

    public boolean removePredecessorBlock(FlowBasicBlock predecessor) {
        return this.predecessors.remove(predecessor);
    }

    public Optional<FlowInstruction> getFirstInstruction() {
        if (this.instructions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(this.instructions.get(0));
    }

    public Optional<FlowInstruction> getLastInstruction() {
        if (this.instructions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(this.instructions.get(this.instructions.size() - 1));
    }

    // Printing

    /**
     * Diese Methode ist für das Printen mit Graphviz, {@link #toString()} für den Rest.
     */
    public String printInst() {
        return this.instructions.stream()
                                .map(inst -> inst.toString().trim() + "\\n")
                                .map(inst -> inst.replace("\"", "\\\""))
                                .map(inst -> inst.replace("<", "less"))
                                .map(inst -> inst.replace(">", "greater"))
                                .collect(Collectors.joining());
    }

    // Overrides

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final FlowBasicBlock that = (FlowBasicBlock) o;
        return this.id.equals(that.id) && this.label.equals(that.label);
    }

    @Override
    public String toString() {
        final String linesString = this.instructions.stream()
                                                    .map(FlowInstruction::toString)
                                                    .map(line -> line + "\n")
                                                    .collect(Collectors.joining());

        if (this.label.isBlank()) {
            return linesString;
        }

        return this.label + ":\n"
               + linesString;
    }

    @Override
    public Iterator<FlowInstruction> iterator() {
        return this.instructions.iterator();
    }
}
