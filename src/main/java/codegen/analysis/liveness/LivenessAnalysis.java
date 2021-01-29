package codegen.analysis.liveness;

import codegen.analysis.dataflow.DataFlowGraph;
import codegen.analysis.dataflow.DataFlowNode;
import util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: This is just terrible
public final class LivenessAnalysis {

    private LivenessAnalysis() {}

    public static void doLivenessAnalysis(DataFlowGraph graph, Map<String, Integer> varMap) {

        // Calculate IN, OUT
        boolean change;
        do {
            change = false;

            for (DataFlowNode node : graph.getNodes()) {
                if (graph.getNodes().indexOf(node) == graph.getNodes().size() - 1) {
                    // Skip END

                    continue;
                }

                change = change || updateInOut(node);
            }
        } while (change);

        Logger.log("IN, OUT Sets:");
        for (DataFlowNode node : graph.getNodes()) {
            Logger.log(graph.getNodes().indexOf(node) + ": " + node.getInst() + " IN: " + node.getIn());
            Logger.log(graph.getNodes().indexOf(node) + ": " + node.getInst() + " OUT: " + node.getOut());
        }
        Logger.log("\n");

        doInterference(graph.getNodes(), varMap);
    }

    private static boolean updateInOut(DataFlowNode node) {
        boolean change;

        for (DataFlowNode succ : node.getSuccessors()) {
            node.addOut(succ.getIn());
        }

        final Set<String> addIN = new HashSet<>(node.getOut()); // Copy important
        addIN.removeAll(node.getDef());

        change = node.addIn(node.getUse());
        change = change || node.addIn(addIN);

        return change;
    }

    private static void doInterference(List<DataFlowNode> nodes, Map<String, Integer> varMap) {
        final List<InterferenceNode> interferenceGraph = new ArrayList<>();

        // Init graph
        for (Map.Entry<String, Integer> var : varMap.entrySet()) {
            interferenceGraph.add(new InterferenceNode(var.getValue().toString()));
        }
        System.out.println("Interference nodes: " + interferenceGraph);

        // Determine neighbours
        for (DataFlowNode node : nodes) {
            Logger.log("NODE " + node.getInst() + " - OUT: " + node.getOut());
            for (String left : node.getOut()) {
                if (left.isBlank()) {
                    continue;
                }

                for (String right : node.getOut()) {
                    if (right.isBlank()) {
                        continue;
                    }

                    Logger.log("LEFT: " + left + ", RIGHT: " + right);
                    getNodeBySymbol(left, interferenceGraph).addNeighbour(getNodeBySymbol(right, interferenceGraph));
                    Logger.log("Add interference neighbour: " + left + ": " + right);
                    Logger.log("Neighbours of " + left + ": " + getNodeBySymbol(left, interferenceGraph).getNeighbours());
                }
            }
        }

        // Color graph
        int colors = 0;
        int currentColor;
        Logger.log("\n");
        for (InterferenceNode node : interferenceGraph) {
            currentColor = 1;
            final Set<Integer> neighbourColors = new HashSet<>();
            Logger.log("Interference node " + node + ", Color = " + node.getColor() + ": Neighbours - "
                       + node.getNeighbours() + ", NColors - " + neighbourColors);
            for (InterferenceNode neighbour : node.getNeighbours()) {
                neighbourColors.add(neighbour.getColor());
            }

            while (neighbourColors.contains(currentColor)) {
                currentColor++;
            }

            node.setColor(currentColor);

            if (currentColor > colors) {
                colors = currentColor;
            }
        }

        Logger.call(() -> printInterferenceGraphToPicture(interferenceGraph));

        System.out.println("\nLiveness: " + colors + " register nötig.");
    }

    // Printing

    private static String printInterferenceGraphToPicture(List<InterferenceNode> graph) {
        final StringBuilder dot = new StringBuilder();

        dot.append("digraph dfd {\n")
           .append("node[shape=Mrecord]\n");

        for (InterferenceNode node : graph) {
            dot.append(node.getSymbol())
               .append(" [label=\"{<f0> Symbol: ")
               .append(node.getSymbol())
               .append("|<f1> Color: ")
               .append(node.getColor())
               .append("}\"];\n");
        }

        for (InterferenceNode node : graph) {
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

    private static InterferenceNode getNodeBySymbol(String symbol, List<InterferenceNode> interferenceGraph) {
        return interferenceGraph.stream()
                                .filter(node -> node.getSymbol().equals(symbol))
                                .findFirst()
                                .orElse(null);
    }

}
