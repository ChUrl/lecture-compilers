package codegen;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import parser.StupsParser;
import parser.ast.AST;
import parser.ast.ASTNode;
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

class CodeGeneratorTest {

    private static StupsParser parser;
    private static Grammar stupsGrammar;

    @BeforeAll
    static void init() throws IOException, URISyntaxException {
        final Path path = Paths.get(CodeGeneratorTest.class.getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        parser = StupsParser.fromGrammar(grammar);
        stupsGrammar = grammar;
    }

    private static AST lexParseProgram(String prog) {
        final Lexer lex = new StupsLexer(CharStreams.fromString(prog));

        final AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());
        tree.postprocess(stupsGrammar);

        return tree;
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

    private static String buildArithmeticProg(String expr) {
        return "class TestOutput {\n\tpublic static void main(String[] args) {\n\t\tint i = "
               + expr
               + ";\n\t\tSystem.out.println(i);\n\t}\n}";
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

    private static String buildLogicProgram(String expr) {
        return "class TestOutput {\n\tpublic static void main(String[] args) {\n\t\tboolean b = "
               + expr
               + ";\n\t\tSystem.out.println(b);\n\t}\n}";
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

    private static Stream<Arguments> compileIfElseProgramsArgs() {
        return Stream.of(
                Arguments.of("10", "i == 10", "i = 1", "i = -1", 1),
                Arguments.of("5", "i == 10", "i = 1", "i = -1", -1),
                Arguments.of("10", "i != 10", "i = 1", "i = -1", -1),
                Arguments.of("5", "i != 10", "i = 1", "i = -1", 1),
                Arguments.of("10", "i == 10", "i = 2 * 5 - 3 * 2;\ni = i + 1", "i = -1", 5),
                Arguments.of("10", "i != 10", "i = 1", "i = (-1 * 3 - 3) / 3 + 2; i = i + 1", 1),
                Arguments.of("10", "i != 10", "i = i", "i = i", 10),
                Arguments.of("10", "i != 10", "i = 1", null, 10),
                Arguments.of("10", "i == 10", "i = 1", null, 1)
        );
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
    @MethodSource("compileArithmeticProgramsArgs")
    void compileArithmeticProgramsTest(String prog, int result) {
        final String program = buildArithmeticProg(prog);
        System.out.println(program);

        final AST tree = lexParseProgram(program);
        final Map<ASTNode, String> nodeTable = TypeChecker.validate(tree);
        final CodeGenerator gen = CodeGenerator.fromAST(tree, nodeTable);
        final StringBuilder srcProg = gen.generateCode("TestOutput");

        compileJasmin(srcProg.toString());
        assertThat(Integer.parseInt(executeCompiledProgram())).isEqualTo(result);
    }

    @ParameterizedTest
    @MethodSource("compileIfElseProgramsArgs")
    void compileIfElseProgramsTest(String expr, String condition, String ifBlock, String elseBlock, int result) {
        final String program = buildIfElseProgram(expr, condition, ifBlock, elseBlock);
        System.out.println(program);

        final AST tree = lexParseProgram(program);
        final Map<ASTNode, String> nodeTable = TypeChecker.validate(tree);
        final CodeGenerator gen = CodeGenerator.fromAST(tree, nodeTable);
        final StringBuilder srcProg = gen.generateCode("TestOutput");

        compileJasmin(srcProg.toString());
        assertThat(Integer.parseInt(executeCompiledProgram())).isEqualTo(result);
    }

    @ParameterizedTest
    @MethodSource("compileLogicProgramsArgs")
    void compileLogicProgramsTest(String expr, boolean result) {
        final String program = buildLogicProgram(expr);
        System.out.println(program);

        final AST tree = lexParseProgram(program);
        final Map<ASTNode, String> nodeTable = TypeChecker.validate(tree);
        final CodeGenerator gen = CodeGenerator.fromAST(tree, nodeTable);
        final StringBuilder srcProg = gen.generateCode("TestOutput");

        compileJasmin(srcProg.toString());
        assertThat(Boolean.parseBoolean(executeCompiledProgram())).isEqualTo(result);
    }
}
