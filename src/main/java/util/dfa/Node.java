package util.dfa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Node implements INode {

    private final String name;
    private final boolean fin;
    private final Map<Character, IEdge> edges = new HashMap<>();

    public Node(String name, boolean fin) {
        this.name = name;
        this.fin = fin;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isFinal() {
        return this.fin;
    }

    @Override
    public void addEdge(IEdge edge) {
        this.edges.put(edge.getChar(), edge);
    }

    @Override
    public Collection<IEdge> getEdges() {
        return this.edges.values();
    }

    @Override
    public INode getNext(char c) {
        return Optional.ofNullable(this.edges.get(c))
                       .map(IEdge::getEnd)
                       .orElseThrow(() -> new DFANoSuchEdgeException("Can't read " + c + " when in " + this.name));
    }

    @Override
    public boolean hasNext(char c) {
        return Optional.ofNullable(this.edges.get(c))
                       .isPresent();
    }
}
