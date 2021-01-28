import codegen.sourcegraph.SourceGraph;
import codegen.sourcegraph.SourceGraphGenerator;
import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import parser.StupsParser;
import parser.ast.AST;
import parser.ast.ASTNode;
import parser.grammar.Grammar;
import typechecker.TypeChecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

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
        final long begin = System.nanoTime();

        // File opening + Lexing
        Lexer lexer;
        try {
            // Relativer Pfad

            final Path programPath = Paths.get(System.getProperty("user.dir") + "/" + filename);
            lexer = new StupsLexer(CharStreams.fromPath(programPath));
        } catch (IOException e) {

            try {
                // Absoluter Pfad

                final Path programPath = Paths.get(filename);
                lexer = new StupsLexer(CharStreams.fromPath(programPath));
            } catch (IOException ee) {
                System.out.println("Das Programm konnte nicht gelesen werden.");
                return;
            }
        }

        // Grammar parsing from file
        final Grammar grammar;
        try {
            final Path grammarFile = Paths.get(System.getProperty("user.dir") + "/stups.grammar");
            grammar = Grammar.fromFile(grammarFile);
        } catch (IOException e) {
            System.out.println("Die Grammatik konnte nicht geöffnet werden.");
            return;
        }

        // Parser from Grammar
        final StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        // Parsing + Typechecking of program
        final AST tree = stupsParser.parse(lexer.getAllTokens(), lexer.getVocabulary());
        tree.postprocess(grammar);
        final Map<ASTNode, String> nodeTable = TypeChecker.validate(tree);

        // Codegeneration + Output
        final String outputName = filename.replaceFirst("stups", "j");
        final SourceGraphGenerator gen = SourceGraphGenerator.fromAST(tree, nodeTable, filename);
        final SourceGraph graph = gen.generateCode();
        try {
            final Path outputFile = Paths.get(System.getProperty("user.dir") + "/" + outputName);
            Files.writeString(outputFile, graph.toString());
        } catch (IOException e) {
            System.out.println("Datei konnte nicht geschrieben werden.");
            return;
        }

        // Calling Jasmin
        final ProcessBuilder assemble = new ProcessBuilder("java", "-jar", "jasmin.jar", outputName);
        try {
            assemble.start();
        } catch (IOException e) {
            System.out.println(outputName + " konnte nicht von Jasmin übersetzt werden.");
            return;
        }

        final long end = System.nanoTime();
        System.out.printf("%nCompilation completed in %dms.%n", (end - begin) / 1_000_000);
    }

    private static void liveness(String filename) {
        System.out.println("Liveness-Analyse für " + filename);
    }
}
