package codegen.sourcegraph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SourceGraph {

    private final SourceGraphHead head;
    private final List<SourceGraphBlock> blocks;
    private final SourceGraphTail tail;

    public SourceGraph(String bytecodeVersion, String source, String clazz, int stackSize, int localCount) {
        this.head = new SourceGraphHead(bytecodeVersion, source, clazz, stackSize, localCount);
        this.blocks = new ArrayList<>();
        this.tail = new SourceGraphTail();
    }

    public void addLabel(String label) {
        if (this.blocks.get(this.blocks.size() - 1).isEmpty()) {
            // Replace empty blocks, we don't need them

            this.blocks.set(this.blocks.size() - 1, new SourceGraphBlock(label));
        } else {
            this.blocks.add(new SourceGraphBlock(label));
        }
    }

    public void addInst(String instruction, String... args) {
        if (this.blocks.isEmpty()) {
            this.blocks.add(new SourceGraphBlock());
        }

        this.blocks.get(this.blocks.size() - 1).addLine(instruction, args);
    }

    public void addJump(String instruction, String label) {
        this.addInst(instruction, label);
        this.blocks.add(new SourceGraphBlock());
    }

    public String print() {
        final String blocksString = this.blocks.stream()
                                               .map(SourceGraphBlock::toString)
                                               .map(string -> string + "-".repeat(50) + "\n")
                                               .collect(Collectors.joining());

        return this.head + "-".repeat(100) + "\n"
               + "-".repeat(50) + "\n" + blocksString + "-".repeat(100) + "\n"
               + this.tail;
    }

    @Override
    public String toString() {
        final String blocksString = this.blocks.stream()
                                               .map(SourceGraphBlock::toString)
                                               .collect(Collectors.joining());

        return this.head
               + blocksString
               + this.tail;
    }
}
