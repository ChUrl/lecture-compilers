package codegen.sourcegraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SourceBlock {

    private final String id;
    private final String label;
    private final List<SourceInst> instructions;
    private final Set<SourceBlock> pred;
    private final Set<SourceBlock> succ;

    public SourceBlock(String label) {
        this.label = label;
        this.id = String.valueOf(System.nanoTime());
        this.instructions = new ArrayList<>();
        this.pred = new HashSet<>();
        this.succ = new HashSet<>();
    }

    public SourceBlock() {
        this("");
    }

    public boolean isEmpty() {
        return this.label.isBlank() && this.instructions.isEmpty();
    }

    public void addLine(String instruction, String... args) {
        this.instructions.add(new SourceInst(instruction, args));
    }

    public List<SourceInst> getInstructions() {
        return this.instructions;
    }

    public String getLabel() {
        return this.label;
    }

    public void addSuccessor(SourceBlock block) {
        this.succ.add(block);
    }

    public void addPredecessor(SourceBlock block) {
        this.pred.add(block);
    }

    public Set<SourceBlock> getSuccessors() {
        return this.succ;
    }

    public Set<SourceBlock> getPredecessors() {
        return this.pred;
    }

    public String getId() {
        return this.id;
    }

    public String printInst() {
        return this.instructions.stream()
                                .map(inst -> inst.toString().trim() + "\\n")
                                .map(inst -> inst.replace("\"", "\\\""))
                                .collect(Collectors.joining());
    }

    public void addSuccessors(Set<SourceBlock> successors) {
        this.succ.addAll(successors);
    }

    public void addPredecessors(Set<SourceBlock> predecessors) {
        this.pred.addAll(predecessors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SourceBlock) {
            return this.id.equals(((SourceBlock) obj).id);
        }

        return false;
    }

    @Override
    public String toString() {
        final String linesString = this.instructions.stream()
                                                    .map(SourceInst::toString)
                                                    .map(line -> line + "\n")
                                                    .collect(Collectors.joining());

        if (this.label.isBlank()) {
            return linesString;
        }

        return this.label + ":\n"
               + linesString;
    }
}
