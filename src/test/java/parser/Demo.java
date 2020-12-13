package parser;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.Test;
import parser.ast.AST;
import parser.ast.ASTCompacter;
import parser.ast.ExpressionBalancer;
import parser.grammar.Grammar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Demo {

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
    void demoClean() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        Grammar grammar = Grammar.fromFile(path);
        LL1Parser parser = LL1Parser.fromGrammar(grammar);

        Lexer lex = this.initLexer("General.stups");
        AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());

        ASTCompacter.clean(tree, grammar);
    }

    @Test
    void demoLeftPrecedence() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        Grammar grammar = Grammar.fromFile(path);
        LL1Parser parser = LL1Parser.fromGrammar(grammar);

        Lexer lex = this.initLexer("General.stups");
        AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());

        ASTCompacter.clean(tree, grammar);
        ExpressionBalancer.flip(tree);
        System.out.println("Before left-precedence:\n" + tree);
        ExpressionBalancer.leftPrecedence(tree);
        System.out.println("After left-precedence:\n" + tree);
    }

    @Test
    void demoOperatorPrecedence() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        Grammar grammar = Grammar.fromFile(path);
        LL1Parser parser = LL1Parser.fromGrammar(grammar);

        Lexer lex = this.initLexer("General.stups");
        AST tree = parser.parse(lex.getAllTokens(), lex.getVocabulary());

        ASTCompacter.clean(tree, grammar);
        ExpressionBalancer.flip(tree);
        ExpressionBalancer.leftPrecedence(tree);
        System.out.println("Before operator-precedence:\n" + tree);
        ExpressionBalancer.operatorPrecedence(tree);
        System.out.println("After operator-precedence:\n" + tree);
    }
}
