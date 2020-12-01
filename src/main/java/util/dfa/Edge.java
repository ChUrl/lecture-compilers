package util.dfa;

public class Edge implements IEdge {

    private final INode start;
    private final INode end;
    private final char read;

    public Edge(INode start, char read, INode end) {
        this.start = start;
        this.read = read;
        this.end = end;
    }

    @Override
    public INode getStart() {
        return this.start;
    }

    @Override
    public INode getEnd() {
        return this.end;
    }

    @Override
    public char getChar() {
        return this.read;
    }
}
