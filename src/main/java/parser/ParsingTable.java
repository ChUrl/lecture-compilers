package parser;

import parser.grammar.Grammar;
import parser.grammar.GrammarAnalyzer;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class ParsingTable {

    private final Grammar grammar;
    private final Map<Entry<String, String>, String> parsetable;

    public ParsingTable(Grammar grammar, Map<Entry<String, String>, String> parsetable) {
        this.grammar = grammar;
        this.parsetable = Collections.unmodifiableMap(parsetable);
    }

    public static ParsingTable fromGrammar(Grammar grammar) {
        final GrammarAnalyzer analyzer = new GrammarAnalyzer(grammar);
        return analyzer.getTable();
    }

    public String get(String nonterminal, String terminal) {
        return this.parsetable.get(new SimpleEntry<>(nonterminal, terminal));
    }

    public String getStartSymbol() {
        return this.grammar.getStartSymbol();
    }

    public Set<String> getNonterminals() {
        return this.grammar.getNonterminals();
    }

    public Set<String> getTerminals() {
        return this.grammar.getTerminals();
    }

    public String getEpsilon() {
        return this.grammar.getEpsilonSymbol();
    }

    @Override
    public String toString() {
        final StringBuilder output = new StringBuilder();
        final Formatter format = new Formatter(output);

        final List<String> inputSymbols = this.parsetable.keySet().stream()
                                                         .map(Entry::getValue)
                                                         .distinct()
                                                         .collect(Collectors.toList());

        // Determine margins (column-sizes)
        final Map<String, Integer> margins = new HashMap<>();
        margins.put("NTERM", 0);
        for (String terminal : inputSymbols) {
            margins.put(terminal, 0);
        }

        for (String nonterminal : this.grammar.getNonterminals()) {

            margins.put("NTERM", Math.max(margins.get("NTERM"), nonterminal.length()));

            for (String terminal : inputSymbols) {
                final String prod = this.parsetable.get(new SimpleEntry<>(nonterminal, terminal));

                final int length;
                if (prod == null) {
                    length = 0;
                } else {
                    length = prod.length();
                }

                margins.put(terminal, Math.max(margins.get(terminal), Math.max(length, terminal.length())));
            }
        }

        output.append(" ".repeat(margins.get("NTERM")))
              .append("| ");
        for (String terminal : inputSymbols) {
            format.format("%-Xs ".replaceAll("X", String.valueOf(margins.get(terminal))), terminal);
        }
        output.append("|\n");

        output.append("-".repeat(margins.get("NTERM")))
              .append("+");
        for (String terminal : inputSymbols) {
            output.append("-".repeat(margins.get(terminal)));
        }
        output.append("-".repeat(inputSymbols.size() + 1))
              .append("+")
              .append("\n");

        for (String nonterminal : this.grammar.getNonterminals()) {
            format.format("%-Xs| ".replaceAll("X", String.valueOf(margins.get("NTERM"))), nonterminal);

            for (String terminal : inputSymbols) {
                final String prod = this.parsetable.get(new SimpleEntry<>(nonterminal, terminal));
                format.format("%-Xs ".replaceAll("X", String.valueOf(margins.get(terminal))), prod == null ? " ".repeat(9) : prod);
            }
            output.append("|\n");
        }

        format.close();

        return output.toString();
    }
}
