package parser.ast;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static util.Logger.log;

public final class ASTBalancer {

    private static final Map<String, Integer> priority;

    // 0 - Unary: -, +, !
    // 1 - Multiplicative: *, /, %
    // 2 - Additive: +, -
    // 3 - Comparative: <, <=, >, >=
    // 4 - Equality: ==, !=
    // 5 - Logical AND: &&
    // 6 - Logical OR: ||
    static {
        priority = new HashMap<>();

        priority.put("NOT", 0);

        priority.put("MUL", 1);
        priority.put("DIV", 1);
        priority.put("MOD", 1);

        priority.put("ADD", 2);
        priority.put("SUB", 2);

        priority.put("LESS", 3);
        priority.put("LESS_EQUAL", 3);
        priority.put("GREATER", 3);
        priority.put("GREATER_EQUAL", 3);

        priority.put("EQUAL", 4);
        priority.put("NOT_EQUAL", 4);

        priority.put("AND", 5);

        priority.put("OR", 6);
    }

    private ASTBalancer() {}

    public static void balance(AST tree) {
        flip(tree);
        leftPrecedence(tree);
        operatorPrecedence(tree);

        log(tree.toString());
        log("-".repeat(100));
    }

    // Baum spiegeln, damit höhere Ebenen links sind und EXPR vorwärts laufen
    public static void flip(AST tree) {
        log("Flipping tree for ltr evaluation");
        flip(tree.getRoot());
    }

    private static void flip(ASTNode root) {
        for (ASTNode child : root.getChildren()) {
            flip(child);
        }

        Collections.reverse(root.getChildren());
    }

    // Führt Linksrotationen durch
    // Es werden EXPR-Nodes (2 Childs, 1 davon EXPR, Kein Wert) solange wie möglich linksrotiert
    public static void leftPrecedence(AST tree) {
        log("Left-rotating expressions for left-precedence");
        leftPrecedence(tree.getRoot());
    }

    // Es wird solange rotiert bis die letzte "Rotation" durchgeführt wurde
    private static void leftPrecedence(ASTNode root) {
        for (ASTNode child : root.getChildren()) {
            leftPrecedence(child);
        }

        ASTNode expr = getExpr(root);

        if (expr == null || root.getChildren().size() != 2 || !root.getValue().isEmpty()) {
            return;
        }

        boolean change;

        do {
            change = specialLeftRotate(root);
        } while (change);
    }

    // Die Letzte Rotation ist keine richtige Rotation, dort wird false zurückgegeben
    private static boolean specialLeftRotate(ASTNode root) {
        ASTNode left = root.getChildren().get(0);
        ASTNode right = root.getChildren().get(1);

        // Verhindert Wurzel mit nur einem EXPR-Child (nach oben "hängende" Wurzel)
        if (endOfExpr(right)) {
            root.setName(right.getName());
            root.setValue(right.getValue());
            root.setChildren(left, right.getChildren().get(0));
            return false; // Braucht keine weitere Rotation
        }

        ASTNode insertLeft = new ASTNode(root.getName());
        insertLeft.setValue(right.getValue()); // Operation wird linksvererbt
        insertLeft.setChildren(left, right.getChildren().get(0));

        root.setName(right.getName()); // Value wird nicht gesetzt, da ans linke Kind vererbt
        root.setChildren(insertLeft, right.getChildren().get(1));

        return true;
    }

    private static ASTNode getExpr(ASTNode root) {
        for (ASTNode child : root.getChildren()) {
            if ("EXPR".equals(child.getName())) {
                return child;
            }
        }

        return null;
    }

    private static boolean endOfExpr(ASTNode root) {
        return root.getChildren().size() == 1;
    }

    public static void operatorPrecedence(AST tree) {
        log("Right-rotating expressions for operator-precedence");

        boolean changed;

        do {
            changed = operatorPrecedence(tree.getRoot());
        } while (changed);
    }

    public static boolean operatorPrecedence(ASTNode root) {
        boolean changed = false;

        for (ASTNode child : root.getChildren()) {
            changed = changed || operatorPrecedence(child);

            if (preceding(root, child)) {
                simpleRightRotate(root);
                changed = true;
            }
        }

        return changed;
    }

    private static boolean preceding(ASTNode parent, ASTNode child) {
        if (!"EXPR".equals(parent.getName()) || parent.getValue().isEmpty()
            || !"EXPR".equals(child.getName()) || child.getValue().isEmpty()) {
            return false;
        }

        // Less equals higher
        return priority.get(parent.getValue()) < priority.get(child.getValue());
    }

    private static void simpleRightRotate(ASTNode root) {
        ASTNode left = root.getChildren().get(0);
        ASTNode right = root.getChildren().get(1);

        log("Right-Rotating " + root.getName() + ": " + root.getValue());

        ASTNode insertRight = new ASTNode(root.getName());
        insertRight.setValue(root.getValue());
        insertRight.setChildren(left.getChildren().get(1), right);

        root.setName(left.getName());
        root.setValue(left.getValue());
        root.setChildren(left.getChildren().get(0), insertRight);
    }
}
