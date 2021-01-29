package codegen.flowgraph;

public class FlowInstruction {

    private final String id;
    private final String blockId;
    private final String instruction;
    private final String[] args;

    public FlowInstruction(String id, String blockId, String instruction, String... args) {
        this.id = id;
        this.blockId = blockId;
        this.instruction = instruction;
        this.args = args;
    }

    @Override
    public String toString() {
        final String argsString = String.join(" ", this.args);

        return "\t\t" + this.instruction + " " + argsString;
    }

    public String getInstruction() {
        return this.instruction;
    }

    public String[] getArgs() {
        return this.args;
    }

    public String getBlockId() {
        return this.blockId;
    }

    public String getId() {
        return this.id;
    }
}
