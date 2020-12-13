package parser.ast;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static util.Logger.log;

public final class ASTBalancer {

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
            if (child.getName().equals("EXPR")) {
                return child;
            }
        }

        return null;
    }

    private static boolean endOfExpr(ASTNode root) {
        return root.getChildren().size() == 1;
    }

    // 0 - Unary: -, +, !
    // 1 - Multiplicative: *, /, %
    // 2 - Additive: +, -
    // 3 - Comparative: <, <=, >, >=
    // 4 - Equality: ==, !=
    // 5 - Logical AND: &&
    // 6 - Logical OR: ||
    public static void operatorPrecedence(AST tree) {
        log("Right-rotating expressions for operator-precedence");
        operatorPrecedence(tree.getRoot());
    }

    public static void operatorPrecedence(ASTNode root) {
        for (ASTNode child : root.getChildren()) {
            operatorPrecedence(child);
        }

        if (preceding(root)) {
            simpleRightRotate(root);
        }
    }

    private static boolean preceding(ASTNode root) {
        ASTNode op = getExpr(root);

        if (op == null || !root.getName().equals("EXPR") || root.getValue().isEmpty()) {
            return false;
        }

        Collection<String> arithHigh = Arrays.asList("MUL", "DIV", "MOD");
        Collection<String> arithLow = Arrays.asList("ADD", "SUB");
        Collection<String> logHigh = Arrays.asList("AND");
        Collection<String> logLow = Arrays.asList("OR");

        return (arithHigh.contains(root.getValue()) && arithLow.contains(op.getValue()))
               || (logHigh.contains(root.getValue()) && logLow.contains(op.getValue()));
    }

    private static void simpleRightRotate(ASTNode root) {
        ASTNode left = root.getChildren().get(0);
        ASTNode right = root.getChildren().get(1);

        ASTNode insertRight = new ASTNode(root.getName());
        insertRight.setValue(root.getValue());
        insertRight.setChildren(left.getChildren().get(1), right);

        root.setName(left.getName());
        root.setValue(left.getValue());
        root.setChildren(left.getChildren().get(0), insertRight);
    }
}
