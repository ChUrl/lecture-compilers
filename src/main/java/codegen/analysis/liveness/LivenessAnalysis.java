package codegen.analysis.liveness;

import codegen.analysis.dataflow.DataFlowGraph;
import codegen.analysis.dataflow.DataFlowNode;
import util.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LivenessAnalysis {

    private final InterferenceGraph interferenceGraph;

    private LivenessAnalysis(InterferenceGraph interferenceGraph) {
        this.interferenceGraph = interferenceGraph;
    }

    public static LivenessAnalysis fromDataFlowGraph(DataFlowGraph graph, Map<String, Integer> varMap) {
        calculateLivenessInOut(graph); // TODO: Copy the graph

        return new LivenessAnalysis(InterferenceGraph.fromDataFlowGraph(graph, varMap));
    }

    private static void calculateLivenessInOut(DataFlowGraph graph) {
        boolean change;

        do {
            change = false;

            for (DataFlowNode node : graph) {
                // TODO: Indexof mega unnötig
                if (graph.indexOf(node) == graph.size() - 1) {
                    // Skip END

                    continue;
                }

                change = change || updateInOut(node);
            }
        } while (change);

        // TODO: Indexof mega unnötig
        Logger.log("IN, OUT Sets:");
        for (DataFlowNode node : graph) {
            Logger.log(graph.indexOf(node) + ": " + node.getInst() + " IN: " + node.getIn());
            Logger.log(graph.indexOf(node) + ": " + node.getInst() + " OUT: " + node.getOut());
        }
        Logger.log("\n");
    }

    private static boolean updateInOut(DataFlowNode node) {
        boolean change;

        for (DataFlowNode succ : node.getSuccessors()) {
            // A variable going live into the successor implies it going live out of the predecessor

            node.addOut(succ.getIn());
        }

        final Set<String> addIN = new HashSet<>(node.getOut()); // Copy important
        addIN.removeAll(node.getDef()); // If a variable that is live-out is defined in the node, it doesn't have to be live-in

        change = node.addIn(node.getUse()); // A variable being used implies it going in live
        change = change || node.addIn(addIN); // A variable that is live-out and isn't defined in the node must be live-in

        return change;
    }

    public int doLivenessAnalysis() {
        final int registers = this.colorInterferenceGraph();

        System.out.println("\nRegisters: " + registers);

        return registers;
    }

    private int colorInterferenceGraph() {
        Logger.log("Coloring Interference Graph\n");

        int colors = 0;
        int currentColor;

        for (InterferenceNode node : this.interferenceGraph) {

            currentColor = 1;

            // Get all colors that can't be used
            final Set<Integer> neighbourColors = new HashSet<>();
            for (InterferenceNode neighbour : node.getNeighbours()) {
                neighbourColors.add(neighbour.getColor());
            }

            // Find a color that can be used
            while (neighbourColors.contains(currentColor)) {
                currentColor++;
            }

            node.setColor(currentColor);

            if (currentColor > colors) {
                colors = currentColor;
            }
        }

        Logger.call(this.interferenceGraph::printToImage);

        return colors;
    }
}
