package parser;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LL1ParsingTable implements ILL1ParsingTable {

    private final String start;
    private final List<String> terminals;
    private final List<String> nonterminals;
    private final String epsilon;
    private final Map<Entry<String, String>, ? extends List<String>> parsetable;

    public LL1ParsingTable(List<String> nonterminals,
                           List<String> terminals,
                           String start,
                           String epsilon,
                           Map<Entry<String, String>, ? extends List<String>> parsetable) {
        this.start = start;
        this.terminals = terminals;
        this.nonterminals = nonterminals;
        this.epsilon = epsilon;
        this.parsetable = parsetable;
    }

    @Override
    public List<String> get(String nonterminal, String terminal) {
        return this.parsetable.get(new SimpleEntry<>(nonterminal, terminal));
    }

    @Override
    public String getStartSymbol() {
        return this.start;
    }

    @Override
    public List<String> getNonterminals() {
        return this.nonterminals;
    }

    @Override
    public List<String> getTerminals() {
        return this.terminals;
    }

    @Override
    public String getEpsilon() {
        return this.epsilon;
    }
}
