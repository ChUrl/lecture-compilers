package codegen;

import codegen.flowgraph.FlowGraph;
import codegen.flowgraph.FlowGraphGenerator;
import parser.ast.SyntaxTree;
import parser.ast.SyntaxTreeNode;

import java.util.Map;

public final class CodeGenerator {

    private CodeGenerator() {}

    public static String generateCode(SyntaxTree tree, Map<SyntaxTreeNode, String> nodeTypeMap, String source) {
        final FlowGraphGenerator gen = FlowGraphGenerator.fromAST(tree, nodeTypeMap, source);
        final FlowGraph graph = gen.generateGraph();

        return graph.toString();
    }
}
