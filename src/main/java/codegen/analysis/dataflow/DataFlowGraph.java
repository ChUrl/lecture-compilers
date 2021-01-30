package codegen.analysis.dataflow;

import codegen.flowgraph.FlowBasicBlock;
import codegen.flowgraph.FlowGraph;
import codegen.flowgraph.FlowInstruction;
import util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class DataFlowGraph {

    private final List<DataFlowNode> graph;

    private DataFlowGraph(List<DataFlowNode> graph) {
        this.graph = Collections.unmodifiableList(graph);
        Logger.call(this::printToImage);
    }

    public static DataFlowGraph fromSourceGraph(FlowGraph srcGraph) {
        final List<DataFlowNode> graph = new LinkedList<>();

        for (FlowBasicBlock block : srcGraph.getBasicBlocks()) {
            for (FlowInstruction inst : block.getInstructions()) {
                graph.add(DataFlowNode.fromFlowNode(inst, block));
            }
        }

        initSuccPred(srcGraph, graph);

        return new DataFlowGraph(graph);
    }

    private static void initSuccPred(FlowGraph srcGraph, List<DataFlowNode> graph) {
        for (FlowBasicBlock block : srcGraph.getBasicBlocks()) {
            for (FlowInstruction inst : block.getInstructions()) {
                final DataFlowNode current = getNodeByInstruction(inst, graph);

                for (FlowInstruction pred : block.getPredecessors(inst)) {
                    final DataFlowNode currentPred = getNodeByInstruction(pred, graph);

                    current.addPredecessor(currentPred);
                    currentPred.addSuccessor(current);
                }

                for (FlowInstruction succ : block.getSuccessors(inst)) {
                    final DataFlowNode currentSucc = getNodeByInstruction(succ, graph);

                    Logger.log("INST: " + current.getInst() + ", SUCC: " + currentSucc.getInst());
                    current.addSuccessor(currentSucc);
                    currentSucc.addPredecessor(current);
                }
            }
        }
    }

    private static DataFlowNode getNodeByInstruction(FlowInstruction inst, List<DataFlowNode> graph) {
        return graph.stream()
                    .filter(node -> node.getId().equals(inst.getId()))
                    .findFirst()
                    .orElse(null);
    }

    public List<DataFlowNode> getNodes() {
        return this.graph;
    }

    // Printing

    public String printToImage() {
        final StringBuilder dot = new StringBuilder();

        dot.append("digraph dfd {\n")
           .append("node[shape=Mrecord]\n");

        for (DataFlowNode node : this.graph) {
            dot.append(node.getId())
               .append(" [label=\"{<f0> ")
               .append(this.graph.indexOf(node))
               .append("|<f1> ")
               .append(node.getInst())
               .append("}\"];\n");
        }

        dot.append("START[label=\"START\"];\n")
           .append("END[label=\"END\"];\n");

        dot.append("START -> ").append(this.graph.get(0).getId()).append(";\n");
        dot.append(this.graph.get(this.graph.size() - 1).getId()).append(" -> END;\n");

        for (DataFlowNode node : this.graph) {
            // Successors
            for (DataFlowNode succ : node.getSuccessors()) {
                if (!dot.toString().contains(node.getId() + " -> " + succ.getId())) {
                    // No duplicate arrows

                    dot.append(node.getId()).append(" -> ").append(succ.getId()).append(";\n");
                }
            }

            // Predecessors
            for (DataFlowNode pred : node.getPredecessors()) {
                if (!dot.toString().contains(pred.getId() + " -> " + node.getId())) {
                    // No duplicates

                    dot.append(pred.getId()).append(" -> ").append(node.getId()).append(";\n");
                }
            }
        }

        dot.append("}");

        final String dotOut = dot.toString();

        final Path dotFile = Paths.get(System.getProperty("user.dir") + "/DataFlowGraph.dot");
        try {
            Files.writeString(dotFile, dotOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ProcessBuilder dotCompile = new ProcessBuilder("dot", "-Tsvg", "-oDataFlowGraph.svg", "DataFlowGraph.dot");
        try {
            dotCompile.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Finished.";
    }
}
