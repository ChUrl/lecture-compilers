package parser.grammar;

import org.junit.jupiter.api.Test;
import parser.grammar.Grammar;
import parser.grammar.GrammarRule;
import parser.grammar.LL1GrammarAnalyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LL1GrammarAnalyzerTest {

    @Test
    void testTable0() {
        /*
         S -> a
         S -> i E t S
         E -> b
         */

        Set<String> nonterminals;
        String[] narray = {"S", "E"};
        nonterminals = new HashSet<>(Arrays.asList(narray));

        Set<String> terminals;
        String[] tarray = {"a", "b", "e", "i", "t"};
        terminals = new HashSet<>(Arrays.asList(tarray));

        String startSymbol = "S";
        String epsilonSymbol = "epsilon";

        Set<GrammarRule> rules = new HashSet<>();
        rules.add(new GrammarRule("S", "a"));
        rules.add(new GrammarRule("S", "i", "E", "t", "S"));
        rules.add(new GrammarRule("E", "b"));

        Grammar grammar = new Grammar(terminals, nonterminals,
                                      startSymbol, epsilonSymbol,
                                      rules);

        LL1GrammarAnalyzer analyzer = new LL1GrammarAnalyzer(grammar);

        assertThat(analyzer.getTable().get("S", "a")).isEqualTo("a");
        assertThat(analyzer.getTable().get("S", "i")).isEqualTo("i E t S");
        assertThat(analyzer.getTable().get("E", "b")).isEqualTo("b");
    }

    @Test
    void testTable1() {
        /*
         Folie 4b/32
         */

        Set<String> nonterminals;
        String[] narray = {"E", "T", "E2", "T2", "F"};
        nonterminals = new HashSet<>(Arrays.asList(narray));

        Set<String> terminals;
        String[] tarray = {"id", "+", "*", "(", ")"};
        terminals = new HashSet<>(Arrays.asList(tarray));

        String startSymbol = "E";
        String epsilonSymbol = "epsilon";

        Set<GrammarRule> rules = new HashSet<>();
        rules.add(new GrammarRule("E", "T", "E2"));
        rules.add(new GrammarRule("E2", "+", "T", "E2"));
        rules.add(new GrammarRule("E2", epsilonSymbol));
        rules.add(new GrammarRule("T", "F", "T2"));
        rules.add(new GrammarRule("T2", "*", "F", "T2"));
        rules.add(new GrammarRule("T2", epsilonSymbol));
        rules.add(new GrammarRule("F", "(", "E", ")"));
        rules.add(new GrammarRule("F", "id"));

        Grammar grammar = new Grammar(terminals, nonterminals,
                                      startSymbol, epsilonSymbol,
                                      rules);

        LL1GrammarAnalyzer analyzer = new LL1GrammarAnalyzer(grammar);

        assertThat(analyzer.getTable().get("F", "id")).isEqualTo("id");
    }
}
