package parser.ast;

import parser.grammar.Grammar;

import java.util.Objects;

/**
 * Repräsentiert konkrete und abstrakte Parse-/Syntaxbäume.
 */
public class SyntaxTree {

    private final SyntaxTreeNode root;

    public SyntaxTree(SyntaxTreeNode root) {
        this.root = root;
    }

    /**
     * Formt einen konkreten Parsebaum in einein abstrakten SyntaxTree um.
     *
     * @param grammar Die Parsegrammatik wird benötigt um die Umformungen durchzuführen.
     */
    public static SyntaxTree toAbstractSyntaxTree(SyntaxTree concreteSyntaxTree, Grammar grammar) {
        final SyntaxTree abstractSyntaxTree = concreteSyntaxTree.deepCopy();

        ParseTreeCleaner.clean(abstractSyntaxTree, grammar);
        SyntaxTreeRebalancer.rebalance(abstractSyntaxTree);
        System.out.println("Tree processing successful.");

        return abstractSyntaxTree;
    }

    public SyntaxTree deepCopy() {
        return new SyntaxTree(this.root.deepCopy());
    }

    // Getters

    public SyntaxTreeNode getRoot() {
        return this.root;
    }

    public long size() {
        return this.root.size();
    }

    public boolean isEmpty() {
        return this.root.isEmpty();
    }

    // Overrides

    @Override
    public int hashCode() {
        return Objects.hash(this.root);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SyntaxTree that = (SyntaxTree) o;
        return this.root.equals(that.root);
    }

    @Override
    public String toString() {
        return this.root.toString();
    }
}
