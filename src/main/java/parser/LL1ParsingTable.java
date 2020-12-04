package parser;

import parser.grammar.Grammar;
import parser.grammar.LL1GrammarAnalyzer;

import java.util.AbstractMap.SimpleEntry;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        Formatter format = new Formatter(output);

        List<String> inputSymbols = this.parsetable.keySet().stream()
                                                   .map(Entry::getValue)
                                                   .distinct()
                                                   .collect(Collectors.toList());

        output.append(" ".repeat(8))
              .append("| ");
        for (String terminal : inputSymbols) {
            format.format("%-9s ", terminal);
        }
        output.append("|\n");

        output.append("-".repeat(8))
              .append("+")
              .append("-".repeat(10 * inputSymbols.size() + 1))
              .append("+")
              .append("\n");

        for (String nonterminal : this.grammar.getNonterminals()) {
            format.format("%-7s | ", nonterminal);

            for (String terminal : inputSymbols) {
                String prod = this.parsetable.get(new SimpleEntry<>(nonterminal, terminal));
                format.format("%-9s ", prod == null ? " ".repeat(9) : prod);
            }
            output.append("|\n");
        }

        format.close();

        return output.toString();
    }
}
