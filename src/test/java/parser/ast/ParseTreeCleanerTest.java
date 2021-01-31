package parser.ast;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.BeforeAll;
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

class ParseTreeCleanerTest {

    private static Grammar grammar;
    private static StupsParser parser;

    @BeforeAll
    static void init() throws IOException, URISyntaxException {
        final Path path = Paths.get(ParseTreeCleanerTest.class.getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        grammar = Grammar.fromFile(path);
        parser = StupsParser.fromGrammar(grammar);
    }

    private static SyntaxTree getTree(String program) {
        try {
            final Path path = Paths.get(ParseTreeCleanerTest.class.getClassLoader().getResource("examplePrograms/" + program).toURI());
            final String programCode = Files.readString(path, StandardCharsets.US_ASCII);
            final Lexer lex = new StupsLexer(CharStreams.fromString(programCode));
            return parser.parse(lex.getAllTokens(), lex.getVocabulary());
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        return null;
    }

    @Test
    void testDeleteChildren() {
        final SyntaxTree tree = getTree("GeneralOperator.stups");
        final long before = tree.size();

        ParseTreeCleaner.deleteChildren(tree, grammar);

        assertThat(before - tree.size()).isEqualTo(3);
    }

    @Test
    void testPromote() {
        final SyntaxTree tree = getTree("GeneralOperator.stups");
        final long before = tree.size();

        ParseTreeCleaner.promote(tree, grammar);

        assertThat(before - tree.size()).isEqualTo(14);
    }

    @Test
    void testDeleteEmpty() {
        final SyntaxTree tree = getTree("GeneralOperator.stups");
        ParseTreeCleaner.deleteChildren(tree, grammar);
        final long before = tree.size();

        ParseTreeCleaner.deleteIfEmpty(tree, grammar);

        assertThat(before - tree.size()).isEqualTo(2);
    }

    @Test
    void testClean() {
        final SyntaxTree tree = getTree("GeneralOperator.stups");

        ParseTreeCleaner.clean(tree, grammar);

        assertThat(tree.size()).isEqualTo(28);
    }
}
