package parser;

import parser.grammar.Grammar;
import util.ast.AST;
import util.ast.Node;

import java.util.Collection;
import java.util.HashSet;

import static util.tools.Logger.log;

public final class ASTCompacter {

    private ASTCompacter() {}

    public static void clean(AST tree, Grammar grammar) {
        int removed = 0;

        int change;
        do {
            change = compact(tree, grammar)
                     + removeEpsilon(tree, grammar)
                     + removeNullable(tree, grammar);

            removed += change;
        } while (change != 0);

        rename(tree);

        log(tree.toString());
        log("\nCleaned Tree: " + removed + " nodes removed.");
        log("-".repeat(100));
    }

    public static int compact(AST tree, Grammar grammar) {
        int compacted = 0;

        int changed;
        do {
            changed = compact(tree.getRoot(), grammar);
            compacted += changed;
        } while (changed != 0);

        if (compacted != 0) {
            log("Flattened " + compacted + " nodes.");
            log("-".repeat(100));
        }

        return compacted;
    }

    private static int compact(Node root, Grammar grammar) {
        int compacted = 0;
        Collection<Node> toRemove = new HashSet<>();

        for (Node child : root.getChildren()) {
            if (grammar.hasCompact(root.getName())
                && root.getChildren().size() == 1) {

                log("Compacting " + root.getName() + " -> " + child.getName());

                root.setName(child.getName());
                root.setValue(child.getValue());
                root.setChildren(child.getChildren());

                toRemove.add(child);

            } else {
                compacted += compact(child, grammar);
            }
        }

        root.getChildren().removeAll(toRemove);

        return compacted + toRemove.size();
    }

    public static int removeNullable(AST tree, Grammar grammar) {
        int removed = removeNullable(tree.getRoot(), grammar);

        if (removed != 0) {
            log("Removed " + removed + " nodes.");
            log("-".repeat(100));
        }

        return removed;
    }

    private static int removeNullable(Node root, Grammar grammar) {
        int removed = 0;
        Collection<Node> toRemove = new HashSet<>();

        for (Node child : root.getChildren()) {
            if (grammar.hasNullable(child.getName())
                && child.getValue().isEmpty()
                && !child.hasChildren()) {

                log("Removing " + child.getName());

                toRemove.add(child);
            } else {
                removed += removeNullable(child, grammar);
            }
        }

        root.getChildren().removeAll(toRemove);

        return removed + toRemove.size();
    }

    // Returns the number of removed nodes
    public static int removeEpsilon(AST tree, Grammar grammar) {
        int removed = removeEpsilon(tree.getRoot(), grammar);

        if (removed != 0) {
            log("Removed " + removed + " nodes.");
            log("-".repeat(100));
        }

        return removed;
    }

    private static int removeEpsilon(Node root, Grammar grammar) {
        int removed = 0;
        Collection<Node> toRemove = new HashSet<>();

        for (Node child : root.getChildren()) {
            if (child.getName().equals(grammar.getEpsilonSymbol()) && !child.hasChildren()) {
                log("Removing " + root.getName() + " -> " + child.getName());
                toRemove.add(child);
            } else {
                removed += removeEpsilon(child, grammar);
            }
        }

        root.getChildren().removeAll(toRemove);

        return removed + toRemove.size();
    }

    private static void rename(AST tree) {
        rename(tree.getRoot());
        log("-".repeat(100));
    }

    private static void rename(Node root) {
        String newName = switch (root.getName()) {
            case "EXPR_2", "EXPR_F" -> "EXPR";
            default -> root.getName();
        };

        if (!newName.equals(root.getName())) {
            log("Rename " + root.getName() + " to " + newName + ".");
        }

        root.setName(newName);

        for (Node child : root.getChildren()) {
            rename(child);
        }
    }
}
