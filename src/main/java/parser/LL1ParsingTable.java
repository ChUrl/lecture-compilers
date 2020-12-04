package parser;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LL1ParsingTable implements ILL1ParsingTable {

    private final String start;
    private final Set<String> terminals;
    private final Set<String> nonterminals;
    private final String epsilon;
    private final Map<Entry<String, String>, String> parsetable;

    public LL1ParsingTable(Set<String> nonterminals,
                           Set<String> terminals,
                           String start,
                           String epsilon,
                           Map<Entry<String, String>, String> parsetable) {
        this.start = start;
        this.terminals = terminals;
        this.nonterminals = nonterminals;
        this.epsilon = epsilon;
        this.parsetable = parsetable;
    }

    @Override
    public String get(String nonterminal, String terminal) {
        return this.parsetable.get(new SimpleEntry<>(nonterminal, terminal));
    }

    @Override
    public String getStartSymbol() {
        return this.start;
    }

    @Override
    public Set<String> getNonterminals() {
        return this.nonterminals;
    }

    @Override
    public Set<String> getTerminals() {
        return this.terminals;
    }

    @Override
    public String getEpsilon() {
        return this.epsilon;
    }
}
