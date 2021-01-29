package codegen.flowgraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FlowBasicBlock {

    private final String id;
    private final String label;
    private final List<FlowInstruction> instructions;
    private final Set<FlowBasicBlock> predecessors;
    private final Set<FlowBasicBlock> successors;

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

    public boolean isEmpty() {
        return this.label.isBlank() && this.instructions.isEmpty();
    }

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public void addInstruction(String instruction, String... args) {
        this.instNr++;
        this.instructions.add(new FlowInstruction(String.valueOf(Long.parseLong(this.id) + this.instNr), this.id,
                                                  instruction, args));
    }

    public List<FlowInstruction> getInstructions() {
        return this.instructions;
    }

    public void addSuccessor(FlowBasicBlock block) {
        this.successors.add(block);
    }

    public void addSuccessors(Set<FlowBasicBlock> successors) {
        this.successors.addAll(successors);
    }

    public void addPredecessor(FlowBasicBlock block) {
        this.predecessors.add(block);
    }

    public void addPredecessors(Set<FlowBasicBlock> predecessors) {
        this.predecessors.addAll(predecessors);
    }

    public Set<FlowBasicBlock> getSuccessorSet() {
        return this.successors;
    }

    public Set<FlowInstruction> getPredecessors(FlowInstruction inst) {
        final int index = this.instructions.indexOf(inst);

        if (index == -1) {
            return null;
        }

        if (index > 0 && index <= this.instructions.size() - 1) {
            // Instruction is in the middle or end

            return Set.of(this.instructions.get(index - 1));
        }

        // Instruction is at the beginning
        return this.predecessors.stream()
                                .map(FlowBasicBlock::getLastInst)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<FlowInstruction> getSuccessors(FlowInstruction inst) {
        final int index = this.instructions.indexOf(inst);

        if (index == -1) {
            return null;
        }

        if (index >= 0 && index < this.instructions.size() - 1) {
            // Instruction is in the beginning or middle

            return Set.of(this.instructions.get(index + 1));
        }

        // Instruction is at the end
        return this.successors.stream()
                              .map(FlowBasicBlock::getFirstInst)
                              .filter(Objects::nonNull)
                              .collect(Collectors.toUnmodifiableSet());
    }

    public Set<FlowBasicBlock> getPredecessorSet() {
        return this.predecessors;
    }

    public FlowInstruction getFirstInst() {
        if (!this.instructions.isEmpty()) {
            return this.instructions.get(0);
        }

        return null;
    }

    public FlowInstruction getLastInst() {
        if (!this.instructions.isEmpty()) {
            return this.instructions.get(this.instructions.size() - 1);
        }

        return null;
    }

    // Print + Overrides

    public String printInst() {
        return this.instructions.stream()
                                .map(inst -> inst.toString().trim() + "\\n")
                                .map(inst -> inst.replace("\"", "\\\""))
                                .collect(Collectors.joining());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FlowBasicBlock) {
            return this.id.equals(((FlowBasicBlock) obj).id);
        }

        return false;
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
}
