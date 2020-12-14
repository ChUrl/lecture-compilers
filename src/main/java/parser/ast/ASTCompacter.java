package parser.ast;

import parser.grammar.Grammar;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static util.Logger.log;

public final class ASTCompacter {

    private ASTCompacter() {}

    // Grundreinigung
    public static void clean(AST tree, Grammar grammar) {
        int removed = 0;

        int change;
        do {
            change = compact(tree, grammar)
                     + removeEpsilon(tree, grammar)
                     + removeRedundant(tree)
                     + removeNullable(tree, grammar);

            removed += change;
        } while (change != 0);

        renameEXPR(tree);
        moveOperatorToEXPR(tree);
        moveIdentifierToASSIGNMENT(tree);

        log(tree.toString());
        log("\nCleaned Tree: " + removed + " nodes removed.");
        log("-".repeat(100));

        System.out.println("- Tree compression successful.");
    }

    // Entfernt [compact]-able Nodes (Reicht Werte nach oben)
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

    private static int compact(ASTNode root, Grammar grammar) {
        int compacted = 0;
        Collection<ASTNode> toRemove = new HashSet<>();

        for (ASTNode child : root.getChildren()) {
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

    // Entfernt [nullable] Nodes (löscht Nodes ohne Inhalt)
    public static int removeNullable(AST tree, Grammar grammar) {
        int removed = removeNullable(tree.getRoot(), grammar);

        if (removed != 0) {
            log("Removed " + removed + " nodes.");
            log("-".repeat(100));
        }

        return removed;
    }

    private static int removeNullable(ASTNode root, Grammar grammar) {
        int removed = 0;
        Collection<ASTNode> toRemove = new HashSet<>();

        for (ASTNode child : root.getChildren()) {
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

    // Löscht epsilon-Nodes
    public static int removeEpsilon(AST tree, Grammar grammar) {
        int removed = removeEpsilon(tree.getRoot(), grammar);

        if (removed != 0) {
            log("Removed " + removed + " nodes.");
            log("-".repeat(100));
        }

        return removed;
    }

    private static int removeEpsilon(ASTNode root, Grammar grammar) {
        int removed = 0;
        Collection<ASTNode> toRemove = new HashSet<>();

        for (ASTNode child : root.getChildren()) {
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

    // Löscht doppelte Informationen (z.b. IF-child von COND)
    public static int removeRedundant(AST tree) {
        int removed = removeRedundant(tree.getRoot());

        if (removed != 0) {
            log("Removed " + removed + " nodes.");
            log("-".repeat(100));
        }

        return removed;
    }

    private static int removeRedundant(ASTNode root) {
        int removed = 0;
        Collection<ASTNode> toRemove = new HashSet<>();

        Collection<String> removable = Arrays.asList("IF", "ELSE", "WHILE", "ASSIGN");

        for (ASTNode child : root.getChildren()) {
            if (removable.contains(child.getName()) && !child.hasChildren()) {
                log("Removing " + root.getName() + " -> " + child.getName());
                toRemove.add(child);
            } else {
                removed += removeRedundant(child);
            }
        }

        root.getChildren().removeAll(toRemove);

        return removed + toRemove.size();
    }

    // Umbenennungen
    // EXPR_2 -> EXPR, EXPR_F -> EXPR
    private static void renameEXPR(AST tree) {
        renameEXPR(tree.getRoot());
        log("-".repeat(100));
    }

    private static void renameEXPR(ASTNode root) {
        String newName = switch (root.getName()) {
            case "EXPR_2", "EXPR_F" -> "EXPR";
            default -> root.getName();
        };

        if (!newName.equals(root.getName())) {
            log("Rename " + root.getName() + " to " + newName + ".");
        }

        root.setName(newName);

        for (ASTNode child : root.getChildren()) {
            renameEXPR(child);
        }
    }

    // TODO: Move Regeln zusammenfassen mit actions?
    // EXPR bekommt die Operation als Value anstatt als Child
    public static void moveOperatorToEXPR(AST tree) {
        moveOperatorToEXPR(tree.getRoot());
        log("-".repeat(100));
    }

    private static void moveOperatorToEXPR(ASTNode root) {
        for (ASTNode child : root.getChildren()) {
            moveOperatorToEXPR(child);
        }

        ASTNode op = getOp(root);

        if (op == null || !"EXPR".equals(root.getName())) {
            return;
        }

        log("Moving operator " + op.getName() + " to " + root.getName());
        root.setValue(op.getName());
        root.getChildren().remove(op);
    }

    private static ASTNode getOp(ASTNode root) {
        for (ASTNode child : root.getChildren()) {
            ASTNode op = switch (child.getName()) {
                case "ADD", "SUB", "MUL", "DIV", "MOD" -> child;
                case "NOT", "AND", "OR" -> child;
                case "LESS", "LESS_EQUAL", "GREATER", "GREATER_EQUAL", "EQUAL", "NOT_EQUAL" -> child;
                default -> null;
            };

            if (op != null) {
                return op;
            }
        }

        return null;
    }

    // Assignment bekommt den Identifier als Value anstatt als Child
    public static void moveIdentifierToASSIGNMENT(AST tree) {
        moveIdentifierToASSIGNMENT(tree.getRoot());
        log("-".repeat(100));
    }

    private static void moveIdentifierToASSIGNMENT(ASTNode root) {
        for (ASTNode child : root.getChildren()) {
            moveIdentifierToASSIGNMENT(child);
        }

        ASTNode id = getId(root);

        if (id == null || !"ASSIGNMENT".equals(root.getName())) {
            return;
        }

        log("Moving identifier " + id.getValue() + " to " + root.getName());
        root.setValue(id.getValue());
        root.getChildren().remove(id);
    }

    private static ASTNode getId(ASTNode root) {
        for (ASTNode child : root.getChildren()) {
            if (child.getName().equals("IDENTIFIER")) {
                return child;
            }
        }

        return null;
    }
}
