package codegen.sourcegraph;

public class SourceGraphInst {

    private final String instruction;
    private final String[] args;

    public SourceGraphInst(String instruction, String... args) {
        this.instruction = instruction;
        this.args = args;
    }

    @Override
    public String toString() {
        final String argsString = String.join(" ", this.args);

        return "\t\t" + this.instruction + " " + argsString;
    }
}
