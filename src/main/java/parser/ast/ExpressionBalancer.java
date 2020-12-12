package parser.ast;

public final class ExpressionBalancer {

    private ExpressionBalancer() {}

    // Spiegelt den Baum, da meine Müll-Grammatik die EXPRs rückwärts parst
    public static void flip(AST tree) {
        flip(tree.getRoot());
    }

    private static void flip(Node root) {
        for (Node child : root.getChildren()) {
            flip(child);
        }

        if (root.getChildren().size() == 2) {
            Node left = root.getChildren().get(0);
            Node right = root.getChildren().get(1);

            root.setChildren(right, left);
        }
    }

    // Führt Linksrotationen durch
    // Es werden EXPR-Nodes (2 Childs, 1 davon EXPR, Kein Wert) solange wie möglich linksrotiert
    public static void leftPrecedence(AST tree) {
        boolean change;

        do {
            change = leftPrecedence(tree.getRoot());
        } while (change);
    }

    private static boolean leftPrecedence(Node root) {
        for (Node child : root.getChildren()) {
            leftPrecedence(child);
        }

        Node expr = getExpr(root);

        if (expr == null || root.getChildren().size() != 2 || !root.getValue().isEmpty()) {
            return false;
        }

        leftRotate(root);

        return true;
    }

    private static void leftRotate(Node root) {
        Node left = root.getChildren().get(0);
        Node right = root.getChildren().get(1);

        // Verhindert Wurzel mit nur einem EXPR-Child
        if (endOfExpr(right)) {
            root.setName(right.getName());
            root.setValue(right.getValue());
            root.setChildren(left, right.getChildren().get(0));
            return;
        }

        Node insertLeft = new Node(root.getName());
        insertLeft.setValue(right.getValue());
        insertLeft.setChildren(left, right.getChildren().get(0));

        root.setName(right.getName());
        root.setChildren(insertLeft, right.getChildren().get(1));
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
