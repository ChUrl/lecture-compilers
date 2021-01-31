package codegen.analysis.liveness;

import codegen.analysis.dataflow.DataFlowGraph;
import codegen.analysis.dataflow.DataFlowNode;
import util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class InterferenceGraph implements Iterable<InterferenceNode> {

    private final List<InterferenceNode> interferenceNodes;

    private InterferenceGraph(List<InterferenceNode> interferenceNodes) {
        this.interferenceNodes = interferenceNodes;
    }

    public static InterferenceGraph fromDataFlowGraph(DataFlowGraph dataFlowGraph, Map<String, Integer> varMap) {
        final List<InterferenceNode> interferenceNodes = new ArrayList<>();

        // Init graph
        for (int symbol : varMap.values()) {
            interferenceNodes.add(new InterferenceNode(symbol));
        }

        final InterferenceGraph interferenceGraph = new InterferenceGraph(interferenceNodes);

        // Determine neighbours
        for (DataFlowNode node : dataFlowGraph) {

            for (String left : node.getOutSet()) {
                for (String right : node.getOutSet()) {

                    final Optional<InterferenceNode> leftNode = getNodeBySymbol(left, interferenceGraph);
                    final Optional<InterferenceNode> rightNode = getNodeBySymbol(right, interferenceGraph);

                    if (leftNode.isPresent() && rightNode.isPresent()) {
                        final boolean change = leftNode.get().addNeighbour(rightNode.get());
                        Logger.logIfTrue(change, "Added interference neighbour: " + left + " -> " + right);
                    }

                }
            }
        }

        return interferenceGraph;
    }

    private static Optional<InterferenceNode> getNodeBySymbol(String symbol, InterferenceGraph interferenceGraph) {
        return interferenceGraph.stream()
                                .filter(node -> node.getSymbol().equals(symbol))
                                .findFirst();
    }

    public Stream<InterferenceNode> stream() {
        return this.interferenceNodes.stream();
    }

    // Printing

    public String printToImage() {
        if (this.interferenceNodes.isEmpty()) {
            return "Empty Graph";
        }

        final StringBuilder dot = new StringBuilder();

        dot.append("digraph dfd {\n")
           .append("node[shape=Mrecord]\n");

        for (InterferenceNode node : this.interferenceNodes) {
            dot.append(node.getSymbol())
               .append(" [label=\"{<f0> Symbol: ")
               .append(node.getSymbol())
               .append("|<f1> Color: ")
               .append(node.getColor())
               .append("}\"];\n");
        }

        for (InterferenceNode node : this.interferenceNodes) {
            for (InterferenceNode neigh : node.getNeighbourSet()) {
                if (!dot.toString().contains(neigh.getSymbol() + " -> " + node.getSymbol())) {
                    // No double lines
                   
                    dot.append(node.getSymbol()).append(" -> ").append(neigh.getSymbol()).append(" [arrowhead=\"none\"];\n");
                }
            }
        }

        dot.append("}");

        final String dotOut = dot.toString();

        final Path dotFile = Paths.get(System.getProperty("user.dir") + "/InterferenceGraph.dot");
        try {
            Files.writeString(dotFile, dotOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ProcessBuilder dotCompile = new ProcessBuilder("dot", "-Tsvg", "-oInterferenceGraph.svg", "InterferenceGraph.dot");
        try {
            dotCompile.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Finished.";
    }

    // Overrides

    @Override
    public Iterator<InterferenceNode> iterator() {
        return this.interferenceNodes.iterator();
    }
}
