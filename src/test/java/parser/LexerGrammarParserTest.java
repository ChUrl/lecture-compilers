package parser;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import parser.grammar.Grammar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LexerGrammarParserTest {

    private static StupsParser parser;

    @BeforeAll
    static void init() throws IOException, URISyntaxException {
        final Path path = Paths.get(LexerGrammarParserTest.class.getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        parser = StupsParser.fromGrammar(grammar);
    }

    private static StupsLexer getLexer(String program) {
        try {
            final Path path = Paths.get(LexerGrammarParserTest.class.getClassLoader().getResource("examplePrograms/" + program).toURI());
            final String programCode = Files.readString(path, StandardCharsets.US_ASCII);
            return new StupsLexer(CharStreams.fromString(programCode));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        return null;
    }

    @ParameterizedTest
    @ValueSource(strings = {"EmptyFile.stups",
                            "EmptyMain.stups",
                            "GeneralComment.stups",
                            "MultipleDeclarations.stups",
                            "DeclarationAssignment.stups",
                            "Expr.stups",
                            "GeneralWhile.stups",
                            "GeneralIfElse.stups",
                            "Println.stups"})
    void testCorrectPrograms(String prog) {
        final Lexer lex = getLexer(prog);

        assertThat(parser.parse(lex.getAllTokens(), lex.getVocabulary())).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"FailingSemicolon.stups",
                            "FailingMissingBrace.stups",
                            "FailingMissingBrace2.stups",
                            "FailingMissingMain.stups",
                            "FailingMissingMain2.stups",
                            "FailingEmptyParExpr.stups",
                            "FailingEmptyParExpr2.stups",
                            "FailingWrongStatement.stups",
                            "FailingWrongStatement2.stups",
                            "FailingWrongStatement3.stups"})
    void testIncorrectPrograms(String prog) {
        final Lexer lex = getLexer(prog);

        assertThatThrownBy(() -> parser.parse(lex.getAllTokens(), lex.getVocabulary())).isInstanceOf(ParseException.class);
    }
}
