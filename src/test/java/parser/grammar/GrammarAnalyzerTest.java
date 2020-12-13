package parser.grammar;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import parser.ParsingTable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GrammarAnalyzerTest {

    private static Grammar grammar0;
    private static Grammar grammar1;
    private static Grammar grammar2;

    @BeforeAll
    static void initGrammar0() {
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

        grammar0 = new Grammar(terminals, nonterminals, startSymbol, epsilonSymbol, null, null, rules);
    }

    @BeforeAll
    static void initGrammar1() {
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

        grammar1 = new Grammar(terminals, nonterminals, startSymbol, epsilonSymbol, null, null, rules);
    }

    @BeforeAll
    static void initGrammar2() {
        /*
         Z -> d
         Z -> X Y Z
         Y ->
         Y -> c
         X -> Y
         X -> a
         */

        Set<String> nonterminals;
        String[] narray = {"X", "Y", "Z"};
        nonterminals = new HashSet<>(Arrays.asList(narray));

        Set<String> terminals;
        String[] tarray = {"a", "c", "d"};
        terminals = new HashSet<>(Arrays.asList(tarray));

        String startSymbol = "Z";
        String epsilonSymbol = "epsilon";

        Set<GrammarRule> rules = new HashSet<>();
        rules.add(new GrammarRule("Z", "d"));
        rules.add(new GrammarRule("Z", "X", "Y", "Z"));
        rules.add(new GrammarRule("Y", epsilonSymbol));
        rules.add(new GrammarRule("Y", "c"));
        rules.add(new GrammarRule("X", "Y"));
        rules.add(new GrammarRule("X", "a"));

        grammar2 = new Grammar(terminals, nonterminals, startSymbol, epsilonSymbol, null, null, rules);
    }

    @Test
    void testFirstGrammar0() {
        GrammarAnalyzer analyzer = new GrammarAnalyzer(grammar0);

        assertThat(analyzer.getFirst().get("S")).containsOnly("i", "a");
        assertThat(analyzer.getFirst().get("E")).containsOnly("b");
    }

    @Test
    void testFirstGrammar1() {
        GrammarAnalyzer analyzer = new GrammarAnalyzer(grammar1);

        assertThat(analyzer.getFirst().get("E")).containsOnly("id", "(");
        assertThat(analyzer.getFirst().get("E2")).containsOnly("+", grammar1.getEpsilonSymbol());
        assertThat(analyzer.getFirst().get("T")).containsOnly("id", "(");
        assertThat(analyzer.getFirst().get("T2")).containsOnly("*", grammar1.getEpsilonSymbol());
        assertThat(analyzer.getFirst().get("F")).containsOnly("id", "(");
    }

    @Test
    void testFirstGrammar2() {
        GrammarAnalyzer analyzer = new GrammarAnalyzer(grammar2);

        assertThat(analyzer.getFirst().get("X")).containsOnly("c", "a", grammar2.getEpsilonSymbol());
        assertThat(analyzer.getFirst().get("Y")).containsOnly("c", grammar2.getEpsilonSymbol());
        assertThat(analyzer.getFirst().get("Z")).containsOnly("c", "a", "d");
    }

    @Test
    void testFollowGrammar0() {
        GrammarAnalyzer analyzer = new GrammarAnalyzer(grammar0);

        assertThat(analyzer.getFollow().get("S")).containsOnly("$");
        assertThat(analyzer.getFollow().get("E")).containsOnly("t");
    }

    @Test
    void testFollowGrammar1() {
        GrammarAnalyzer analyzer = new GrammarAnalyzer(grammar1);

        assertThat(analyzer.getFollow().get("E")).containsOnly(")", "$");
        assertThat(analyzer.getFollow().get("E2")).containsOnly(")", "$");
        assertThat(analyzer.getFollow().get("T")).containsOnly("+", ")", "$");
        assertThat(analyzer.getFollow().get("T2")).containsOnly("+", ")", "$");
        assertThat(analyzer.getFollow().get("F")).containsOnly("+", "*", ")", "$");
    }

    @Test
    void testFollowGrammar2() {
        GrammarAnalyzer analyzer = new GrammarAnalyzer(grammar2);

        assertThat(analyzer.getFollow().get("X")).containsOnly("a", "c", "d");
        assertThat(analyzer.getFollow().get("Y")).containsOnly("a", "c", "d");
        assertThat(analyzer.getFollow().get("Z")).containsOnly("$");
    }

    @Test
    void testTableGrammar1() {
        GrammarAnalyzer analyzer = new GrammarAnalyzer(grammar1);
        ParsingTable table = analyzer.getTable();

        assertThat(table.get("E", "id")).isEqualTo("T E2");
        assertThat(table.get("E", "(")).isEqualTo("T E2");
        assertThat(table.get("E2", "+")).isEqualTo("+ T E2");
        assertThat(table.get("E2", ")")).isEqualTo(grammar1.getEpsilonSymbol());
        assertThat(table.get("E2", "$")).isEqualTo(grammar1.getEpsilonSymbol());
        assertThat(table.get("T", "id")).isEqualTo("F T2");
        assertThat(table.get("T", "(")).isEqualTo("F T2");
        assertThat(table.get("T2", "+")).isEqualTo(grammar1.getEpsilonSymbol());
        assertThat(table.get("T2", "*")).isEqualTo("* F T2");
        assertThat(table.get("T2", ")")).isEqualTo(grammar1.getEpsilonSymbol());
        assertThat(table.get("T2", "$")).isEqualTo(grammar1.getEpsilonSymbol());
        assertThat(table.get("F", "id")).isEqualTo("id");
        assertThat(table.get("F", "(")).isEqualTo("( E )");
    }
}
