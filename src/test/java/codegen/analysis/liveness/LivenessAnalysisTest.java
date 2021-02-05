package codegen.analysis.liveness;

import codegen.analysis.dataflow.DataFlowGraph;
import codegen.flowgraph.FlowGraph;
import codegen.flowgraph.FlowGraphGenerator;
import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import parser.StupsParser;
import parser.ast.SyntaxTree;
import parser.ast.SyntaxTreeNode;
import parser.grammar.Grammar;
import typechecker.TypeChecker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LivenessAnalysisTest {

    private static StupsParser parser;
    private static Grammar stupsGrammar;

    @BeforeAll
    static void init() throws IOException, URISyntaxException {
        final Path path = Paths.get(System.getProperty("user.dir") + "/stups.grammar");
        final Grammar grammar = Grammar.fromFile(path);
        parser = StupsParser.fromGrammar(grammar);
        stupsGrammar = grammar;
    }

    private static SyntaxTree lexParseProgram(String prog) {
        final Lexer lex = new StupsLexer(CharStreams.fromString(prog));

        final SyntaxTree tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());
        final SyntaxTree ast = SyntaxTree.toAbstractSyntaxTree(tree, stupsGrammar);

        return ast;
    }

    private static LivenessAnalysis initLivenessAnalysis(String program) {
        final SyntaxTree tree = lexParseProgram(program);
        final Map<SyntaxTreeNode, String> nodeTable = TypeChecker.validate(tree);
        final FlowGraphGenerator gen = FlowGraphGenerator.fromAST(tree, nodeTable, "TestOutput");
        final FlowGraph graph = gen.generateGraph();
        final DataFlowGraph dataGraph = DataFlowGraph.fromFlowGraph(graph);

        return LivenessAnalysis.fromDataFlowGraph(dataGraph, gen.getVarMap());
    }

    private static String buildLivenessProg(String[] expr, String[] use) {
        return "class TestOutput {\n\tpublic static void main(String[] args) {\n\t\t"
               + String.join("\n\t\t", expr) + "\n"
               + Arrays.stream(use)
                       .map(string -> "\n\t\tSystem.out.println(" + string + ");")
                       .collect(Collectors.joining())
               + "\n\t}\n}";
    }

    private static Stream<Arguments> compileLivenessProgramsArgs() {
        return Stream.of(
                Arguments.of(1, new String[]{"int i = 5;"}, new String[]{"i"}), // 1
                Arguments.of(1, new String[]{"int i = 5;",
                                             "int j = 6;",
                                             "int k = 7;"}, new String[]{"i"}),
                Arguments.of(3, new String[]{"int i = 5;",
                                             "int j = 6;",
                                             "int k = 7;"}, new String[]{"i", "j", "k"}),
                Arguments.of(2, new String[]{"int i = 5;",
                                             "int j = 6;",
                                             "int k = i + j;"}, new String[]{"k"}),
                Arguments.of(2, new String[]{"int i = 5;", // 5
                                             "int j = 6;",
                                             "int k = i + j;"}, new String[]{"j"}),
                Arguments.of(1, new String[]{"int i = 5;",
                                             "i = i * 2;"}, new String[]{"i"}),
                Arguments.of(1, new String[]{"int i = 5;"}, new String[]{}),
                Arguments.of(2, new String[]{"int i = 5;",
                                             "int j = 5;",
                                             "System.out.println(i + j);",
                                             "int k = 6;",
                                             "int l = 6;"}, new String[]{"k + l"}),
                Arguments.of(4, new String[]{"int i = 5;",
                                             "int j = 5;",
                                             "int k = 6;",
                                             "int l = 6;"}, new String[]{"i + j", "k + l"}),
                Arguments.of(0, new String[]{}, new String[]{"5 * 6 - 2"}), // 10
                Arguments.of(1, new String[]{"int i = 5;",
                                             "i = 2 * i + 1;",
                                             "int j = i + 1;"}, new String[]{"j"}),
                Arguments.of(2, new String[]{"int i = 5;",
                                             "i = 2 * i + 1;",
                                             "int j = i + 1;"}, new String[]{"i", "j"}),
                Arguments.of(1, new String[]{"int i = 5;",
                                             "i = 2 * i + 1;",
                                             "int j = i + 1;",
                                             "System.out.println(j);",
                                             "i = 5 * 2;"}, new String[]{"i"}),
                Arguments.of(2, new String[]{"int i = 5;",
                                             "i = 2 * i + 1;",
                                             "int j = i + 1;",
                                             "System.out.println(j);",
                                             "i = 5 * i;"}, new String[]{"i"})
        );
    }

    @ParameterizedTest
    @MethodSource("compileLivenessProgramsArgs")
    void compileLivenessProgramsTest(int result, String[] prog, String[] use) {
        final String program = buildLivenessProg(prog, use);
        System.out.println(program);

        final LivenessAnalysis liveness = initLivenessAnalysis(program);

        assertThat(liveness.doLivenessAnalysis()).isEqualTo(result);
    }
}
