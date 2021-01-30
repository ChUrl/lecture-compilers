package codegen.analysis.liveness;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class InterferenceNode {

    private final String symbol;
    private final Set<InterferenceNode> neighbours;
    private int color;

    public InterferenceNode(String symbol) {
        this.symbol = symbol;
        this.color = 0;
        this.neighbours = new HashSet<>();
    }

    // Getters, Setters

    public String getSymbol() {
        return this.symbol;
    }

    public Integer getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void addNeighbour(InterferenceNode node) {
        if (!node.equals(this)) {
            this.neighbours.add(node);
        }
    }

    public Set<InterferenceNode> getNeighbours() {
        return this.neighbours;
    }

    // Overrides

    @Override
    public int hashCode() {
        return Objects.hash(this.symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final InterferenceNode that = (InterferenceNode) o;
        return this.symbol.equals(that.symbol);
    }

    @Override
    public String toString() {
        return this.symbol;
    }
}
