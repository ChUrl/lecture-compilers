package codegen.analysis.dataflow;

import codegen.flowgraph.FlowBasicBlock;
import codegen.flowgraph.FlowInstruction;

import java.util.HashSet;
import java.util.Set;

public final class DataFlowNode {

    private final String id;
    private final String blockId;
    private final String inst;
    private final String use;
    private final String def;
    private final Set<String> in;
    private final Set<String> out;
    private final Set<DataFlowNode> predecessors;
    private final Set<DataFlowNode> successors;

    private DataFlowNode(String id, String blockId, String inst, String use, String def) {
        this.id = id;
        this.blockId = blockId;
        this.inst = inst;
        this.use = use;
        this.def = def;
        this.in = new HashSet<>();
        this.out = new HashSet<>();
        this.predecessors = new HashSet<>();
        this.successors = new HashSet<>();
    }

    public static DataFlowNode fromFlowNode(FlowInstruction srcInst, FlowBasicBlock block) {
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

        return new DataFlowNode(srcInst.getId(), block.getId(), srcInst.getInstruction(), use, def);
    }

    public String getId() {
        return this.id;
    }

    public String getBlockId() {
        return this.blockId;
    }

    public Set<String> getUse() {
        return Set.of(this.use);
    }

    public Set<String> getDef() {
        return Set.of(this.def);
    }

    public Set<String> getIn() {
        return this.in;
    }

    public Set<String> getOut() {
        return this.out;
    }

    public String getInst() {
        return this.inst;
    }

    public void addPredecessor(DataFlowNode node) {
        this.predecessors.add(node);
    }

    public Set<DataFlowNode> getPredecessors() {
        return this.predecessors;
    }

    public Set<DataFlowNode> getSuccessors() {
        return this.successors;
    }

    public void addSuccessor(DataFlowNode node) {
        this.successors.add(node);
    }

    public void addOut(Set<String> out) {
        if (out.isEmpty()) {
            return;
        }

        if (out.stream().allMatch(String::isEmpty)) {
            return;
        }

        this.out.addAll(out);
    }

    public boolean addIn(Set<String> in) {
        if (in.isEmpty()) {
            return false;
        }

        if (in.stream().allMatch(String::isEmpty)) {
            return false;
        }

        return this.in.addAll(in);
    }
}
