package codegen.analysis.liveness;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Repräsentiert eine Variable und ihre Farbe im Interferenzgraph.
 */
public class InterferenceNode {

    private final UUID id;

    /**
     * Der Name der Variable.
     */
    private final String symbol;
    /**
     * Alle Nachbarn dieses Nodes.
     * Benachbart bedeutet, dass beide Variablen zu gleichen Zeiten live sind.
     * Benachbarte Variablen können sich kein Register teilen.
     */
    private final Set<InterferenceNode> neighbours;
    /**
     * Der Integer repräsentiert die "Farbe".
     */
    private int color;

    public InterferenceNode(String symbol) {
        this.id = UUID.randomUUID();
        this.symbol = symbol;
        this.color = 0;
        this.neighbours = new HashSet<>();
    }

    public InterferenceNode(int symbol) {
        this(String.valueOf(symbol));
    }

    // Getters, Setters

    public UUID getId() {
        return this.id;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public Integer getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Set<InterferenceNode> getNeighbourSet() {
        return Collections.unmodifiableSet(this.neighbours);
    }

    public boolean addNeighbour(InterferenceNode node) {
        if (!node.equals(this)) {
            return this.neighbours.add(node);
        }

        return false;
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
