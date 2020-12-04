package parser;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

        String[] productions0 = {"a", "i E t S"};
        String[] productions1 = {"b"};

        Map<String, List<String>> map = new HashMap<>();
        map.put("S", Arrays.asList(productions0));
        map.put("E", Arrays.asList(productions1));

        LL1GrammarAnalyzer analyzer = new LL1GrammarAnalyzer(terminals, nonterminals,
                                                             startSymbol, epsilonSymbol,
                                                             map);

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

        String[] production0 = {"T E2"};
        String[] production1 = {"+ T E2", "epsilon"};
        String[] production2 = {"F T2"};
        String[] production3 = {"* F T2", "epsilon"};
        String[] production4 = {"( E )", "id"};

        Map<String, List<String>> map = new HashMap<>();
        map.put("E", Arrays.asList(production0));
        map.put("E2", Arrays.asList(production1));
        map.put("T", Arrays.asList(production2));
        map.put("T2", Arrays.asList(production3));
        map.put("F", Arrays.asList(production4));

        LL1GrammarAnalyzer analyzer = new LL1GrammarAnalyzer(terminals, nonterminals,
                                                             startSymbol, epsilonSymbol,
                                                             map);

        assertThat(analyzer.getTable().get("F", "id")).isEqualTo("id");
    }
}
