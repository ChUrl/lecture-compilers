package codegen.analysis.liveness;

import codegen.analysis.dataflow.DataFlowGraph;
import codegen.analysis.dataflow.DataFlowNode;
import util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class InterferenceGraph {

    private final List<InterferenceNode> nodes;

    private InterferenceGraph(List<InterferenceNode> nodes) {
        this.nodes = nodes;
    }

    public static InterferenceGraph fromDataFlowGraph(DataFlowGraph graph, Map<String, Integer> varMap) {
        final List<InterferenceNode> interferenceGraph = new ArrayList<>();

        // Init graph
        for (Map.Entry<String, Integer> var : varMap.entrySet()) {
            interferenceGraph.add(new InterferenceNode(var.getValue().toString()));
        }

        // Determine neighbours
        for (DataFlowNode node : graph.getNodes()) {
            Logger.log("NODE " + node.getInst() + " - OUT: " + node.getOut());

            for (String left : node.getOut()) {
                if (left.isBlank()) {
                    continue;
                }

                for (String right : node.getOut()) {
                    if (right.isBlank()) {
                        continue;
                    }

                    getNodeBySymbol(left, interferenceGraph).addNeighbour(getNodeBySymbol(right, interferenceGraph));
                    Logger.log("Add interference neighbour: " + left + " <-> " + right);
                }
            }
        }

        return new InterferenceGraph(interferenceGraph);
    }

    private static InterferenceNode getNodeBySymbol(String symbol, List<InterferenceNode> interferenceGraph) {
        return interferenceGraph.stream()
                                .filter(node -> node.getSymbol().equals(symbol))
                                .findFirst()
                                .orElse(null);
    }

    // Getters, Setters

    public List<InterferenceNode> getNodes() {
        return this.nodes;
    }

    // Printing

    public String printToImage() {
        if (this.nodes.isEmpty()) {
            return "Empty Graph";
        }

        final StringBuilder dot = new StringBuilder();

        dot.append("digraph dfd {\n")
           .append("node[shape=Mrecord]\n");

        for (InterferenceNode node : this.nodes) {
            dot.append(node.getSymbol())
               .append(" [label=\"{<f0> Symbol: ")
               .append(node.getSymbol())
               .append("|<f1> Color: ")
               .append(node.getColor())
               .append("}\"];\n");
        }

        for (InterferenceNode node : this.nodes) {
            for (InterferenceNode neigh : node.getNeighbours()) {
                if (!dot.toString().contains(neigh.getSymbol() + " -> " + node.getSymbol())) { // No double lines
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
}
