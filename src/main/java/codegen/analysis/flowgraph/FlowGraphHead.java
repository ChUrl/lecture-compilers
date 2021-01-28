package codegen.analysis.flowgraph;

public class FlowGraphHead {

    private final String bytecodeVersion;
    private final String source;
    private final String clazz;
    private final int stackSize;
    private final int localCount;

    public FlowGraphHead(String bytecodeVersion, String source, String clazz, int stackSize, int localCount) {
        this.bytecodeVersion = bytecodeVersion;
        this.source = source;
        this.clazz = clazz;
        this.stackSize = stackSize;
        this.localCount = localCount;
    }

    @Override
    public String toString() {
        return ".bytecode " + this.bytecodeVersion + "\n"
               + ".source " + this.source + "\n"
               + ".class public" + this.clazz + "\n"
               + ".super java/lang/Object\n"
               + ".method public <init>()V\n"
               + "\t.limit stack 1\n"
               + "\t.limit locals 1\n"
               + "\t\taload_0\n"
               + "\t\tinvokespecial java/lang/Object/<init>()V\n"
               + "\t\treturn\n"
               + ".end method\n\n"

               + ".method public static main([Ljava/lang/String;)V\n"
               + "\t.limit stack " + this.stackSize + "\n"
               + "\t.limit locals " + this.localCount + "\n";
    }
}
