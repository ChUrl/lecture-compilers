package codegen.analysis.dataflow;

import codegen.flowgraph.FlowInstruction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Die Datenflussrepräsentation einer Instruktion.
 */
public final class DataFlowNode {

    // General graph structure information
    private final String id;
    private final Set<DataFlowNode> predecessors;
    private final Set<DataFlowNode> successors;

    /**
     * Die Instruction, welche auch die zugehörige {@link FlowInstruction} enthält.
     */
    private final String inst;

    /**
     * Die Variable, die von diesem Block verwendet wird.
     * Da wir keinen 3-Address-Code, sondern Jasmin-Assembler haben, ist das maximal eine.
     */
    private final String use;

    /**
     * Die Variable, die von diesem Block definiert wird.
     * Da wir keinen 3-Address-Code, sondern Jasmin-Assembler haben, ist das maximal eine.
     */
    private final String def;

    /**
     * Alle Variablen, welche live in diesem Node ankommen.
     */
    private final Set<String> in;

    /**
     * Alle Variablen , welche diesen Node live verlassen.
     */
    private final Set<String> out;

    private DataFlowNode(String id, String inst, String use, String def) {
        this.id = id;
        this.inst = inst;
        this.use = use;
        this.def = def;
        this.in = new HashSet<>();
        this.out = new HashSet<>();
        this.predecessors = new HashSet<>();
        this.successors = new HashSet<>();
    }

    public static DataFlowNode fromFlowNode(FlowInstruction srcInst) {
        final String instType = switch (srcInst.getInstruction()) {
            case "aload", "iload" -> "use";
            case "astore", "istore" -> "def";
            case "goto", "ifeq",
                    "if_icmpeq", "if_acmpeq", "if_icmpne", "if_acmpne",
                    "if_icmplt", "if_icmple", "if_icmpgt", "if_icmpge" -> "jmp";
            default -> "";
        };

        String use = "";
        String def = "";
        if ("use".equals(instType)) {
            use = srcInst.getArgs()[0];
        } else if ("def".equals(instType)) {
            def = srcInst.getArgs()[0];
        }

        return new DataFlowNode(srcInst.getId(), srcInst.getInstruction(), use, def);
    }

    // Getters, Setters

    public String getId() {
        return this.id;
    }

    public String getInst() {
        return this.inst;
    }

    public Set<DataFlowNode> getPredecessorSet() {
        return Collections.unmodifiableSet(this.predecessors);
    }

    public boolean addPredecessor(DataFlowNode node) {
        return this.predecessors.add(node);
    }

    public Set<DataFlowNode> getSuccessorSet() {
        return Collections.unmodifiableSet(this.successors);
    }

    public boolean addSuccessor(DataFlowNode node) {
        return this.successors.add(node);
    }

    public Set<String> getUseSet() {
        return Set.of(this.use);
    }

    public Set<String> getDefSet() {
        return Set.of(this.def);
    }

    public Set<String> getInSet() {
        return Collections.unmodifiableSet(this.in);
    }

    public boolean addIn(Collection<String> in) {
        if (in.isEmpty()) {
            return false;
        }

        if (in.stream().allMatch(String::isBlank)) {
            return false;
        }

        return this.in.addAll(in.stream()
                                .filter(string -> !string.isBlank())
                                .collect(Collectors.toSet()));
    }

    public Set<String> getOutSet() {
        return Collections.unmodifiableSet(this.out);
    }

    public void addOut(Collection<String> out) {
        if (out.isEmpty()) {
            return;
        }

        if (out.stream().allMatch(String::isBlank)) {
            return;
        }

        this.out.addAll(out.stream()
                           .filter(string -> !string.isBlank())
                           .collect(Collectors.toSet()));
    }

    // Overrides

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.inst);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DataFlowNode that = (DataFlowNode) o;
        return this.id.equals(that.id) && this.inst.equals(that.inst);
    }
}
