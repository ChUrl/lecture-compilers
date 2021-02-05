package parser.typechecker;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import parser.StupsParser;
import parser.ast.SyntaxTree;
import parser.grammar.Grammar;
import typechecker.SymbolAlreadyDefinedException;
import typechecker.TypeTable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TypeTableTest {

    private static Grammar grammar;
    private static StupsParser parser;

    @BeforeAll
    static void init() throws IOException, URISyntaxException {
        final Path path = Paths.get(System.getProperty("user.dir") + "/stups.grammar");
        grammar = Grammar.fromFile(path);
        parser = StupsParser.fromGrammar(grammar);
    }

    private static SyntaxTree getTree(String program) {
        try {
            final Path path = Paths.get(TypeTableTest.class.getClassLoader().getResource("examplePrograms/" + program).toURI());
            final String programCode = Files.readString(path, StandardCharsets.US_ASCII);
            final Lexer lex = new StupsLexer(CharStreams.fromString(programCode));
            final SyntaxTree tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());
            final SyntaxTree ast = SyntaxTree.toAbstractSyntaxTree(tree, grammar);
            return ast;
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        return null;
    }

    @Test
    void testSingleSymbol() {
        final SyntaxTree tree = getTree("SingleSymbol.stups");

        final TypeTable table = TypeTable.fromAST(tree);

        assertThat(table.getSymbolType("i")).isEqualTo("INTEGER_TYPE");
        assertThat(table.getSymbolCount()).isEqualTo(1);
    }

    @Test
    void testMultipleSymbol() {
        final SyntaxTree tree = getTree("MultipleSymbol.stups");

        final TypeTable table = TypeTable.fromAST(tree);

        assertThat(table.getSymbolType("i")).isEqualTo("INTEGER_TYPE");
        assertThat(table.getSymbolType("ii")).isEqualTo("INTEGER_TYPE");
        assertThat(table.getSymbolType("b")).isEqualTo("BOOLEAN_TYPE");
        assertThat(table.getSymbolType("bb")).isEqualTo("BOOLEAN_TYPE");
        assertThat(table.getSymbolType("s")).isEqualTo("STRING_TYPE");
        assertThat(table.getSymbolType("ss")).isEqualTo("STRING_TYPE");
        assertThat(table.getSymbolCount()).isEqualTo(6);
    }

    @Test
    void testExistingSymbol() {
        final SyntaxTree tree = getTree("ExistingSymbol.stups");

        assertThatThrownBy(() -> TypeTable.fromAST(tree)).isInstanceOf(SymbolAlreadyDefinedException.class);
    }

    @Test
    void testExistingSymbol2() {
        final SyntaxTree tree = getTree("ExistingSymbol2.stups");

        assertThatThrownBy(() -> TypeTable.fromAST(tree)).isInstanceOf(SymbolAlreadyDefinedException.class);
    }
}
