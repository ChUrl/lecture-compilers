package parser.grammar;

public class GrammarRule {

    private final String leftside;
    private final String rightside;

    public GrammarRule(String leftside, String... rightside) {
        this.leftside = leftside;
        this.rightside = String.join(" ", rightside);
    }

    public String getLeftside() {
        return this.leftside;
    }

    public String getRightside() {
        return this.rightside;
    }
}
