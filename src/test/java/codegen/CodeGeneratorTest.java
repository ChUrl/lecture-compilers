package codegen;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import parser.StupsParser;
import parser.ast.AST;
import parser.grammar.Grammar;
import typechecker.TypeChecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static String buildProg(String expr) {
        return "class TestOutput {\n\tpublic static void main(String[] args) {\n\t\tint i = " + expr + ";\n\t\tSystem.out.println(i);\n\t}\n}";
    }

    private static StupsLexer getLexer(String program) {
        return new StupsLexer(CharStreams.fromString(program));
    }

    private static void writeByteCode(String src) {
        try {
            final Path outputFile = Paths.get(System.getProperty("user.dir") + "/TestOutput.j");
            Files.writeString(outputFile, src);
        } catch (IOException e) {
            System.out.println("Datei konnte nicht geschrieben werden.");
        }
    }

    private static void compile(String src) {
        writeByteCode(src);
        final ProcessBuilder assemble = new ProcessBuilder("java", "-jar", "jasmin.jar", "TestOutput.j");
        try {
            final Process p = assemble.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Test konnte nicht von Jasmin übersetzt werden.");
        }
    }

    private static String execute() {
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

    private static AST getTree(String prog) {
        final Lexer lex = getLexer(prog);

        final AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());
        tree.postprocess(stupsGrammar);
        TypeChecker.validate(tree);

        return tree;
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
                Arguments.of("(6 % 4 + 2) * 6 / 3 * (2 + 4) - 1", 47),
                Arguments.of("24100000 / 10000", 2410),
                Arguments.of("5 - 2 * 3", -1),
                Arguments.of("5 - 2 * 3 + 6 / 2 * 3 + 1 / 1 - 5 * 8 / 2 * 1 + 7 / 6", -10), // 20
                Arguments.of("5 + 5 * 2 - (1 + 2 + 3) - (1 + 2) / 3", 8),
                Arguments.of("(((((1 + 1) * 2) * 2) + 2) / 2) - (1 - 2)", 6),
                Arguments.of("-10", -10),
                Arguments.of("+10", 10),
                Arguments.of("-1 * (10) / (5) -1 * -2 -1 * (-2) * 1 / 1 / 1 / 1 / (1) - (1)", 1)
        );
    }

    @ParameterizedTest
    @MethodSource("compileArithmeticProgramsArgs")
    void compileArithmeticPrograms(String prog, int result) {
        final AST tree = getTree(buildProg(prog));
        final StringBuilder srcProg = CodeGenerator.generateCode(tree, "TestOutput");

        compile(srcProg.toString());
        assertThat(Integer.parseInt(execute())).isEqualTo(result);
    }
}
