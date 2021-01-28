package codegen.sourcegraph;

public class SourceInst {

    private final String instruction;
    private final String[] args;

    public SourceInst(String instruction, String... args) {
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
}
