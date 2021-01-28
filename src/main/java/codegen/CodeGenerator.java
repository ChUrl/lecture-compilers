package codegen;

import codegen.sourcegraph.SourceGraph;
import codegen.sourcegraph.SourceGraphGenerator;
import parser.ast.AST;
import parser.ast.ASTNode;

import java.util.Map;

public final class CodeGenerator {

    private CodeGenerator() {}

    public static String generateCode(AST tree, Map<ASTNode, String> nodeTypeMap, String source) {
        final SourceGraphGenerator gen = SourceGraphGenerator.fromAST(tree, nodeTypeMap, source);
        final SourceGraph graph = gen.generateGraph();

        return graph.toString();
    }
}
