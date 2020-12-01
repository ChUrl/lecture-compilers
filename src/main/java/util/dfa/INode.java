package util.dfa;

import java.util.Collection;

public interface INode {

    String getName();

    boolean isFinal();

    void addEdge(IEdge edge);

    Collection<IEdge> getEdges();

    INode getNext(char c);

    boolean hasNext(char c);
}
