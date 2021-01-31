package parser.grammar;

import java.util.Objects;

/**
 * Repräsentation einer Produktionsregel der Form leftside -> rightside.
 */
public class GrammarRule {

    private final String leftside;
    private final String rightside;

    public GrammarRule(String leftside, String... rightside) {
        this.leftside = leftside.trim();
        this.rightside = String.join(" ", rightside).trim();
    }

    public String getLeftside() {
        return this.leftside;
    }

    public String getRightside() {
        return this.rightside;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final GrammarRule that = (GrammarRule) o;
        return this.leftside.equals(that.leftside) && this.rightside.equals(that.rightside);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.leftside, this.rightside);
    }

    @Override
    public String toString() {
        return this.leftside + " -> " + this.rightside;
    }
}
