package parser.ast;

import util.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Ein SyntaxTree wird an bestimmten Stellen rotiert, sodass bestimmte Eigenschaften
 * korrekt repräsentiert werden (Operatorpräzedenz, Linkassoziativität etc.).
 */
public final class SyntaxTreeRebalancer {

    /**
     * Jedem Operator wird eine Priorität zugewiesen, 0 ist die höchste.
     */
    private static final Map<String, Integer> operatorPriority;

    private static final Set<String> unaryOperators;
    private static final Set<String> commutativeOperators;

    //!: Operatorpräzedenz
    // 0 - Unary: -, +, !
    // 1 - Multiplicative: *, /, %
    // 2 - Additive: +, -
    // 3 - Comparative: <, <=, >, >=
    // 4 - Equality: ==, !=
    // 5 - Logical AND: &&
    // 6 - Logical OR: ||
    static {
        operatorPriority = Map.ofEntries(Map.entry("NOT", 0),
                                         Map.entry("MUL", 1),
                                         Map.entry("DIV", 1),
                                         Map.entry("MOD", 1),
                                         Map.entry("ADD", 2),
                                         Map.entry("SUB", 2),
                                         Map.entry("LESS", 3),
                                         Map.entry("LESS_EQUAL", 3),
                                         Map.entry("GREATER", 3),
                                         Map.entry("GREATER_EQUAL", 3),
                                         Map.entry("EQUAL", 4),
                                         Map.entry("NOT_EQUAL", 4),
                                         Map.entry("AND", 5),
                                         Map.entry("OR", 6));

        unaryOperators = Set.of("NOT", "ADD", "SUB");

        commutativeOperators = Set.of("ADD", "MUL", "EQUAL", "NOT_EQUAL", "AND", "OR");
    }

    private SyntaxTreeRebalancer() {}

    /**
     * Ein Abstrakter Syntaxbaum wird umbalanciert.
     *
     * <ul>
     *     <li>Baum wird gespiegelt, damit die Ausdrücke vorwárts laufen (Tiefste Ebenen müssen nach links)</li>
     *     <li>Linkspräzedenz wird durch Links-Rotationen durchgesetzt</li>
     *     <li>Operatorpräzedenz wird durch Rechtsrotationen durchgesetzt</li>
     *     <li>Kommutative Ausdrücke werden gespiegelt, damit die tiefen Teilausdrücke zuerst berechnet werden</li>
     * </ul>
     */
    public static void rebalance(SyntaxTree abstractSyntaxTree) {
        Logger.logDebug("Beginning rebalancing of syntax-tree", SyntaxTreeRebalancer.class);

        flip(abstractSyntaxTree);
        leftPrecedence(abstractSyntaxTree);
        operatorPrecedence(abstractSyntaxTree);
        flipCommutativeExpr(abstractSyntaxTree);

        Logger.logDebug("Successfully rebalanced syntax-tree", SyntaxTreeRebalancer.class);
        Logger.logDebugSupplier(() -> abstractSyntaxTree.printToImage("AbstractSyntaxTree"), SyntaxTreeRebalancer.class);
    }

    /**
     * Baum spiegeln, damit höhere Ebenen links sind und EXPR vorwärts laufen.
     */
    public static void flip(SyntaxTree abstractSyntaxTree) {
        Logger.logDebug(" :: Flipping tree for ltr evaluation", SyntaxTreeRebalancer.class);
        flip(abstractSyntaxTree.getRoot());
        Logger.logDebug(" :: Successfully flipped tree", SyntaxTreeRebalancer.class);
    }

    private static void flip(SyntaxTreeNode root) {
        for (SyntaxTreeNode child : root.getChildren()) {
            flip(child);
        }

        Collections.reverse(root.getChildren());
    }

    /**
     * Kommutative Ausdrücke werden gespiegelt, damit die tiefen Teilexpressions zuerst berechnet werden.
     */
    public static void flipCommutativeExpr(SyntaxTree abstractSyntaxTree) {
        Logger.logDebug(" :: Flipping commutative expressions for stack efficiency", SyntaxTreeRebalancer.class);
        flipCommutativeExpr(abstractSyntaxTree.getRoot());
        Logger.logDebug(" :: Succesfully optimized stack efficiency", SyntaxTreeRebalancer.class);
    }

    private static void flipCommutativeExpr(SyntaxTreeNode root) {
        for (SyntaxTreeNode child : root.getChildren()) {
            flipCommutativeExpr(child);
        }

        if ("expr".equals(root.getName()) && commutativeOperators.contains(root.getValue())) {
            // Ausdruck ist kommutativ

            if (root.getChildren().size() == 2 && root.getChildren().get(0).size() < root.getChildren().get(1).size()) {
                // Make the bigger subtree the left one

                Logger.logInfo("Flipping node \"" + root.getName() + ": " + root.getValue() + "\"\n"
                               + root.nodePrint("\t\t"), SyntaxTreeRebalancer.class);

                Collections.reverse(root.getChildren());
            }
        }
    }

