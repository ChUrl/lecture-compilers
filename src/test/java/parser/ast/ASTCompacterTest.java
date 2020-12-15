package parser.ast;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.Test;
import parser.StupsParser;
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
            final Path path = Paths.get(this.getClass().getClassLoader().getResource("examplePrograms/" + program).toURI());
            final String programCode = Files.readString(path, StandardCharsets.US_ASCII);
            return new StupsLexer(CharStreams.fromString(programCode));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        return null;
    }

    @Test
    void testDeleteChildren() throws URISyntaxException, IOException {
        final Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        final StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        final Lexer lex = this.initLexer("GeneralOperator.stups");
        final AST tree = stupsParser.parse(lex.getAllTokens(), lex.getVocabulary());

        final long before = tree.size();

        ASTCompacter.deleteChildren(tree, grammar);

        assertThat(before - tree.size()).isEqualTo(3);
        System.out.println(tree);
    }

    @Test
    void testPromote() throws URISyntaxException, IOException {
        final Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        final StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        final Lexer lex = this.initLexer("GeneralOperator.stups");
        final AST tree = stupsParser.parse(lex.getAllTokens(), lex.getVocabulary());

        final long before = tree.size();

        ASTCompacter.promote(tree, grammar);

        assertThat(before - tree.size()).isEqualTo(14);
        System.out.println(tree);
    }

    @Test
    void testDeleteEmpty() throws URISyntaxException, IOException {
        final Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        final StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        final Lexer lex = this.initLexer("GeneralOperator.stups");
        final AST tree = stupsParser.parse(lex.getAllTokens(), lex.getVocabulary());
        ASTCompacter.deleteChildren(tree, grammar);

        final long before = tree.size();

        ASTCompacter.deleteIfEmpty(tree, grammar);

        assertThat(before - tree.size()).isEqualTo(2);
        System.out.println(tree);
    }

    @Test
    void testClean() throws URISyntaxException, IOException {
        final Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        final StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        final Lexer lex = this.initLexer("GeneralOperator.stups");
        final AST tree = stupsParser.parse(lex.getAllTokens(), lex.getVocabulary());

        ASTCompacter.clean(tree, grammar);

        assertThat(tree.size()).isEqualTo(28);
    }
}
