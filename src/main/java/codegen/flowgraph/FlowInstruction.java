package codegen.flowgraph;

/**
 * Repräsentiert eine Instruction im {@link FlowGraph}.
 */
public class FlowInstruction {

    private final String id;

    /**
     * Die Instruction ist der Jasmin-Assembler Befehl.
     */
    private final String instruction;
    private final String[] args;

    public FlowInstruction(String id, String instruction, String... args) {
        this.id = id;
        this.instruction = instruction;
        this.args = args;
    }

    public String getId() {
        return this.id;
    }

    public String getInstruction() {
        return this.instruction;
    }

    public String[] getArgs() {
        return this.args;
    }

    // Overrides

    @Override
    public String toString() {
        final String argsString = String.join(" ", this.args);

        return "\t\t" + this.instruction + " " + argsString;
    }
}
