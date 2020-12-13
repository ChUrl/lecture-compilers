package parser;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class LexerStupsParserGrammarTest {

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
        StupsParser stupsParser = StupsParser.fromGrammar(path);

        Lexer lex = this.initLexer("EmptyFile.stups");

        assertThat(stupsParser.parse(lex.getAllTokens(), lex.getVocabulary())).isNotNull();
    }

    @Test
    void testEmptyMain() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        StupsParser stupsParser = StupsParser.fromGrammar(path);

        Lexer lex = this.initLexer("EmptyMain.stups");

        assertThat(stupsParser.parse(lex.getAllTokens(), lex.getVocabulary())).isNotNull();
    }

    @Test
    void testGeneralComment() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        StupsParser stupsParser = StupsParser.fromGrammar(path);

        Lexer lex = this.initLexer("GeneralComment.stups");

        assertThat(stupsParser.parse(lex.getAllTokens(), lex.getVocabulary())).isNotNull();
    }

    @Test
    void tesMultiDecl() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        StupsParser stupsParser = StupsParser.fromGrammar(path);

        Lexer lex = this.initLexer("MultipleDeclarations.stups");

        assertThat(stupsParser.parse(lex.getAllTokens(), lex.getVocabulary())).isNotNull();
    }

    @Test
    void testDeclarationAssignment() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        StupsParser stupsParser = StupsParser.fromGrammar(path);

        Lexer lex = this.initLexer("DeclarationAssignment.stups");

        assertThat(stupsParser.parse(lex.getAllTokens(), lex.getVocabulary())).isNotNull();
    }

    @Test
    void testExpr() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        StupsParser stupsParser = StupsParser.fromGrammar(path);

        Lexer lex = this.initLexer("Expr.stups");

        assertThat(stupsParser.parse(lex.getAllTokens(), lex.getVocabulary())).isNotNull();
    }

    @Test
    void testGeneralWhile() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        StupsParser stupsParser = StupsParser.fromGrammar(path);

        Lexer lex = this.initLexer("GeneralWhile.stups");

        assertThat(stupsParser.parse(lex.getAllTokens(), lex.getVocabulary())).isNotNull();
    }

    @Test
    void testGeneralIfElse() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        StupsParser stupsParser = StupsParser.fromGrammar(path);

        Lexer lex = this.initLexer("GeneralIfElse.stups");

        assertThat(stupsParser.parse(lex.getAllTokens(), lex.getVocabulary())).isNotNull();
    }
}
