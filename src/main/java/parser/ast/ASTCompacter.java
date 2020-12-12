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
                     + remove(tree)
                     + removeNullable(tree, grammar);

            removed += change;
        } while (change != 0);

        rename(tree);
        moveOperator(tree);

        log(tree.toString());
        log("\nCleaned Tree: " + removed + " nodes removed.");
        log("-".repeat(100));
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

    // Entfernt [nullable] Nodes (löscht Nodes ohne Inhalt)
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

    // Löscht epsilon-Nodes
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

    // Löscht epsilon-Nodes
    public static int remove(AST tree) {
        int removed = remove(tree.getRoot());

        if (removed != 0) {
            log("Removed " + removed + " nodes.");
            log("-".repeat(100));
        }

        return removed;
    }

    private static int remove(Node root) {
        int removed = 0;
        Collection<Node> toRemove = new HashSet<>();

        Collection<String> removable = Arrays.asList("IF", "ELSE", "WHILE");

        for (Node child : root.getChildren()) {
            if (removable.contains(child.getName()) && !child.hasChildren()) {
                log("Removing " + root.getName() + " -> " + child.getName());
                toRemove.add(child);
            } else {
                removed += remove(child);
            }
        }

        root.getChildren().removeAll(toRemove);

        return removed + toRemove.size();
    }

    // Umbenennungen
    // EXPR_2 -> EXPR, EXPR_F -> EXPR
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

    // EXPR bekommt die Operation als Value anstatt als Child
    public static void moveOperator(AST tree) {
        moveOperator(tree.getRoot());
        log("-".repeat(100));
    }

    private static void moveOperator(Node root) {
        for (Node child : root.getChildren()) {
            moveOperator(child);
        }

        Node op = getOp(root);

        if (op == null || !"EXPR".equals(root.getName())) {
            return;
        }

        log("Moving operator " + op.getName() + " to " + root.getName());
        root.setValue(op.getName());
        root.getChildren().remove(op);
    }

    private static Node getOp(Node root) {
        for (Node child : root.getChildren()) {
            Node op = switch (child.getName()) {
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
}
