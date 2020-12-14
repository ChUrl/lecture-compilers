import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import parser.StupsParser;
import parser.ast.AST;
import parser.grammar.Grammar;
import typechecker.TypeChecker;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class StupsCompiler {

    private StupsCompiler() {}

    public static void main(String[] args) {
        System.out.println("StupsCompiler: " + Arrays.toString(args) + "\n");

        if (args.length != 2) {
            System.out.println("Falsche Argumente.");
            return;
        }

        switch (args[0]) {
            case "-compile" -> compile(args[1]);
            case "-liveness" -> liveness(args[1]);
            default -> System.out.println("Falsche Argumente.");
        }
    }

    private static void compile(String filename) {
        System.out.println("Beginning compilation.");

        Lexer lexer;
        try {
            // Relativer Pfad

            Path programPath = Paths.get(System.getProperty("user.dir") + "/" + filename);
            lexer = new StupsLexer(CharStreams.fromPath(programPath));
        } catch (IOException e) {

            try {
                // Absoluter Pfad

                Path programPath = Paths.get(filename);
                lexer = new StupsLexer(CharStreams.fromPath(programPath));
            } catch (IOException ee) {
                System.out.println("Das Programm konnte nicht gelesen werden.");
                return;
            }
        }

        Grammar grammar;
        try {
            Path grammarFile = Paths.get(System.getProperty("user.dir") + "/stups.grammar");
            grammar = Grammar.fromFile(grammarFile);
        } catch (IOException e) {
            System.out.println("Die Grammatik konnte nicht geöffnet werden.");
            return;
        }

        StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        AST tree = stupsParser.parse(lexer.getAllTokens(), lexer.getVocabulary());

        tree.postprocess(grammar);

        TypeChecker.validate(tree);

        System.out.println("Compilation completed.");
    }

    private static void liveness(String filename) {
        System.out.println("Liveness-Analyse für " + filename);
    }
}
