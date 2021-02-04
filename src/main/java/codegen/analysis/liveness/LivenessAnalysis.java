package codegen.analysis.liveness;

import codegen.analysis.dataflow.DataFlowGraph;
import codegen.analysis.dataflow.DataFlowNode;
import util.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Die Liveness-Analyse bestimmt die mindestanzahl an benötigten Registern für ein Programm.
 * Anhand eines {@link DataFlowGraph} wird ein {@link InterferenceGraph} erstellt.
 * Dieser wird gefärbt, jede Farbe entspricht einem Register.
 */
public final class LivenessAnalysis {

    private final InterferenceGraph interferenceGraph;

    private LivenessAnalysis(InterferenceGraph interferenceGraph) {
        this.interferenceGraph = interferenceGraph;
    }

    public static LivenessAnalysis fromDataFlowGraph(DataFlowGraph dataFlowGraph, Map<String, Integer> varMap) {
        final DataFlowGraph livenessDataFlowGraph = DataFlowGraph.copy(dataFlowGraph);

        calculateLivenessInOut(livenessDataFlowGraph);

        return new LivenessAnalysis(InterferenceGraph.fromDataFlowGraph(livenessDataFlowGraph, varMap));
    }

    private static void calculateLivenessInOut(DataFlowGraph dataFlowGraph) {
        Logger.logDebug("Calculating in/out-sets", LivenessAnalysis.class);

        boolean change;

        do {
            change = false;

            for (DataFlowNode node : dataFlowGraph) {
                if (dataFlowGraph.indexOf(node) == dataFlowGraph.size() - 1) {
                    // Skip END

                    continue;
                }

                change = change || calculateLivenessInOutNode(node);
            }
        } while (change);

        Logger.logDebug("Successfully calculated in/out-sets", LivenessAnalysis.class);
    }

    private static boolean calculateLivenessInOutNode(DataFlowNode dataFlowNode) {
        boolean change;

        for (DataFlowNode succ : dataFlowNode.getSuccessorSet()) {
            // A variable going live into the successor implies it going live out of the predecessor

            dataFlowNode.addOut(succ.getInSet());
        }

        final Collection<String> addIN = new HashSet<>(dataFlowNode.getOutSet());
        addIN.removeAll(dataFlowNode.getDefSet()); // If a variable that is live-out is defined in the node, it doesn't have to be live-in

        change = dataFlowNode.addIn(dataFlowNode.getUseSet()); // A variable being used implies it going in live
        change = change || dataFlowNode.addIn(addIN); // A variable that is live-out and isn't defined in the node must be live-in

        return change;
    }

    /**
     * Führt die Liveness-Analyse auf dem gespeicherten {@link InterferenceGraph} durch.
     * Die Registeranzahl wird durch naive Färbung des InterferenzGraphen ermittelt.
     */
    public int doLivenessAnalysis() {
        return this.colorInterferenceGraph();
    }

    private int colorInterferenceGraph() {
        Logger.logDebug("Coloring interference-graph", LivenessAnalysis.class);

        int colors = 0;
        int currentColor;

        for (InterferenceNode node : this.interferenceGraph) {

            currentColor = 1;

            // Get all colors that can't be used
            final Collection<Integer> neighbourColors = new HashSet<>();
            for (InterferenceNode neighbour : node.getNeighbourSet()) {
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

            Logger.logDebug("Successfully colored interference-graph", LivenessAnalysis.class);
        }

        Logger.logDebugSupplier(this.interferenceGraph::printToImage, LivenessAnalysis.class);

        return colors;
    }
}
