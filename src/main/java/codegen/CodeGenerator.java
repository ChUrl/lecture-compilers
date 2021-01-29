package codegen;

import codegen.flowgraph.FlowGraph;
import codegen.flowgraph.FlowGraphGenerator;
import parser.ast.AST;
import parser.ast.ASTNode;

import java.util.Map;

public final class CodeGenerator {

    private CodeGenerator() {}

    public static String generateCode(AST tree, Map<ASTNode, String> nodeTypeMap, String source) {
        final FlowGraphGenerator gen = FlowGraphGenerator.fromAST(tree, nodeTypeMap, source);
        final FlowGraph graph = gen.generateGraph();

        return graph.toString();
    }
}
