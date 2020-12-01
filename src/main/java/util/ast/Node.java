package util.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node {

    private final String name;
    private final List<Node> children = new ArrayList<>();

    public Node(String name) {
        this.name = name;
    }

    public void addChild(Node node) {
        this.children.add(node);
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            for (int i = 0; i < this.children.size(); i++) {
                if (!this.children.get(i).equals(((Node) obj).children.get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    // toString() und print() von hier: https://stackoverflow.com/a/8948691
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        this.print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(this.name);
        buffer.append('\n');

        for (Iterator<Node> it = this.children.listIterator(); it.hasNext(); ) {
            Node next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
