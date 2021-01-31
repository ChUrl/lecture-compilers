package parser.ast;

import parser.grammar.Grammar;

import java.util.Collection;
import java.util.HashSet;

import static util.Logger.log;

public final class ASTCompacter {

    private ASTCompacter() {}

    public static void clean(SyntaxTree tree, Grammar grammar) {
        deleteChildren(tree, grammar);
        deleteIfEmpty(tree, grammar);
        promote(tree, grammar);

        renameTo(tree, grammar);
        nameToValue(tree, grammar);
        valueToValue(tree, grammar);

        log("\nCleaned Tree:\n" + tree);
        log("-".repeat(100));
        System.out.println(" - Compressing syntax-tree...");
    }

    // Entfernt [promote]-able Nodes (Reicht Werte nach oben)
    public static void promote(SyntaxTree tree, Grammar grammar) {
        log("\nPromoting nodes:");
        promote(tree.getRoot(), grammar);
    }

    private static void promote(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            promote(child, grammar);

            // Impliziert, dass die for-schleife nur 1x läuft, deshalb ist child das richtige Kind
            if (!grammar.canPromoteChild(root)) {
                continue;
            }

            log("Promoting " + child.getName() + " -> " + root.getName());
            log(root.toString());

            root.setName(child.getName());
            root.setValue(child.getValue());
            root.setChildren(child.getChildren());

            child.setValue("REMOVE"); // If both childs have the same identity both are removed
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    // Entfernt [delIfEmpty] Nodes (löscht Nodes ohne Inhalt)
    public static void deleteIfEmpty(SyntaxTree tree, Grammar grammar) {
        log("\nDeleting empty nodes:");
        deleteIfEmpty(tree.getRoot(), grammar);
    }

    private static void deleteIfEmpty(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            deleteIfEmpty(child, grammar);

            if (!grammar.canDeleteIfEmpty(child)) {
                continue;
            }

            log("Removing " + child.getName());

            child.setValue("REMOVE"); // If both childs have the same identity both are removed
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    // Löscht redundante Informationen in [delChildren]-Nodes (z.b. IF-child von COND) und Epsilon-Nodes
    public static void deleteChildren(SyntaxTree tree, Grammar grammar) {
        log("Removing redundant children:");
        deleteChildren(tree.getRoot(), grammar);
    }

    private static void deleteChildren(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            deleteChildren(child, grammar);

            if (!grammar.canDeleteChild(root, child)) {
                continue;
            }

            log("Removing " + root.getName() + " -> " + child.getName());

            child.setValue("REMOVE"); // If both childs have the same identity both are removed
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    // Umbenennungen
    private static void renameTo(SyntaxTree tree, Grammar grammar) {
        log("\nRenaming nodes:");
        renameTo(tree.getRoot(), grammar);
    }

    private static void renameTo(SyntaxTreeNode root, Grammar grammar) {
        for (SyntaxTreeNode child : root.getChildren()) {
            renameTo(child, grammar);

            if (!grammar.canBeRenamed(root)) {
                continue;
            }

            log("Rename " + root.getName() + " to " + grammar.getNewName(root) + ".");

            root.setName(grammar.getNewName(root));
        }
    }

    public static void nameToValue(SyntaxTree tree, Grammar grammar) {
        log("\nMoving names to values:");
        nameToValue(tree.getRoot(), grammar);
    }

    private static void nameToValue(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            nameToValue(child, grammar);

            if (!grammar.canMoveNameToVal(root, child)) {
                continue;
            }

            log("Moving " + child.getName() + " to value of " + root.getName());
            log(root.toString());

            root.setValue(child.getName());

            child.setValue("REMOVE"); // If both childs have the same identity both are removed
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    // Assignment bekommt den Identifier als Value anstatt als Child
    public static void valueToValue(SyntaxTree tree, Grammar grammar) {
        log("\nMoving values to values:");
        valueToValue(tree.getRoot(), grammar);
    }

    private static void valueToValue(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            valueToValue(child, grammar);

            if (!grammar.hasValToVal(root, child)) {
                continue;
            }

            if (!root.getValue().isBlank()) {
                // Do not overwrite
                continue;
            }

            if (root.getChildren().size() == 2
                && root.getChildren().get(0).getName().equals(root.getChildren().get(1).getName())) {
                // Special case where variable is assigned another variable

                log("Special case: Var to var assignment");
                log("Moving " + root.getChildren().get(1).getValue() + " to value of " + root.getName());
                log(root.toString());

                root.setValue(root.getChildren().get(1).getValue());

                root.getChildren().get(1).setValue("REMOVE"); // If both childs have the same identity both are removed
                toRemove.add(root.getChildren().get(1));

                continue;
            }

            log("Moving " + child.getValue() + " to value of " + root.getName());
            log(root.toString());

            root.setValue(child.getValue());
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }
}
