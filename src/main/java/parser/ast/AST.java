package parser.ast;

import parser.grammar.Grammar;

import java.util.Objects;

public class AST {

    private final Node root;

    public AST(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return this.root;
    }

    public long size() {
        return this.root.size();
    }

    public void preprocess(Grammar grammar) {
        ASTCompacter.clean(this, grammar);
        ExpressionBalancer.balance(this);
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

    @Override
    public int hashCode() {
        return Objects.hash(this.root);
    }
}
