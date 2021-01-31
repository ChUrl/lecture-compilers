package codegen;

import codegen.flowgraph.FlowGraph;
import codegen.flowgraph.FlowGraphGenerator;
import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import parser.StupsParser;
import parser.ast.SyntaxTree;
import parser.ast.SyntaxTreeNode;
import parser.grammar.Grammar;
import typechecker.TypeChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlowGraphGeneratorTest {

    private static StupsParser parser;
    private static Grammar stupsGrammar;

    @BeforeAll
    static void init() throws IOException, URISyntaxException {
        final Path path = Paths.get(FlowGraphGeneratorTest.class.getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        parser = StupsParser.fromGrammar(grammar);
        stupsGrammar = grammar;
    }

    private static String readProgram(String prog) {
        try {
            final Path progPath = Paths.get(FlowGraphGeneratorTest.class.getClassLoader().getResource("examplePrograms/" + prog).toURI());
            return Files.readString(progPath);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static SyntaxTree lexParseProgram(String prog) {
        final Lexer lex = new StupsLexer(CharStreams.fromString(prog));

        final SyntaxTree tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());
        final SyntaxTree ast = SyntaxTree.toAbstractSyntaxTree(tree, stupsGrammar);

        return ast;
    }

    private static void codegenToFile(String src) {
        try {
            final Path outputFile = Paths.get(System.getProperty("user.dir") + "/TestOutput.j");
            Files.writeString(outputFile, src);
        } catch (IOException e) {
            System.out.println("Datei konnte nicht geschrieben werden.");
        }
    }

    private static void compileJasmin(String src) {
        codegenToFile(src);
        final ProcessBuilder assemble = new ProcessBuilder("java", "-jar", "jasmin.jar", "TestOutput.j");
        try {
            final Process p = assemble.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Test konnte nicht von Jasmin übersetzt werden.");
        }
    }

    private static String executeCompiledProgram() {
        final ProcessBuilder execute = new ProcessBuilder("java", "TestOutput");
        StringBuilder out = null;

        try {
            final Process run = execute.start();
            final BufferedReader r = new BufferedReader(new InputStreamReader(run.getInputStream()));
            out = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                out.append("\n").append(line);
            }
            run.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return out.toString().replaceFirst("\n", "");
    }

    // Arithmetic programs

    private static String buildArithmeticProg(String expr) {
        return "class TestOutput {\n\tpublic static void main(String[] args) {\n\t\tint i = "
               + expr
               + ";\n\t\tSystem.out.println(i);\n\t}\n}";
    }

    private static Stream<Arguments> compileArithmeticProgramsArgs() {
        return Stream.of(
                Arguments.of("1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10", 55), // 1
                Arguments.of("1 + 2 * 3", 7),
                Arguments.of("1 + 2 * 3 + 2 * 3", 13),
                Arguments.of("1 - 1 + 1 - 1 / 1 * 1", 0),
                Arguments.of("2 * (2 + 3)", 10), // 5
                Arguments.of("2 * 2 + 3", 7),
                Arguments.of("2 * ((1 + 2) * (2 + 4) - 1)", 34),
                Arguments.of("6 / 3", 2),
                Arguments.of("6 - 6 / 3", 4),
                Arguments.of("3 / 6", 0), // 10
                Arguments.of("6 / 6 * 6 + 6 - 6", 6),
                Arguments.of("6 % 5", 1),
                Arguments.of("6 % 6", 0),
                Arguments.of("6 % 8", 6),
                Arguments.of("6 % 2", 0), // 15
                Arguments.of("6 % 4", 2),
                Arguments.of("123456789 - 123456780", 9),
                Arguments.of("(6 % 4 + 2) * 6 / 3 * (2 + 4) - 1", 47),
                Arguments.of("5 - 2 * 3", -1),
                Arguments.of("5 - 2 * 3 + 6 / 2 * 3 + 1 / 1 - 5 * 8 / 2 * 1 + 7 / 6", -10), // 20
                Arguments.of("5 + 5 * 2 - (1 + 2 + 3) - (1 + 2) / 3", 8),
                Arguments.of("(((((1 + 1) * 2) * 2) + 2) / 2) - (1 - 2)", 6),
                Arguments.of("-10", -10),
                Arguments.of("+10", 10),
                Arguments.of("-1 * (10) / (5) -1 * -2 -1 * (-2) * 1 / 1 / 1 / 1 / (1) - (1)", 1)
        );
    }

    private static String buildIfElseProgram(String expr, String condition, String ifBlock, String elseBlock) {
        String elseBlc = "else {\n\t\t\t" + elseBlock + ";\n\t\t}";
        if (elseBlock == null) {
            elseBlc = "";
        }

        return "class TestOutput {\n\tpublic static void main(String[] args) {\n\t\tint i = "
               + expr
               + ";\n\t\tif ("
               + condition
               + ") {\n\t\t\t"
               + ifBlock
               + ";\n\t\t} " + elseBlc + "\n\t\tSystem.out.println(i);\n\t}\n}";
    }

    // Conditional programs

    private static String buildLoopProgram(String expr, String condition, String body) {
        return "class TestOutput {\n\tpublic static void main(String[] args) {\n\t\tint i = "
               + expr
               + ";\n\t\twhile ("
               + condition
               + ") {\n\t\t\t"
               + body
               + ";\n\t\t}\n\t\tSystem.out.println(i);\n\t}\n}";
    }

    private static Stream<Arguments> compileIfElseProgramsArgs() {
        return Stream.of(
                Arguments.of("10", "i == 10", "i = 1", "i = -1", 1), // 1
                Arguments.of("5", "i == 10", "i = 1", "i = -1", -1),
                Arguments.of("10", "i != 10", "i = 1", "i = -1", -1),
                Arguments.of("5", "i != 10", "i = 1", "i = -1", 1),
                Arguments.of("10", "i == 10", "i = 2 * 5 - 3 * 2;\ni = i + 1", "i = -1", 5), // 5
                Arguments.of("10", "i != 10", "i = 1", "i = (-1 * 3 - 3) / 3 + 2; i = i + 1", 1),
                Arguments.of("10", "i != 10", "i = i", "i = i", 10),
                Arguments.of("10", "i != 10", "i = 1", null, 10),
                Arguments.of("10", "i == 10", "i = 1", null, 1)
        );
    }

    public static Stream<Arguments> compileLoopProgramsArgs() {
        return Stream.of(
                Arguments.of("0", "i <= 5", "System.out.println(i); i = i + 1", "0\n1\n2\n3\n4\n5\n6"),
                Arguments.of("5 - 9", "i != 0", "System.out.println(i); i = i + 1", "-4\n-3\n-2\n-1\n0"),
                Arguments.of("0", "i < 0", "System.out.println(i); i = i + 1", "0"),
                Arguments.of("2", "i <= 5", "System.out.println(i); i = i * i", "2\n4\n16")
        );
    }

    // Loop programs

    private static String buildLogicProgram(String expr) {
        return "class TestOutput {\n\tpublic static void main(String[] args) {\n\t\tboolean b = "
               + expr
               + ";\n\t\tSystem.out.println(b);\n\t}\n}";
    }

    public static Stream<Arguments> compileProgramsArgs() {
        return Stream.of(
                Arguments.of("EmptyMain.stups", ""), // 1
                Arguments.of("GeneralComment.stups", "Test"),
                Arguments.of("GeneralIfElse.stups", "x ist kleiner als y.\nx und y sind gleich gross."),
                Arguments.of("Println.stups", "Hey\ntrue\n5\n1\nHey\nfalse"),
                Arguments.of("CompileAllInOne1.stups", "0\nfalse\nELSE"), // 5
                Arguments.of("Fibonacci.stups", "1\n2\n3\n5\n8\n13\n21\n34"),
                Arguments.of("Factorial.stups", "1\n2\n6\n24\n120"),
                Arguments.of("Squares.stups", "1\n4\n9\n16\n25\n36\n49\n64\n81\n100"),
                Arguments.of("Multiplication.stups", "5\n10\n15\n20")
        );
    }

    @ParameterizedTest
    @MethodSource("compileArithmeticProgramsArgs")
    void compileArithmeticProgramsTest(String prog, int result) {
        final String program = buildArithmeticProg(prog);
        System.out.println(program);

        final SyntaxTree tree = lexParseProgram(program);
        final Map<SyntaxTreeNode, String> nodeTable = TypeChecker.validate(tree);
        final FlowGraphGenerator gen = FlowGraphGenerator.fromAST(tree, nodeTable, "TestOutput");
        final FlowGraph srcProg = gen.generateGraph();

        compileJasmin(srcProg.toString());
        assertThat(Integer.parseInt(executeCompiledProgram())).isEqualTo(result);
    }

    // Logic Programs

    @ParameterizedTest
    @MethodSource("compileIfElseProgramsArgs")
    void compileIfElseProgramsTest(String expr, String condition, String ifBlock, String elseBlock, int result) {
        final String program = buildIfElseProgram(expr, condition, ifBlock, elseBlock);
        System.out.println(program);

        final SyntaxTree tree = lexParseProgram(program);
        final Map<SyntaxTreeNode, String> nodeTable = TypeChecker.validate(tree);
        final FlowGraphGenerator gen = FlowGraphGenerator.fromAST(tree, nodeTable, "TestOutput");
        final FlowGraph srcProg = gen.generateGraph();

        compileJasmin(srcProg.toString());
        assertThat(Integer.parseInt(executeCompiledProgram())).isEqualTo(result);
    }

    private static Stream<Arguments> compileLogicProgramsArgs() {
        return Stream.of(
                Arguments.of("true || false", true), // 1
                Arguments.of("false || false", false),
                Arguments.of("true && false", false),
                Arguments.of("!(true && false)", true),
                Arguments.of("true && !false", true), // 5
                Arguments.of("(5 < 4) || false", false),
                Arguments.of("5 > 4 && 4 < 5", true),
                Arguments.of("5 == 5 && !(5 != 5)", true),
                Arguments.of("true == true", true),
                Arguments.of("false == false", true), // 10
                Arguments.of("(1 < 2) == (3 < 4)", true),
                Arguments.of("1 >= 1", true),
                Arguments.of("true && 5 < 6", true),
                Arguments.of("!true", false),
                Arguments.of("!false", true), // 15
                Arguments.of("true && (true || false)", true),
                Arguments.of("false || true && false", false),
                Arguments.of("(((5 < 4) == false) == true) == true", true),
                Arguments.of("(false == false) && (true == !false) && true", true),
                Arguments.of("true && true && true && false", false), // 20
                Arguments.of("false || false || false || true", true),
                Arguments.of("true && false || false && true || (5 < 6 == false)", false),
                Arguments.of("false || 5 < 6 == false", false)
        );
    }

    @ParameterizedTest
    @MethodSource("compileLogicProgramsArgs")
    void compileLogicProgramsTest(String expr, boolean result) {
        final String program = buildLogicProgram(expr);
        System.out.println(program);

        final SyntaxTree tree = lexParseProgram(program);
        final Map<SyntaxTreeNode, String> nodeTable = TypeChecker.validate(tree);
        final FlowGraphGenerator gen = FlowGraphGenerator.fromAST(tree, nodeTable, "TestOutput");
        final FlowGraph srcProg = gen.generateGraph();

        compileJasmin(srcProg.toString());
        assertThat(Boolean.parseBoolean(executeCompiledProgram())).isEqualTo(result);
    }

    // General programs

    @ParameterizedTest
    @MethodSource("compileLoopProgramsArgs")
    void compileLoopProgramsTest(String expr, String condition, String body, String result) {
        final String program = buildLoopProgram(expr, condition, body);
        System.out.println(program);

        final SyntaxTree tree = lexParseProgram(program);
        final Map<SyntaxTreeNode, String> nodeTable = TypeChecker.validate(tree);
        final FlowGraphGenerator gen = FlowGraphGenerator.fromAST(tree, nodeTable, "TestOutput");
        final FlowGraph srcProg = gen.generateGraph();

        compileJasmin(srcProg.toString());
        assertThat(executeCompiledProgram()).isEqualTo(result);
    }

    @ParameterizedTest
    @MethodSource("compileProgramsArgs")
    void compileProgramsTest(String prog, String result) {
        final String program = readProgram(prog);
        System.out.print(program);

        final SyntaxTree tree = lexParseProgram(program);
        final Map<SyntaxTreeNode, String> nodeTable = TypeChecker.validate(tree);
        final FlowGraphGenerator gen = FlowGraphGenerator.fromAST(tree, nodeTable, "TestOutput");
        final FlowGraph srcProg = gen.generateGraph();

        compileJasmin(srcProg.toString());
        assertThat(executeCompiledProgram()).isEqualTo(result);
    }

    @Test
    void compileEmptyProgramTest() {
        final String program = readProgram("EmptyFile.stups");

        final SyntaxTree tree = lexParseProgram(program);
        final Map<SyntaxTreeNode, String> nodeTable = TypeChecker.validate(tree);

        assertThatThrownBy(() -> FlowGraphGenerator.fromAST(tree, nodeTable, "TestOutput"))
                .isInstanceOf(CodeGenerationException.class);
    }
}
