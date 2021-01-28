package codegen.analysis.flowgraph;

import java.util.List;
import java.util.stream.Collectors;

public class FlowGraphBlock {

    private final String label;
    private final List<FlowGraphLine> lines;

    public FlowGraphBlock(String label, List<FlowGraphLine> lines) {
        this.label = label;
        this.lines = lines;
    }

    @Override
    public String toString() {
        final String linesString = this.lines.stream()
                                             .map(FlowGraphLine::toString)
                                             .collect(Collectors.joining());

        return this.label + ":\n"
               + linesString;
    }
}
