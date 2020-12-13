package parser.typechecker;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.Test;
import parser.Parser;
import parser.ast.AST;
import parser.grammar.Grammar;
import typechecker.SymbolAlreadyDefinedException;
import typechecker.SymbolTable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SymbolTableTest {

    private Lexer initLexer(String program) {
        try {
            Path path = Paths.get(this.getClass().getClassLoader().getResource("examplePrograms/" + program).toURI());
            String programCode = Files.readString(path, StandardCharsets.US_ASCII);
            return new StupsLexer(CharStreams.fromString(programCode));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        return null;
    }

    @Test
    void testSingleSymbol() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        Grammar grammar = Grammar.fromFile(path);
        Parser parser = Parser.fromGrammar(grammar);

        Lexer lex = this.initLexer("SingleSymbol.stups");
        AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());
        tree.preprocess(grammar);

        SymbolTable table = SymbolTable.fromAST(tree);

        assertThat(table.getSymbolType("i")).isEqualTo("INTEGER_TYPE");
        assertThat(table.getSymbolCount()).isEqualTo(1);
    }

    @Test
    void testMultipleSymbol() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        Grammar grammar = Grammar.fromFile(path);
        Parser parser = Parser.fromGrammar(grammar);

        Lexer lex = this.initLexer("MultipleSymbol.stups");
        AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());
        tree.preprocess(grammar);

        SymbolTable table = SymbolTable.fromAST(tree);

        assertThat(table.getSymbolType("i")).isEqualTo("INTEGER_TYPE");
        assertThat(table.getSymbolType("ii")).isEqualTo("INTEGER_TYPE");
        assertThat(table.getSymbolType("b")).isEqualTo("BOOLEAN_TYPE");
        assertThat(table.getSymbolType("bb")).isEqualTo("BOOLEAN_TYPE");
        assertThat(table.getSymbolType("s")).isEqualTo("STRING_TYPE");
        assertThat(table.getSymbolType("ss")).isEqualTo("STRING_TYPE");
        assertThat(table.getSymbolCount()).isEqualTo(6);
    }

    @Test
    void testExistingSymbol() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        Grammar grammar = Grammar.fromFile(path);
        Parser parser = Parser.fromGrammar(grammar);

        Lexer lex = this.initLexer("ExistingSymbol.stups");
        AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());
        tree.preprocess(grammar);

        assertThatThrownBy(() -> SymbolTable.fromAST(tree)).isInstanceOf(SymbolAlreadyDefinedException.class);
    }
}
