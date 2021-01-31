package parser.grammar;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import parser.ParsingTable;

import java.util.Arrays;
import java.util.Collections;
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

        final Set<String> nonterminals;
        final String[] narray = {"S", "E"};
        nonterminals = new HashSet<>(Arrays.asList(narray));

        final Set<String> terminals;
        final String[] tarray = {"a", "b", "e", "i", "t"};
        terminals = new HashSet<>(Arrays.asList(tarray));

        final Set<GrammarRule> rules = new HashSet<>();
        rules.add(new GrammarRule("S", "a"));
        rules.add(new GrammarRule("S", "i", "E", "t", "S"));
        rules.add(new GrammarRule("E", "b"));

        grammar0 = new Grammar(terminals, nonterminals,
                               Collections.emptyMap(), Collections.emptyMap(),
                               Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), rules);
    }

    @BeforeAll
    static void initGrammar1() {
        /*
         Folie 4b/32
         */

        final Set<String> nonterminals;
        final String[] narray = {"S", "T", "E2", "T2", "F"};
        nonterminals = new HashSet<>(Arrays.asList(narray));

        final Set<String> terminals;
        final String[] tarray = {"id", "+", "*", "(", ")"};
        terminals = new HashSet<>(Arrays.asList(tarray));

        final Set<GrammarRule> rules = new HashSet<>();
        rules.add(new GrammarRule("S", "T", "E2"));
        rules.add(new GrammarRule("E2", "+", "T", "E2"));
        rules.add(new GrammarRule("E2", Grammar.EPSILON_SYMBOL));
        rules.add(new GrammarRule("T", "F", "T2"));
        rules.add(new GrammarRule("T2", "*", "F", "T2"));
        rules.add(new GrammarRule("T2", Grammar.EPSILON_SYMBOL));
        rules.add(new GrammarRule("F", "(", "S", ")"));
        rules.add(new GrammarRule("F", "id"));

        grammar1 = new Grammar(terminals, nonterminals,
                               Collections.emptyMap(), Collections.emptyMap(),
                               Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), rules);
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

        final Set<String> nonterminals;
        final String[] narray = {"X", "Y", "S"};
        nonterminals = new HashSet<>(Arrays.asList(narray));

        final Set<String> terminals;
        final String[] tarray = {"a", "c", "d"};
        terminals = new HashSet<>(Arrays.asList(tarray));

        final Set<GrammarRule> rules = new HashSet<>();
        rules.add(new GrammarRule("S", "d"));
        rules.add(new GrammarRule("S", "X", "Y", "S"));
        rules.add(new GrammarRule("Y", Grammar.EPSILON_SYMBOL));
        rules.add(new GrammarRule("Y", "c"));
        rules.add(new GrammarRule("X", "Y"));
        rules.add(new GrammarRule("X", "a"));

        grammar2 = new Grammar(terminals, nonterminals,
                               Collections.emptyMap(), Collections.emptyMap(),
                               Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), rules);
    }

    @Test
    void testFirstGrammar0() {
        final GrammarAnalyzer analyzer = GrammarAnalyzer.fromGrammar(grammar0);

        assertThat(analyzer.getFirst().get("S")).containsOnly("i", "a");
        assertThat(analyzer.getFirst().get("E")).containsOnly("b");
    }

    @Test
    void testFirstGrammar1() {
        final GrammarAnalyzer analyzer = GrammarAnalyzer.fromGrammar(grammar1);

        assertThat(analyzer.getFirst().get("S")).containsOnly("id", "(");
        assertThat(analyzer.getFirst().get("E2")).containsOnly("+", Grammar.EPSILON_SYMBOL);
        assertThat(analyzer.getFirst().get("T")).containsOnly("id", "(");
        assertThat(analyzer.getFirst().get("T2")).containsOnly("*", Grammar.EPSILON_SYMBOL);
        assertThat(analyzer.getFirst().get("F")).containsOnly("id", "(");
    }

    @Test
    void testFirstGrammar2() {
        final GrammarAnalyzer analyzer = GrammarAnalyzer.fromGrammar(grammar2);

        assertThat(analyzer.getFirst().get("X")).containsOnly("c", "a", Grammar.EPSILON_SYMBOL);
        assertThat(analyzer.getFirst().get("Y")).containsOnly("c", Grammar.EPSILON_SYMBOL);
        assertThat(analyzer.getFirst().get("S")).containsOnly("c", "a", "d");
    }

    @Test
    void testFollowGrammar0() {
        final GrammarAnalyzer analyzer = GrammarAnalyzer.fromGrammar(grammar0);

        assertThat(analyzer.getFollow().get("S")).containsOnly("$");
        assertThat(analyzer.getFollow().get("E")).containsOnly("t");
    }

    @Test
    void testFollowGrammar1() {
        final GrammarAnalyzer analyzer = GrammarAnalyzer.fromGrammar(grammar1);

        assertThat(analyzer.getFollow().get("S")).containsOnly(")", "$");
        assertThat(analyzer.getFollow().get("E2")).containsOnly(")", "$");
        assertThat(analyzer.getFollow().get("T")).containsOnly("+", ")", "$");
        assertThat(analyzer.getFollow().get("T2")).containsOnly("+", ")", "$");
        assertThat(analyzer.getFollow().get("F")).containsOnly("+", "*", ")", "$");
    }

    @Test
    void testFollowGrammar2() {
        final GrammarAnalyzer analyzer = GrammarAnalyzer.fromGrammar(grammar2);

        assertThat(analyzer.getFollow().get("X")).containsOnly("a", "c", "d");
        assertThat(analyzer.getFollow().get("Y")).containsOnly("a", "c", "d");
        assertThat(analyzer.getFollow().get("S")).containsOnly("$");
    }

    @Test
    void testTableGrammar1() {
        final GrammarAnalyzer analyzer = GrammarAnalyzer.fromGrammar(grammar1);
        final ParsingTable table = analyzer.getTable();

        assertThat(table.get("S", "id")).isEqualTo("T E2");
        assertThat(table.get("S", "(")).isEqualTo("T E2");
        assertThat(table.get("E2", "+")).isEqualTo("+ T E2");
        assertThat(table.get("E2", ")")).isEqualTo(Grammar.EPSILON_SYMBOL);
        assertThat(table.get("E2", "$")).isEqualTo(Grammar.EPSILON_SYMBOL);
        assertThat(table.get("T", "id")).isEqualTo("F T2");
        assertThat(table.get("T", "(")).isEqualTo("F T2");
        assertThat(table.get("T2", "+")).isEqualTo(Grammar.EPSILON_SYMBOL);
        assertThat(table.get("T2", "*")).isEqualTo("* F T2");
        assertThat(table.get("T2", ")")).isEqualTo(Grammar.EPSILON_SYMBOL);
        assertThat(table.get("T2", "$")).isEqualTo(Grammar.EPSILON_SYMBOL);
        assertThat(table.get("F", "id")).isEqualTo("id");
        assertThat(table.get("F", "(")).isEqualTo("( S )");
    }
}
