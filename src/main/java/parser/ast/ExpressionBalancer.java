package parser.ast;

import java.util.Collections;

import static util.Logger.log;

public final class ExpressionBalancer {

    private ExpressionBalancer() {}

    public static void balance(AST tree) {
        flip(tree);
        leftPrecedence(tree);
        operatorPrecedence(tree);

        log(tree.toString());
        log("-".repeat(100));
    }

    // Baum spiegeln, damit höhere Ebenen links sind und EXPR vorwärts laufen
    public static void flip(AST tree) {
        log("Flipping expressions");
        flip(tree.getRoot());
    }

    private static void flip(Node root) {
        for (Node child : root.getChildren()) {
            flip(child);
        }

        Collections.reverse(root.getChildren());
    }

    // Führt Linksrotationen durch
    // Es werden EXPR-Nodes (2 Childs, 1 davon EXPR, Kein Wert) solange wie möglich linksrotiert
    public static void leftPrecedence(AST tree) {
        log("Rotating expressions for left-precedence");
        leftPrecedence(tree.getRoot());
    }

    // Es wird solange rotiert bis die letzte "Rotation" durchgeführt wurde
    private static void leftPrecedence(Node root) {
        for (Node child : root.getChildren()) {
            leftPrecedence(child);
        }

        Node expr = getExpr(root);

        if (expr == null || root.getChildren().size() != 2 || !root.getValue().isEmpty()) {
            return;
        }

        boolean change;

        do {
            change = leftRotate(root);
        } while (change);
    }

    // Die Letzte Rotation ist keine richtige Rotation, dort wird false zurückgegeben
    private static boolean leftRotate(Node root) {
        Node left = root.getChildren().get(0);
        Node right = root.getChildren().get(1);

        // Verhindert Wurzel mit nur einem EXPR-Child
        if (endOfExpr(right)) {
            root.setName(right.getName());
            root.setValue(right.getValue());
            root.setChildren(left, right.getChildren().get(0));
            return false;
        }

        Node insertLeft = new Node(root.getName());
        insertLeft.setValue(right.getValue());
        insertLeft.setChildren(left, right.getChildren().get(0));

        root.setName(right.getName());
        root.setChildren(insertLeft, right.getChildren().get(1));

        return true;
    }

    private static Node getExpr(Node root) {
        for (Node child : root.getChildren()) {
            if (child.getName().equals("EXPR")) {
                return child;
            }
        }

        return null;
    }

    private static boolean endOfExpr(Node root) {
        return root.getChildren().size() == 1;
    }

    public static void operatorPrecedence(AST tree) {

    }
}
