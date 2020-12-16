package parser.ast;

import parser.grammar.Grammar;

import java.util.Collection;
import java.util.HashSet;

import static util.Logger.log;

public final class ASTCompacter {

    private ASTCompacter() {}

    public static void clean(AST tree, Grammar grammar) {
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
    public static void promote(AST tree, Grammar grammar) {
        log("\nPromoting nodes:");
        promote(tree.getRoot(), grammar);
    }

    private static void promote(ASTNode root, Grammar grammar) {
        final Collection<ASTNode> toRemove = new HashSet<>();

        for (ASTNode child : root.getChildren()) {
            promote(child, grammar);

            // Impliziert, dass die for-schleife nur 1x läuft, deshalb ist child das richtige Kind
            if (!grammar.canPromoteChild(root)) {
                continue;
            }

            log("Promoting " + child.getName() + " -> " + root.getName());

            root.setName(child.getName());
            root.setValue(child.getValue());
            root.setChildren(child.getChildren());
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    // Entfernt [delIfEmpty] Nodes (löscht Nodes ohne Inhalt)
    public static void deleteIfEmpty(AST tree, Grammar grammar) {
        log("\nDeleting empty nodes:");
        deleteIfEmpty(tree.getRoot(), grammar);
    }

    private static void deleteIfEmpty(ASTNode root, Grammar grammar) {
        final Collection<ASTNode> toRemove = new HashSet<>();

        for (ASTNode child : root.getChildren()) {
            deleteIfEmpty(child, grammar);

            if (!grammar.canDeleteIfEmpty(child)) {
                continue;
            }

            log("Removing " + child.getName());

            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    // Löscht redundante Informationen in [delChildren]-Nodes (z.b. IF-child von COND) und Epsilon-Nodes
    public static void deleteChildren(AST tree, Grammar grammar) {
        log("Removing redundant children:");
        deleteChildren(tree.getRoot(), grammar);
    }

    private static void deleteChildren(ASTNode root, Grammar grammar) {
        final Collection<ASTNode> toRemove = new HashSet<>();

        for (ASTNode child : root.getChildren()) {
            deleteChildren(child, grammar);

            if (!grammar.canDeleteChild(root, child)) {
                continue;
            }

            log("Removing " + root.getName() + " -> " + child.getName());

            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    // Umbenennungen
    private static void renameTo(AST tree, Grammar grammar) {
        log("\nRenaming nodes:");
        renameTo(tree.getRoot(), grammar);
    }

    private static void renameTo(ASTNode root, Grammar grammar) {
        for (ASTNode child : root.getChildren()) {
            renameTo(child, grammar);

            if (!grammar.canBeRenamed(root)) {
                continue;
            }

            log("Rename " + root.getName() + " to " + grammar.getNewName(root) + ".");

            root.setName(grammar.getNewName(root));
        }
    }

    public static void nameToValue(AST tree, Grammar grammar) {
        log("\nMoving names to values:");
        nameToValue(tree.getRoot(), grammar);
    }

    private static void nameToValue(ASTNode root, Grammar grammar) {
        final Collection<ASTNode> toRemove = new HashSet<>();

        for (ASTNode child : root.getChildren()) {
            nameToValue(child, grammar);

            if (!grammar.canMoveNameToVal(root, child)) {
                continue;
            }

            log("Moving " + child.getName() + " to value of " + root.getName());

            root.setValue(child.getName());
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    // Assignment bekommt den Identifier als Value anstatt als Child
    public static void valueToValue(AST tree, Grammar grammar) {
        log("\nMoving values to values:");
        valueToValue(tree.getRoot(), grammar);
    }

    private static void valueToValue(ASTNode root, Grammar grammar) {
        final Collection<ASTNode> toRemove = new HashSet<>();

        for (ASTNode child : root.getChildren()) {
            valueToValue(child, grammar);

            if (!grammar.hasValToVal(root, child)) {
                continue;
            }

            log("Moving " + child.getValue() + " to value of " + root.getName());

            root.setValue(child.getValue());
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }
}
