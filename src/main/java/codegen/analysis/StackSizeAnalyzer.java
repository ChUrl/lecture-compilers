package codegen.analysis;

import parser.ast.SyntaxTree;
import parser.ast.SyntaxTreeNode;
import util.Logger;

import java.util.Set;

/**
 * Ermittelt die maximal benötigte Stacktiefe für ein Programm.
 * Das Programm wird übergeben als {@link SyntaxTree}.
 */
public final class StackSizeAnalyzer {

    private static final Set<String> mod;
    private static final Set<String> binaryOperators;

    static {
        mod = Set.of("assignment", "expr", "INTEGER_LIT", "BOOLEAN_LIT", "STRING_LIT", "IDENTIFIER", "print");
        binaryOperators = Set.of("AND", "OR", "ADD", "SUB", "MUL", "DIV", "MOD", "LESS", "LESS_EQUAL", "GREATER", "GREATER_EQUAL", "EQUAL", "NOT_EQUAL");
    }

    private StackSizeAnalyzer() {}

    public static int runStackModel(SyntaxTree tree) {
        Logger.logDebug("Determining minimal stack-depth", StackSizeAnalyzer.class);

        final StackModel stack = new StackModel();

        runStackModel(tree.getRoot().getChildren().get(3).getChildren().get(11), stack);

        Logger.logDebug("Found required stack-depth", StackSizeAnalyzer.class);
        return stack.getMax();
    }

    private static void runStackModel(SyntaxTreeNode root, StackModel stack) {
        if (mod.contains(root.getName())) {
            switch (root.getName()) {
                case "assignment" -> assignment(root, stack);
                case "INTEGER_LIT", "BOOLEAN_LIT", "STRING_LIT", "IDENTIFIER" -> literal(root, stack);
                case "expr" -> expr(root, stack);
                case "print" -> println(root, stack);
                default -> throw new IllegalStateException("Unexpected value: " + root.getName());
            }
        } else {
            for (SyntaxTreeNode child : root.getChildren()) {
                runStackModel(child, stack);
            }
        }
    }

    // Simulate instructions

    private static void literal(SyntaxTreeNode root, StackModel stack) {
        stack.push(root);
    }

    private static void assignment(SyntaxTreeNode root, StackModel stack) {
        runStackModel(root.getChildren().get(0), stack);

        stack.pop();
    }

    private static void println(SyntaxTreeNode root, StackModel stack) {
        stack.push(root); // Getstatic

        runStackModel(root.getChildren().get(1).getChildren().get(1), stack);

        stack.pop(); // Objectref
        stack.pop(); // Argument
    }

    private static void expr(SyntaxTreeNode root, StackModel stack) {
        if (root.getChildren().size() == 2 && binaryOperators.contains(root.getValue())) {
            // Expression with binary operator

            runStackModel(root.getChildren().get(0), stack);
            runStackModel(root.getChildren().get(1), stack);

            stack.pop(); // Argument
            stack.pop(); // Argument
            stack.push(root); // Result
        } else if (root.getChildren().size() == 1 && "NOT".equals(root.getValue())) {
            // Expression with NOT

            runStackModel(root.getChildren().get(0), stack);

            stack.push(new SyntaxTreeNode("1 (XOR)", 0)); // 1 for xor
            stack.pop(); // xor
            stack.pop(); // xor
            stack.push(root); // result
        } else if (root.getChildren().size() == 1) {
            // Expression with other unary operators

            runStackModel(root.getChildren().get(0), stack);
        }
    }
}
