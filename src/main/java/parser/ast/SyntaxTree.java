package parser.ast;

import parser.grammar.Grammar;
import util.GraphvizCaller;

import java.util.ArrayDeque;
import java.util.Deque;
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

    public String printToImage(String filename) {
        if (this.isEmpty()) {
            return "Empty tree can't be exported to image: " + filename + ".svg";
        }

        final Deque<SyntaxTreeNode> stack = new ArrayDeque<>();
        final StringBuilder dot = new StringBuilder();

        dot.append("digraph tree {\n")
           .append("node[shape=Mrecord]\n");

        stack.push(this.root);
        while (!stack.isEmpty()) {
            final SyntaxTreeNode current = stack.pop();

            dot.append("\"").append(current.getId()).append("\"")
               .append(" [label=\"{<f0> ")
               .append(current.getName()
                              .replace("\"", "\\\"")
                              .replace("<", "less")
                              .replace(">", "greater"))
               .append("|<f1> ")
               .append(current.getValue()
                              .replace("\"", "\\\"")
                              .replace("<", "less")
                              .replace(">", "greater"))
               .append("}\"];\n");

            current.getChildren().forEach(stack::push);
        }

        stack.push(this.root);
        while (!stack.isEmpty()) {
            final SyntaxTreeNode current = stack.pop();

            for (SyntaxTreeNode child : current.getChildren()) {
                dot.append("\"").append(current.getId()).append("\"")
                   .append(" -> ")
                   .append("\"").append(child.getId()).append("\"")
                   .append("\n");
            }

            current.getChildren().forEach(stack::push);
        }

        dot.append("}");

        GraphvizCaller.callGraphviz(dot, filename);

        return "Successfully generated image of syntax-tree: " + filename + ".svg";
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
