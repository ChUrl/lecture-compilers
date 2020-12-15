package parser;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.Test;
import parser.ast.AST;
import parser.ast.ASTBalancer;
import parser.ast.ASTCompacter;
import parser.grammar.Grammar;
import typechecker.TypeChecker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Demo {

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
    void demoClean() throws URISyntaxException, IOException {
        final Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        final StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        final Lexer lex = this.initLexer("General.stups");
        final AST tree = stupsParser.parse(lex.getAllTokens(), lex.getVocabulary());

        System.out.println(tree);
        ASTCompacter.clean(tree, grammar);
        System.out.println(tree);
    }

    @Test
    void demoLeftPrecedence() throws URISyntaxException, IOException {
        final Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        final StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        final Lexer lex = this.initLexer("General.stups");
        final AST tree = stupsParser.parse(lex.getAllTokens(), lex.getVocabulary());

        ASTCompacter.clean(tree, grammar);
        ASTBalancer.flip(tree);
        System.out.println("Before left-precedence:\n" + tree);
        ASTBalancer.leftPrecedence(tree);
        System.out.println("After left-precedence:\n" + tree);
    }

    @Test
    void demoOperatorPrecedence() throws URISyntaxException, IOException {
        final Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        final StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        final Lexer lex = this.initLexer("General.stups");
        final AST tree = stupsParser.parse(lex.getAllTokens(), lex.getVocabulary());

        ASTCompacter.clean(tree, grammar);
        ASTBalancer.flip(tree);
        ASTBalancer.leftPrecedence(tree);
        System.out.println("Before operator-precedence:\n" + tree);
        ASTBalancer.operatorPrecedence(tree);
        System.out.println("After operator-precedence:\n" + tree);
    }

    @Test
    void demoTypeCheck() throws URISyntaxException, IOException {
        final Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        final Grammar grammar = Grammar.fromFile(path);
        final StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        final Lexer lex = this.initLexer("General.stups");
        final AST tree = stupsParser.parse(lex.getAllTokens(), lex.getVocabulary());

        tree.postprocess(grammar);
        System.out.println("After Postprocessing:" + tree);

        TypeChecker.validate(tree);
    }
}
