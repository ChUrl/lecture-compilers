package parser;

import parser.grammar.Grammar;
import parser.grammar.LL1GrammarAnalyzer;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LL1ParsingTable implements ILL1ParsingTable {

    private final Grammar grammar;
    private final Map<Entry<String, String>, String> parsetable;

    public LL1ParsingTable(Grammar grammar, Map<Entry<String, String>, String> parsetable) {
        this.grammar = grammar;
        this.parsetable = parsetable;
    }

    public static ILL1ParsingTable fromGrammar(Grammar grammar) {
        LL1GrammarAnalyzer analyzer = new LL1GrammarAnalyzer(grammar);
        return analyzer.getTable();
    }

    @Override
    public String get(String nonterminal, String terminal) {
        return this.parsetable.get(new SimpleEntry<>(nonterminal, terminal));
    }

    @Override
    public String getStartSymbol() {
        return this.grammar.getStartSymbol();
    }

    @Override
    public Set<String> getNonterminals() {
        return this.grammar.getNonterminals();
    }

    @Override
    public Set<String> getTerminals() {
        return this.grammar.getTerminals();
    }

    @Override
    public String getEpsilon() {
        return this.grammar.getEpsilonSymbol();
    }
}
