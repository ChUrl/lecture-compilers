import codegen.analysis.dataflow.DataFlowGraph;
import codegen.analysis.liveness.LivenessAnalysis;
import codegen.flowgraph.FlowGraph;
import codegen.flowgraph.FlowGraphGenerator;
import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import parser.StupsParser;
import parser.ast.SyntaxTree;
import parser.ast.SyntaxTreeNode;
import parser.grammar.Grammar;
import typechecker.TypeChecker;
import util.Logger;

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
        System.out.println("Kompiliere " + filename);
//        final long begin = System.nanoTime();

        final FlowGraphGenerator gen = getFlowGraphGen(filename);
        final FlowGraph graph = gen.generateGraph();

        Logger.logDebugSupplier(graph::printToImage, StupsCompiler.class);

        // Codegeneration + Output
        final String fileExtension = filename.substring(filename.lastIndexOf('.') + 1);
        final String outputName = filename.replaceFirst("\\." + fileExtension, ".j");
        final String sourceCode = graph.toString();
        try {
            final Path outputFile = Paths.get(System.getProperty("user.dir") + "/" + outputName);
            Files.writeString(outputFile, sourceCode);
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

        System.out.println("Kompilieren abgeschlossen.");
//        final long end = System.nanoTime();
//        System.out.printf("%nCompilation completed in %dms.%n", (end - begin) / 1_000_000);
    }

    private static void liveness(String filename) {
        System.out.println("Liveness-Analyse für " + filename);

        final FlowGraphGenerator gen = getFlowGraphGen(filename);
        final FlowGraph graph = gen.generateGraph();

        Logger.logDebugSupplier(graph::printToImage, StupsCompiler.class);

        final DataFlowGraph dataFlowGraph = DataFlowGraph.fromFlowGraph(graph);

        Logger.logDebugSupplier(dataFlowGraph::printToImage, StupsCompiler.class);

        final LivenessAnalysis liveness = LivenessAnalysis.fromDataFlowGraph(dataFlowGraph, gen.getVarMap());
        final int registers = liveness.doLivenessAnalysis();

        System.out.println("Liveness-Analyse abgeschlossen.");
        System.out.println("Registers: " + registers);
    }

    private static FlowGraphGenerator getFlowGraphGen(String filename) {
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
                throw new IllegalStateException("Das Programm konnte nicht gelesen werden.");
            }
        }

        // Grammar parsing from file
        final Grammar grammar;
        try {
            final Path grammarFile = Paths.get(System.getProperty("user.dir") + "/stups.grammar");
            grammar = Grammar.fromFile(grammarFile);
        } catch (IOException e) {
            System.out.println("Die Grammatik konnte nicht geöffnet werden.");
            throw new IllegalStateException("Die Grammatik konnte nicht geöffnet werden.");
        }

        // Parser from Grammar
        final StupsParser stupsParser = StupsParser.fromGrammar(grammar);

        // Parsing + Typechecking of program
        final SyntaxTree parseTree = stupsParser.parse(lexer.getAllTokens(), lexer.getVocabulary());

        Logger.logDebugSupplier(() -> parseTree.printToImage("ParseTree"), StupsCompiler.class);

        final SyntaxTree abstractSyntaxTree = SyntaxTree.toAbstractSyntaxTree(parseTree, grammar);

        final Map<SyntaxTreeNode, String> nodeTable = TypeChecker.validate(abstractSyntaxTree);

        return FlowGraphGenerator.fromAST(abstractSyntaxTree, nodeTable, filename);
    }
}
