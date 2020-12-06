package parser.grammar;

import java.util.Objects;

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

    public String[] getSymbols() {
        return this.rightside.split(" ");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GrammarRule) {
            return this.leftside.equals(((GrammarRule) obj).leftside)
                   && this.rightside.equals(((GrammarRule) obj).rightside);
        }

        return false;
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