    /**
     * Führt Linksrotationen durch für Linkspräzedenz.
     * Es werden EXPR-Nodes (2 Childs, 1 davon EXPR, Kein Wert) solange wie möglich linksrotiert.
     */
    public static void leftPrecedence(SyntaxTree abstractSyntaxTree) {
        Logger.logDebug(" :: Left-rotating expressions for left-precedence", SyntaxTreeRebalancer.class);
        leftPrecedence(abstractSyntaxTree.getRoot());
        Logger.logDebug(" :: Successfully rotated expressions for left-precedence", SyntaxTreeRebalancer.class);
    }

    private static void leftPrecedence(SyntaxTreeNode root) {
        for (SyntaxTreeNode child : root.getChildren()) {
            leftPrecedence(child);
        }

        final SyntaxTreeNode expr = getExpr(root);

        if (expr == null || root.getChildren().size() != 2 || !root.getValue().isEmpty()) {
            return;
        }

        boolean change;

        do {
            change = specialLeftRotate(root);
        } while (change);
    }

    /**
     * Führt eine Linksrotation durch.
     * Diese ist nicht regulär, da der Operator linksvererbt wird.
     *
     * @return Es wird false zurückgegeben, sobald keine weitere Rotation mehr möglich ist.
     */
    private static boolean specialLeftRotate(SyntaxTreeNode root) {
        Logger.logInfo("Special-left-rotation around node \"" + root.getName() + ": " + root.getValue() + "\"\n"
                       + root.nodePrint("\t\t"), SyntaxTreeRebalancer.class);

        final SyntaxTreeNode left = root.getChildren().get(0);
        final SyntaxTreeNode right = root.getChildren().get(1);

        // Verhindert Wurzel mit nur einem EXPR-Child (nach oben "hängende" Wurzel)
        if (endOfExpr(right)) {
            root.setName(right.getName());
            root.setValue(right.getValue());
            root.setChildren(left, right.getChildren().get(0));
            return false; // Braucht keine weitere Rotation
        }

        final SyntaxTreeNode insertLeft = new SyntaxTreeNode(root.getName(), root.getLine());
        insertLeft.setValue(right.getValue()); // Operation wird linksvererbt
        insertLeft.setChildren(left, right.getChildren().get(0));

        root.setName(right.getName()); // Value wird nicht gesetzt, da ans linke Kind vererbt
        root.setChildren(insertLeft, right.getChildren().get(1));

        return true;
    }

    // Findet die 1te (linkeste) expr
    private static SyntaxTreeNode getExpr(SyntaxTreeNode root) {
        for (SyntaxTreeNode child : root.getChildren()) {
            if ("expr".equals(child.getName())) {
                return child;
            }
        }

        return null;
    }

    private static boolean endOfExpr(SyntaxTreeNode root) {
        return root.getChildren().size() == 1;
    }

    /**
     * Führt Rechtsrotationen durch für Operatorpräzedenz.
     * Es wird solange rechtsrotiert, bis alle Operatoren mit hoher Priorität tiefer stehen
     * als die Operatoren mit niedriger Priorität.
     */
    public static void operatorPrecedence(SyntaxTree abstractSyntaxTree) {
        Logger.logDebug(" :: Right-rotating expressions for operator-precedence", SyntaxTreeRebalancer.class);

        boolean changed;

        do {
            changed = operatorPrecedence(abstractSyntaxTree.getRoot());
        } while (changed);

        Logger.logDebug(" :: Rotated expressions for operator-precedence", SyntaxTreeRebalancer.class);
    }

    public static boolean operatorPrecedence(SyntaxTreeNode root) {
        boolean changed = false;

        for (SyntaxTreeNode child : root.getChildren()) {
            changed = changed || operatorPrecedence(child);

            if (preceding(root, child)) {
                simpleRightRotate(root);
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Ermittelt, ob der ParentNode höhere Priorität als der ChildNode hat.
     */
    private static boolean preceding(SyntaxTreeNode parent, SyntaxTreeNode child) {
        if (!"expr".equals(parent.getName()) || parent.getValue().isEmpty()
            || !"expr".equals(child.getName()) || child.getValue().isEmpty()) {
            return false;
        }

        // Unary operators have the highest precedence
        if (child.getChildren().size() == 1 && unaryOperators.contains(child.getValue())) {
            return false;
        }

        // Less equals higher
        {
            return operatorPriority.get(parent.getValue()) < operatorPriority.get(child.getValue());
        }
    }

    private static void simpleRightRotate(SyntaxTreeNode root) {
        Logger.logInfo("Simple right-rotation around node \"" + root.getName() + ": " + root.getValue() + "\"\n"
                       + root.nodePrint("\t\t"), SyntaxTreeRebalancer.class);

        final SyntaxTreeNode left = root.getChildren().get(0);
        final SyntaxTreeNode right = root.getChildren().get(1);

        final SyntaxTreeNode insertRight = new SyntaxTreeNode(root.getName(), root.getLine());
        insertRight.setValue(root.getValue());
        insertRight.setChildren(left.getChildren().get(1), right);

        root.setName(left.getName());
        root.setValue(left.getValue());
        root.setChildren(left.getChildren().get(0), insertRight);
    }
}
