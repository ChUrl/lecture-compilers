package codegen.sourcegraph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SourceGraphBlock {

    private final String label;
    private final List<SourceGraphInst> lines;

    public SourceGraphBlock(String label) {
        this.label = label;
        this.lines = new ArrayList<>();
    }

    public SourceGraphBlock() {
        this.label = "";
        this.lines = new ArrayList<>();
    }

    public boolean isEmpty() {
        return this.label.isEmpty() && this.lines.isEmpty();
    }

    @Override
    public String toString() {
        final String linesString = this.lines.stream()
                                             .map(SourceGraphInst::toString)
                                             .map(line -> line + "\n")
                                             .collect(Collectors.joining());

        if (this.label.isBlank()) {
            return linesString;
        }

        return this.label + ":\n"
               + linesString;
    }

    public void addLine(String instruction, String... args) {
        this.lines.add(new SourceGraphInst(instruction, args));
    }
}
