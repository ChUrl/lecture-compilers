package parser.ast;

import parser.grammar.Grammar;

import java.util.Collection;
import java.util.HashSet;

import static util.Logger.log;

/**
 * Wendet in der Grammatik definierte Regeln auf einen Parsebaum an.
 * Dies ist der erste Schritt zum Abstrakten Syntaxbaum.
 *
 * <ul>
 *     <li>Löscht redundante Knoten</li>
 *     <li>Löscht leere Knoten</li>
 *     <li>Komprimiert Äste, welche nur Informationen hochpropagieren</li>
 *     <li>Führt Umbenennungen durch</li>
 *     <li>Verschiebt Informationen in Knoten-Namen und -Wert</li>
 * </ul>
 */
public final class ParseTreeCleaner {

    private ParseTreeCleaner() {}

    public static void clean(SyntaxTree parseTree, Grammar grammar) {
        deleteChildren(parseTree, grammar);
        deleteIfEmpty(parseTree, grammar);
        promote(parseTree, grammar);

        renameTo(parseTree, grammar);
        nameToValue(parseTree, grammar);
        valueToValue(parseTree, grammar);

        log("\nCleaned Tree:\n" + parseTree);
        log("-".repeat(100));
        System.out.println(" - Compressing syntax-tree...");
    }

    /**
     * Es werden Werte nach oben gereicht von [promote]-able Nodes.
     */
    public static void promote(SyntaxTree parseTree, Grammar grammar) {
        log("\nPromoting nodes:");
        promote(parseTree.getRoot(), grammar);
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

            child.setValue("REMOVE"); // If both childs have the same identity both are removed, so change one
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    /**
     * Löscht leere Knoten mit [delIfEmpty].
     */
    public static void deleteIfEmpty(SyntaxTree parseTree, Grammar grammar) {
        log("\nDeleting empty nodes:");
        deleteIfEmpty(parseTree.getRoot(), grammar);
    }

    private static void deleteIfEmpty(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            deleteIfEmpty(child, grammar);

            if (!grammar.canDeleteIfEmpty(child)) {
                continue;
            }

            log("Removing " + child.getName());

            child.setValue("REMOVE"); // If both childs have the same identity both are removed, so change one
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    /**
     * Löscht redundante Informationen in [delChildren]-Nodes (z.b. IF-child von COND) und Epsilon-Nodes.
     */
    public static void deleteChildren(SyntaxTree parseTree, Grammar grammar) {
        log("Removing redundant children:");
        deleteChildren(parseTree.getRoot(), grammar);
    }

    private static void deleteChildren(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            deleteChildren(child, grammar);

            if (!grammar.canDeleteChild(root, child)) {
                continue;
            }

            log("Removing " + root.getName() + " -> " + child.getName());

            child.setValue("REMOVE"); // If both childs have the same identity both are removed, so change one
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    /**
     * Führt Umbenennungen durch.
     */
    private static void renameTo(SyntaxTree parseTree, Grammar grammar) {
        log("\nRenaming nodes:");
        renameTo(parseTree.getRoot(), grammar);
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

    /**
     * Verschiebt Knotennamen von [nametoval]-Nodes in Parent-Values und löscht das Child.
     */
    public static void nameToValue(SyntaxTree parseTree, Grammar grammar) {
        log("\nMoving names to values:");
        nameToValue(parseTree.getRoot(), grammar);
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

            child.setValue("REMOVE"); // If both childs have the same identity both are removed, so change one
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    /**
     * [valtoval]-Nodes bekommen den Child-Namen als Value anstatt als Child.
     * Wird z.B. durchgeführt bei Assignments: Der Assignment-Node bekommt den
     * Variablennamen als Wert anstatt als Child-Node.
     */
    public static void valueToValue(SyntaxTree parseTree, Grammar grammar) {
        log("\nMoving values to values:");
        valueToValue(parseTree.getRoot(), grammar);
    }

    private static void valueToValue(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            valueToValue(child, grammar);

            if (!grammar.hasValToVal(root, child) || !root.getValue().isBlank()) {
                continue;
            }

            if (root.getChildren().size() == 2
                && root.getChildren().get(0).getName().equals(root.getChildren().get(1).getName())) {
                // Case where variable is assigned another variable with the same name

                log("Moving " + root.getChildren().get(1).getValue() + " to value of " + root.getName());
                log(root.toString());

                root.setValue(root.getChildren().get(1).getValue());

                root.getChildren().get(1).setValue("REMOVE"); // If both childs have the same identity both are removed, so change one
                toRemove.add(root.getChildren().get(1));

            } else {
                // Usual case where an expression is assigned

                log("Moving " + child.getValue() + " to value of " + root.getName());
                log(root.toString());

                root.setValue(child.getValue());
                toRemove.add(child);
            }
        }

        root.getChildren().removeAll(toRemove);
    }
}
