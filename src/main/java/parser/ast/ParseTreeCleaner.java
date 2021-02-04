package parser.ast;

import parser.grammar.Grammar;
import util.Logger;

import java.util.Collection;
import java.util.HashSet;

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
        Logger.logDebug("Beginning cleaning of parse-tree", ParseTreeCleaner.class);

        deleteChildren(parseTree, grammar);
        deleteIfEmpty(parseTree, grammar);
        promote(parseTree, grammar);

        renameTo(parseTree, grammar);
        nameToValue(parseTree, grammar);
        valueToValue(parseTree, grammar);

        Logger.logDebug("Successfully cleaned the parse-tree", ParseTreeCleaner.class);
        Logger.logDebugSupplier(() -> parseTree.printToImage("ParseTreeCleaned"), ParseTreeCleaner.class);
    }

    /**
     * Es werden Werte nach oben gereicht von [promote]-able Nodes.
     */
    public static void promote(SyntaxTree parseTree, Grammar grammar) {
        Logger.logDebug(" :: Beginning up-propagation of nodes", ParseTreeCleaner.class);
        promote(parseTree.getRoot(), grammar);
        Logger.logDebug(" :: Promoted nodes", ParseTreeCleaner.class);
    }

    private static void promote(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            promote(child, grammar);

            // Impliziert, dass die for-schleife nur 1x läuft, deshalb ist child das richtige Kind
            if (!grammar.canPromoteChild(root)) {
                continue;
            }

            Logger.logInfo("Promoting child \"" + child.getName() + "\" to root \"" + root.getName() + "\"\n"
                           + root.nodePrint("\t\t"), ParseTreeCleaner.class);

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
        Logger.logDebug(" :: Beginning removal of empty nodes", ParseTreeCleaner.class);
        deleteIfEmpty(parseTree.getRoot(), grammar);
        Logger.logDebug(" :: Removed all empty nodes", ParseTreeCleaner.class);
    }

    private static void deleteIfEmpty(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            deleteIfEmpty(child, grammar);

            if (!grammar.canDeleteIfEmpty(child)) {
                continue;
            }

            Logger.logInfo("Removing node \"" + child.getName() + "\"", ParseTreeCleaner.class);

            child.setValue("REMOVE"); // If both childs have the same identity both are removed, so change one
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    /**
     * Löscht redundante Informationen in [delChildren]-Nodes (z.b. IF-child von COND) und Epsilon-Nodes.
     */
    public static void deleteChildren(SyntaxTree parseTree, Grammar grammar) {
        Logger.logDebug(" :: Beginning removal of redundant children", ParseTreeCleaner.class);
        deleteChildren(parseTree.getRoot(), grammar);
        Logger.logDebug(" :: Redundant children were removed", ParseTreeCleaner.class);
    }

    private static void deleteChildren(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            deleteChildren(child, grammar);

            if (!grammar.canDeleteChild(root, child)) {
                continue;
            }

            Logger.logInfo("Removing child \"" + child.getName() + "\" from root \"" + root.getName() + "\"\n"
                           + root.nodePrint("\t\t"), ParseTreeCleaner.class);

            child.setValue("REMOVE"); // If both childs have the same identity both are removed, so change one
            toRemove.add(child);
        }

        root.getChildren().removeAll(toRemove);
    }

    /**
     * Führt Umbenennungen durch.
     */
    private static void renameTo(SyntaxTree parseTree, Grammar grammar) {
        Logger.logDebug(" :: Beginning renaming of nodes", ParseTreeCleaner.class);
        renameTo(parseTree.getRoot(), grammar);
        Logger.logDebug(" :: Renamed nodes", ParseTreeCleaner.class);
    }

    private static void renameTo(SyntaxTreeNode root, Grammar grammar) {
        for (SyntaxTreeNode child : root.getChildren()) {
            renameTo(child, grammar);

            if (!grammar.canBeRenamed(root)) {
                continue;
            }

            Logger.logInfo("Renaming node \"" + root.getName() + "\" to \"" + grammar.getNewName(root) + "\"", ParseTreeCleaner.class);

            root.setName(grammar.getNewName(root));
        }
    }

    /**
     * Verschiebt Knotennamen von [nametoval]-Nodes in Parent-Values und löscht das Child.
     */
    public static void nameToValue(SyntaxTree parseTree, Grammar grammar) {
        Logger.logDebug(" :: Beginning up-propagation of node-names", ParseTreeCleaner.class);
        nameToValue(parseTree.getRoot(), grammar);
        Logger.logDebug(" :: Moved node-names to parent-values", ParseTreeCleaner.class);
    }

    private static void nameToValue(SyntaxTreeNode root, Grammar grammar) {
        final Collection<SyntaxTreeNode> toRemove = new HashSet<>();

        for (SyntaxTreeNode child : root.getChildren()) {
            nameToValue(child, grammar);

            if (!grammar.canMoveNameToVal(root, child)) {
                continue;
            }

            Logger.logInfo("Moving child-name \"" + child.getName() + "\" to parent-value of node \"" + root.getName() + "\"\n"
                           + root.nodePrint("\t\t"), ParseTreeCleaner.class);

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
        Logger.logDebug(" :: Beginning up-propagation of node-values", ParseTreeCleaner.class);
        valueToValue(parseTree.getRoot(), grammar);
        Logger.logDebug(" :: Moved node-values to parent-values", ParseTreeCleaner.class);
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

                Logger.logInfo("Moving child-value \"" + root.getChildren().get(1).getValue()
                               + "\" to parent-value of node \"" + root.getName() + "\"\n"
                               + root.nodePrint("\t\t"), ParseTreeCleaner.class);

                root.setValue(root.getChildren().get(1).getValue());

                root.getChildren().get(1).setValue("REMOVE"); // If both childs have the same identity both are removed, so change one
                toRemove.add(root.getChildren().get(1));

            } else {
                // Usual case where an expression is assigned

                Logger.logInfo("Moving child value \"" + child.getValue() + "\" to parent-value of node \""
                               + root.getName() + "\"\n" + root.nodePrint("\t\t"), ParseTreeCleaner.class);

                root.setValue(child.getValue());
                toRemove.add(child);
            }
        }

        root.getChildren().removeAll(toRemove);
    }
}
