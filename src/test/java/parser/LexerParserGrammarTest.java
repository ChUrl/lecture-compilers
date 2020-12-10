package parser;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import parser.grammar.Grammar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LexerParserGrammarTest {

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
    void testEmptyFile() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        Lexer lex = this.initLexer("EmptyFile.stups");

        assertThat(parser.parse(lex.getAllTokens(), lex.getVocabulary())).isTrue();
    }

    @Test
    void testEmptyMain() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        Lexer lex = this.initLexer("EmptyMain.stups");

        assertThat(parser.parse(lex.getAllTokens(), lex.getVocabulary())).isTrue();
    }

    @Test
    void testGeneralComment() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        Lexer lex = this.initLexer("GeneralComment.stups");

        assertThat(parser.parse(lex.getAllTokens(), lex.getVocabulary())).isTrue();
    }

    @Test
    void tesMultiDecl() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        Lexer lex = this.initLexer("MultipleDeclarations.stups");

        assertThat(parser.parse(lex.getAllTokens(), lex.getVocabulary())).isTrue();
    }

    @Test
    void testDeclarationAssignment() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        Lexer lex = this.initLexer("DeclarationAssignment.stups");

        assertThat(parser.parse(lex.getAllTokens(), lex.getVocabulary())).isTrue();
    }

    @Test
    void testExpr() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        Lexer lex = this.initLexer("Expr.stups");

        assertThat(parser.parse(lex.getAllTokens(), lex.getVocabulary())).isTrue();
    }

    @Test
    void testGeneralWhile() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        Lexer lex = this.initLexer("GeneralWhile.stups");

        assertThat(parser.parse(lex.getAllTokens(), lex.getVocabulary())).isTrue();
    }

    @Test
    void testGeneralIfElse() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        Lexer lex = this.initLexer("GeneralIfElse.stups");

        assertThat(parser.parse(lex.getAllTokens(), lex.getVocabulary())).isTrue();
    }
}
