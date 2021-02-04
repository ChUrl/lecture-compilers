package parser.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repräsentiert einen Token aus dem Quellprogramm im Parsebaum,
 * oder eine abstrakte Anweisung im Abstrakten Syntaxbaum.
 */
public class SyntaxTreeNode {

    private final UUID id;
    private final int line;
    private String name;
    private String value;
    private List<SyntaxTreeNode> children = new ArrayList<>();

    public SyntaxTreeNode(String name, int line) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.line = line;
        this.value = "";
    }

    public SyntaxTreeNode deepCopy() {
        final SyntaxTreeNode newNode = new SyntaxTreeNode(this.name, this.line);

        newNode.value = this.value;
        newNode.children = this.children.stream()
                                        .map(SyntaxTreeNode::deepCopy)
                                        .collect(Collectors.toList());

        return newNode;
    }

    // Getters, Setters

    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    public long size() {
        return 1 + this.children.stream().mapToLong(SyntaxTreeNode::size).sum();
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLine() {
        return this.line;
    }

    public List<SyntaxTreeNode> getChildren() {
        return this.children;
    }

    public void setChildren(List<SyntaxTreeNode> children) {
        this.children = children;
    }

    public void setChildren(SyntaxTreeNode... children) {
        this.children = new ArrayList<>();
        this.children.addAll(Arrays.asList(children));
    }

    public void addChild(SyntaxTreeNode syntaxTreeNode) {
        this.children.add(syntaxTreeNode);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return this.id;
    }

    // Printing

    // toString() und treePrint() von hier: https://stackoverflow.com/a/8948691
    private void treePrint(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(this.name);
        if (!this.value.isBlank()) {
            buffer.append(": ");
            buffer.append(this.value);
        }
        buffer.append('\n');

        for (final Iterator<SyntaxTreeNode> it = this.children.listIterator(); it.hasNext(); ) {
            final SyntaxTreeNode next = it.next();
            if (it.hasNext()) {
                next.treePrint(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.treePrint(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }

    public String nodePrint(String prefix) {
        return prefix + this.name + ": " + this.value + "\n"
               + prefix + this.children.stream()
                                       .map(child -> prefix + "└── " + child.name + ": " + child.value + "\n")
                                       .collect(Collectors.joining()).trim();
    }

    // Overrides

    @Override
    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    public int hashCode() {
        return Objects.hash(this.line, this.name, this.value, this.children);
    }

    @Override
    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SyntaxTreeNode that = (SyntaxTreeNode) o;
        return this.line == that.line && this.name.equals(that.name)
               && this.value.equals(that.value) && this.children.equals(that.children);
    }

    // toString() und treePrint() von hier: https://stackoverflow.com/a/8948691
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.treePrint(buffer, "", "");
        return buffer.toString();
    }
}
