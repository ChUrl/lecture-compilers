package parser.ast;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.Test;
import parser.LL1Parser;
import parser.grammar.Grammar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class ASTCompacterTest {

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
    void testRemoveEpsilon() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        Grammar grammar = Grammar.fromFile(path);
        LL1Parser parser = LL1Parser.fromGrammar(grammar);

        Lexer lex = this.initLexer("GeneralOperator.stups");
        AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());

        assertThat(ASTCompacter.removeEpsilon(tree, grammar)).isEqualTo(2);
        System.out.println(tree);
    }

    @Test
    void testCompact() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        Grammar grammar = Grammar.fromFile(path);
        LL1Parser parser = LL1Parser.fromGrammar(grammar);

        Lexer lex = this.initLexer("GeneralOperator.stups");
        AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());

        assertThat(ASTCompacter.compact(tree, grammar)).isEqualTo(14);
        System.out.println(tree);
    }

    @Test
    void testRemoveNullable() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        Grammar grammar = Grammar.fromFile(path);
        LL1Parser parser = LL1Parser.fromGrammar(grammar);

        Lexer lex = this.initLexer("GeneralOperator.stups");
        AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());
        ASTCompacter.removeEpsilon(tree, grammar);

        assertThat(ASTCompacter.removeNullable(tree, grammar)).isEqualTo(2);
        System.out.println(tree);
    }

    @Test
    void testClean() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        Grammar grammar = Grammar.fromFile(path);
        LL1Parser parser = LL1Parser.fromGrammar(grammar);

        Lexer lex = this.initLexer("GeneralOperator.stups");
        AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());

        ASTCompacter.clean(tree, grammar);

        assertThat(tree.size()).isEqualTo(29);
    }
}
