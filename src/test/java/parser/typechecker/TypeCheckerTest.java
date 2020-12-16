package parser.typechecker;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import parser.ParseException;
import parser.StupsParser;
import parser.ast.AST;
import parser.grammar.Grammar;
import typechecker.AssignmentTypeMismatchException;
import typechecker.OperatorTypeMismatchException;
import typechecker.OperatorUsageException;
import typechecker.TypeChecker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TypeCheckerTest {

    private final Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
    private final Grammar grammar = Grammar.fromFile(this.path);
    private final StupsParser stupsParser = StupsParser.fromGrammar(this.grammar);

    TypeCheckerTest() throws URISyntaxException, IOException {}

    private static String exprToProg(String expr) {
        return "class MyClass {\n"
               + "public static void main(String[] args) {\n"
               + expr
               + "}}";
    }

    private AST getTree(String expr) {
        final Lexer lex = new StupsLexer(CharStreams.fromString(exprToProg(expr)));
        final AST tree = this.stupsParser.parse(lex.getAllTokens(), lex.getVocabulary());
        tree.postprocess(this.grammar);

        return tree;
    }

    @ParameterizedTest
    @ValueSource(strings = {"int i = 0;",
                            "boolean b = false;",
                            "String s = \"Hi\";"})
    void testDeclCorrect(String expr) {
        TypeChecker.validate(this.getTree(expr));
    }

    @ParameterizedTest
    @ValueSource(strings = {"int i = false;",
                            "boolean b = \"String\";",
                            "String s = 5;"})
    void testDeclMismatch(String expr) {
        assertThatThrownBy(() -> TypeChecker.validate(this.getTree(expr))).isInstanceOf(AssignmentTypeMismatchException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"int i = +1;",
                            "boolean b = !false;",
                            "int i = -1;"})
    void testUnaryOperatorCorrect(String expr) {
        TypeChecker.validate(this.getTree(expr));
    }

    @ParameterizedTest
    @ValueSource(strings = {"int i = (1 + 1);",
                            "int i = 1 * 1;",
                            "int i = 1 % 1;",
                            "boolean b = 1 < (1);",
                            "boolean b = 1 <= 1;",
                            "boolean b = (1) > 1;",
                            "boolean b = (((1)) >= 1);",
                            "boolean b = 1 == 1;",
                            "boolean b = 1 != 1;",
                            "boolean b = (((true)) && ((true)));",
                            "boolean b = true || true;"})
    void testBinaryOperatorCorrect(String expr) {
        TypeChecker.validate(this.getTree(expr));
    }

    @ParameterizedTest
    @ValueSource(strings = {"int i = / 1;",
                            "int i = 1 *;",
                            "int i = % 1;",
                            "boolean b = && false;",
                            "boolean b = false ||;",
                            "boolean b = < 1;",
                            "boolean b = <= 1;",
                            "boolean b = 1 >;",
                            "boolean b = >= 1;",
                            "boolean b = 1 ==;",
                            "boolean b = 1 !=;",
                            "boolean b = !;",
                            "boolean b = <;",
                            "boolean b = true !false;"})
    void testBinaryOperatorIncorrect(String expr) {
        assertThatThrownBy(() -> TypeChecker.validate(this.getTree(expr))).isInstanceOfAny(OperatorUsageException.class,
                                                                                           ParseException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"int i = true / 1;",
                            "int i = 1 * \"Hello\";",
                            "int i = false % 1;",
                            "boolean b = 1 && (false);",
                            "boolean b = false || \"String\";",
                            "boolean b = true < 1;",
                            "boolean b = false <= 1;",
                            "boolean b = 1 > \"String\";",
                            "boolean b = \"Hey\" >= (1);",
                            "boolean b = 1 == false;",
                            "boolean b = 1 != (true);"})
    void testOperatorTypeMismatch(String expr) {
        assertThatThrownBy(() -> TypeChecker.validate(this.getTree(expr))).isInstanceOf(OperatorTypeMismatchException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"int i = 2 / 1 * (true || false);",
                            "int i = 1 * (5 - \"Hello\");",
                            "int i = 4 * (false && 1)",
                            "int i = 3 - 5 / true",
                            "int i = true - false / \"String\"",
                            "int i = \"String\" * 1",
                            "int i = -\"String\"",
                            "boolean b = !1"})
    void testFaultyExpressions(String expr) {
        assertThatThrownBy(() -> TypeChecker.validate(this.getTree(expr))).isInstanceOfAny(OperatorTypeMismatchException.class,
                                                                                           ParseException.class);
    }
}
