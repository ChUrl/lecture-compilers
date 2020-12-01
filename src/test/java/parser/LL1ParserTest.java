package parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LL1ParserTest {

    private static ILL1ParsingTable table0;
    private static ILL1ParsingTable table1;

    @BeforeAll
    static void setUp() {
        table0 = initTable0();
        table1 = initTable1();
    }

    private static ILL1ParsingTable initTable0() {
        /*
         S -> a
         S -> i E t S
         E -> b
         */
        List<String> nonterminals;
        String[] narray = {"S", "E"};
        nonterminals = Arrays.asList(narray);

        List<String> terminals;
        String[] tarray = {"a", "b", "e", "i", "t"};
        terminals = Arrays.asList(tarray);

        String startSymbol = "S";
        String epsilonSymbol = "epsilon";

        Map<Map.Entry<String, String>, List<String>> map;
        map = new HashMap<>();
        String[] production0 = {"a"};
        map.put(new SimpleEntry<>("S", "a"), Arrays.asList(production0));
        String[] production1 = {"i", "E", "t", "S"};
        map.put(new SimpleEntry<>("S", "i"), Arrays.asList(production1));
        String[] production2 = {"b"};
        map.put(new SimpleEntry<>("E", "b"), Arrays.asList(production2));

        return new LL1ParsingTable(nonterminals, terminals, startSymbol, epsilonSymbol, map);
    }

    private static ILL1ParsingTable initTable1() {
        /*
         Folie 4b/32
         */
        List<String> nonterminals;
        String[] narray = {"E", "T", "E2", "T2", "F"};
        nonterminals = Arrays.asList(narray);

        List<String> terminals;
        String[] tarray = {"id", "+", "*", "(", ")"};
        terminals = Arrays.asList(tarray);

        String startSymbol = "E";
        String epsilonSymbol = "epsilon";

        Map<Map.Entry<String, String>, List<String>> map;
        map = new HashMap<>();
        String[] production0 = {"T", "E2"};
        map.put(new SimpleEntry<>("E", "id"), Arrays.asList(production0));
        String[] production1 = {"T", "E2"};
        map.put(new SimpleEntry<>("E", "("), Arrays.asList(production1));
        String[] production2 = {"+", "T", "E2"};
        map.put(new SimpleEntry<>("E2", "+"), Arrays.asList(production2));
        String[] production3 = {"epsilon"};
        map.put(new SimpleEntry<>("E2", ")"), Arrays.asList(production3));
        String[] production4 = {"epsilon"};
        map.put(new SimpleEntry<>("E2", "$"), Arrays.asList(production4));
        String[] production5 = {"F", "T2"};
        map.put(new SimpleEntry<>("T", "id"), Arrays.asList(production5));
        String[] production6 = {"F", "T2"};
        map.put(new SimpleEntry<>("T", "("), Arrays.asList(production6));
        String[] production7 = {"epsilon"};
        map.put(new SimpleEntry<>("T2", "+"), Arrays.asList(production7));
        String[] production8 = {"*", "F", "T2"};
        map.put(new SimpleEntry<>("T2", "*"), Arrays.asList(production8));
        String[] production9 = {"epsilon"};
        map.put(new SimpleEntry<>("T2", ")"), Arrays.asList(production9));
        String[] production10 = {"epsilon"};
        map.put(new SimpleEntry<>("T2", "$"), Arrays.asList(production10));
        String[] production11 = {"id"};
        map.put(new SimpleEntry<>("F", "id"), Arrays.asList(production11));
        String[] production12 = {"(", "E", ")"};
        map.put(new SimpleEntry<>("F", "("), Arrays.asList(production12));

        return new LL1ParsingTable(nonterminals, terminals, startSymbol, epsilonSymbol, map);
    }

    @Test
    void testIfThenElse() throws MyParseException {
        LL1Parser parser = new LL1Parser(table0);

        String[] token1 = {"i", "b", "t", "a"};
        String[] token2 = {"i", "b", "t", "i", "b", "t", "a"};
        String[] token3 = {"i", "b", "t", "i", "b", "t", "i", "b", "t", "a"};

        assertThat(parser.parse(Arrays.asList(token1))).isTrue();
        assertThat(parser.parse(Arrays.asList(token2))).isTrue();
        assertThat(parser.parse(Arrays.asList(token3))).isTrue();
    }

    @Test
    void testException0() {
        LL1Parser parser = new LL1Parser(table0);
        String[] token1 = {"i", "b", "t"};

        assertThatThrownBy(() -> parser.parse(Arrays.asList(token1))).isInstanceOf(MyParseException.class);
    }

    @Test
    void testException1() {
        LL1Parser parser = new LL1Parser(table0);
        String[] token1 = {"i", "b", "t", "t"};

        assertThatThrownBy(() -> parser.parse(Arrays.asList(token1))).isInstanceOf(MyParseException.class);
    }

    @Test
    void testArithExpression() {
        LL1Parser parser = new LL1Parser(table1);

        String[] token1 = {"id", "+", "id", "*", "id"};
        String[] token2 = {"id", "*", "id", "*", "id"};
        String[] token3 = {"id", "+", "id"};

        assertThatThrownBy(() -> parser.parse(Arrays.asList(token3))).isInstanceOf(MyParseException.class);
        assertThatThrownBy(() -> parser.parse(Arrays.asList(token3))).isInstanceOf(MyParseException.class);
        assertThatThrownBy(() -> parser.parse(Arrays.asList(token3))).isInstanceOf(MyParseException.class);
    }

    @Test
    void testException2() {
        LL1Parser parser = new LL1Parser(table1);
        String[] token1 = {"id", "id"};

        assertThatThrownBy(() -> parser.parse(Arrays.asList(token1))).isInstanceOf(MyParseException.class);
    }
}
