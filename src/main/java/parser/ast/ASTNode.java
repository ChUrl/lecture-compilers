package parser.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ASTNode {

    private String name;
    private String value;
    private List<ASTNode> children = new ArrayList<>();

    public ASTNode(String name) {
        this.name = name;
        this.value = "";
    }

    public void addChild(ASTNode ASTNode) {
        this.children.add(ASTNode);
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public List<ASTNode> getChildren() {
        return this.children;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.value, this.children);
    }

    // toString() und print() von hier: https://stackoverflow.com/a/8948691
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        this.print(buffer, "", "");
        return buffer.toString();
    }

    public void setChildren(List<ASTNode> children) {
        this.children = children;
    }

    public void setChildren(ASTNode... children) {
        this.children = new ArrayList<>();
        this.children.addAll(Arrays.asList(children));
    }

    public String getValue() {
        return this.value;
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(this.name);
        if (!this.value.isBlank()) {
            buffer.append(": ");
            buffer.append(this.value);
        }
        buffer.append('\n');

        for (Iterator<ASTNode> it = this.children.listIterator(); it.hasNext(); ) {
            ASTNode next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASTNode) {
            return this.name.equals(((ASTNode) obj).name)
                   && this.value.equals(((ASTNode) obj).value)
                   && this.children.equals(((ASTNode) obj).children);
        }

        return false;
    }

    public long size() {
        int s = 0;

        for (ASTNode child : this.children) {
            s += child.size();
        }

        return s + 1;
    }
}
