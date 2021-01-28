package codegen.analysis.flowgraph;

import java.util.List;
import java.util.stream.Collectors;

public class FlowGraph {

    private final FlowGraphHead head;
    private final List<FlowGraphBlock> blocks;
    private final FlowGraphTail tail;

    public FlowGraph(FlowGraphHead head, List<FlowGraphBlock> blocks, FlowGraphTail tail) {
        this.head = head;
        this.blocks = blocks;
        this.tail = tail;
    }

    public void print() {
        final String blocksString = this.blocks.stream()
                                               .map(FlowGraphBlock::toString)
                                               .map(string -> string + "-".repeat(50))
                                               .collect(Collectors.joining());

        System.out.println(this.head + "-".repeat(100)
                           + blocksString + "-".repeat(100)
                           + this.tail);
    }

    @Override
    public String toString() {
        final String blocksString = this.blocks.stream()
                                               .map(FlowGraphBlock::toString)
                                               .collect(Collectors.joining());

        return this.head
               + blocksString
               + this.tail;
    }
}
