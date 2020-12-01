package util.dfa;

public interface IEdge {

    INode getStart();

    INode getEnd();

    char getChar();
}
