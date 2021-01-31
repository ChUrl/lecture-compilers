package codegen.analysis.dataflow;

import codegen.flowgraph.FlowBasicBlock;
import codegen.flowgraph.FlowGraph;
import codegen.flowgraph.FlowInstruction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Die Instruktionen repräsentiert durch einen Graphen.
 */
public final class DataFlowGraph implements Iterable<DataFlowNode> {

    // List for easy indexing
    private final List<DataFlowNode> dataFlowNodes;

    private DataFlowGraph(List<DataFlowNode> dataFlowNodes) {
        this.dataFlowNodes = Collections.unmodifiableList(dataFlowNodes);
    }

    public static DataFlowGraph fromFlowGraph(FlowGraph flowGraph) {
        final List<DataFlowNode> dataFlowNodes = new LinkedList<>();

        // Initialize all DataFlowNodes
        for (FlowBasicBlock basicBlock : flowGraph) {
            for (FlowInstruction instruction : basicBlock) {
                dataFlowNodes.add(DataFlowNode.fromFlowNode(instruction));
            }
        }

        final DataFlowGraph dataFlowGraph = new DataFlowGraph(dataFlowNodes);
        initNodePosition(flowGraph, dataFlowGraph);

        return dataFlowGraph;
    }

    /**
     * Jeder {@link DataFlowNode} im {@link DataFlowGraph} wird anhand des {@link FlowGraph} positioniert.
     * Dabei werden für den Node die Predecessors und Successors gesetzt.
     */
    private static void initNodePosition(FlowGraph flowGraph, DataFlowGraph dataFlowGraph) {
        for (FlowBasicBlock basicBlock : flowGraph) {
            for (FlowInstruction instruction : basicBlock) {

                final Optional<DataFlowNode> currentNode = getNodeByInstructionId(instruction, dataFlowGraph);

                if (currentNode.isEmpty()) {
                    continue;
                }

                for (FlowInstruction predecessor : basicBlock.getInstructionPredecessorSet(instruction)) {
                    final Optional<DataFlowNode> currentPredecessor = getNodeByInstructionId(predecessor, dataFlowGraph);
                    currentPredecessor.ifPresent(dataFlowNode -> currentNode.get().addPredecessor(dataFlowNode));
                }

                for (FlowInstruction successor : basicBlock.getInstructionSuccessorSet(instruction)) {
                    final Optional<DataFlowNode> currentSuccessor = getNodeByInstructionId(successor, dataFlowGraph);
                    currentSuccessor.ifPresent(dataFlowNode -> currentNode.get().addSuccessor(dataFlowNode));
                }
            }
        }
    }

    private static Optional<DataFlowNode> getNodeByInstructionId(FlowInstruction instruction, DataFlowGraph dataFlowGraph) {
        return dataFlowGraph.stream()
                            .filter(node -> node.getId().equals(instruction.getId()))
                            .findFirst();
    }

    public int indexOf(DataFlowNode node) {
        return this.dataFlowNodes.indexOf(node);
    }

    public int size() {
        return this.dataFlowNodes.size();
    }

    private Stream<DataFlowNode> stream() {
        return this.dataFlowNodes.stream();
    }

    // Printing

    public String printToImage() {
        if (this.dataFlowNodes.isEmpty()) {
            return "Empty Graph";
        }

        final StringBuilder dot = new StringBuilder();

        dot.append("digraph dfd {\n")
           .append("node[shape=Mrecord]\n");

        for (DataFlowNode node : this.dataFlowNodes) {
            dot.append(node.getId())
               .append(" [label=\"{<f0> ")
               .append(this.dataFlowNodes.indexOf(node))
               .append("|<f1> ")
               .append(node.getInst())
               .append("}\"];\n");
        }

        dot.append("START[label=\"START\"];\n")
           .append("END[label=\"END\"];\n");

        dot.append("START -> ").append(this.dataFlowNodes.get(0).getId()).append(";\n");
        dot.append(this.dataFlowNodes.get(this.dataFlowNodes.size() - 1).getId()).append(" -> END;\n");

        for (DataFlowNode node : this.dataFlowNodes) {
            for (DataFlowNode successor : node.getSuccessorSet()) {

                dot.append(node.getId()).append(" -> ").append(successor.getId()).append(";\n");
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

    // Overrides

    @Override
    public Iterator<DataFlowNode> iterator() {
        return this.dataFlowNodes.iterator();
    }
}
