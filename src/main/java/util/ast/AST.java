package util.ast;

public class AST {

    private final Node root;

    public AST(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return this.root;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AST) {
            return this.root.equals(((AST) obj).root);
        }

        return false;
    }

    @Override
    public String toString() {
        return this.root.toString();
    }
}
