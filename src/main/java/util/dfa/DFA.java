package util.dfa;

import java.util.Collection;
import java.util.Set;

public class DFA {

    private final Set<INode> nodes;
    private final INode start;

    public DFA(Set<INode> nodes, INode start, Collection<IEdge> edges) {
        this.nodes = nodes;
        this.start = start;

        for (INode node : nodes) {
            edges.stream()
                 .filter(edge -> edge.getStart() == node)
                 .forEach(node::addEdge);
        }
    }

    public INode getStart() {
        return this.start;
    }

    public Set<INode> getNodes() {
        return this.nodes;
    }

    public String accept(String word) {
        return this.accept(word, this.start);
    }

    private String accept(String input, INode current) {
        if (input.isEmpty()) {
            return current.isFinal()
                    ? current.getName() + " akzeptieren"
                    : current.getName() + " ablehnen";
        }

        char c = input.charAt(0);

        if (!current.hasNext(c)) {
            return current.getName() + " ablehnen";
        }

        return current.getName() + this.accept(input.substring(1), current.getNext(c));
    }
}
