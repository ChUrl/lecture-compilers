package parser;

import lexer.StupsLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import parser.grammar.Grammar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LL1ParserTest {

    private static ILL1ParsingTable table0;
    private static ILL1ParsingTable table1;

    private Lexer initLexer(String program) {
        try {
            Path path = Paths.get(this.getClass().getClassLoader().getResource("examplePrograms/" + program).toURI());
            String programCode = Files.readString(path, StandardCharsets.US_ASCII);
            return new StupsLexer(CharStreams.fromString(programCode));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        return null;
    }

    private List<String> getSymbols(Lexer lex) {
        return lex.getAllTokens().stream()
                  .map(tok -> lex.getVocabulary().getSymbolicName(tok.getType()))
                  .collect(Collectors.toUnmodifiableList());
    }

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
        Set<String> nonterminals;
        String[] narray = {"S", "E"};
        nonterminals = new HashSet<>(Arrays.asList(narray));

        Set<String> terminals;
        String[] tarray = {"a", "b", "e", "i", "t"};
        terminals = new HashSet<>(Arrays.asList(tarray));

        String startSymbol = "S";
        String epsilonSymbol = "epsilon";

        Map<Map.Entry<String, String>, String> map;
        map = new HashMap<>();
        String production0 = "a";
        map.put(new SimpleEntry<>("S", "a"), production0);
        String production1 = "i E t S";
        map.put(new SimpleEntry<>("S", "i"), production1);
        String production2 = "b";
        map.put(new SimpleEntry<>("E", "b"), production2);

        Grammar grammar = new Grammar(terminals, nonterminals,
                                      startSymbol, epsilonSymbol,
                                      null);

        return new LL1ParsingTable(grammar, map);
    }

    private static ILL1ParsingTable initTable1() {
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

        Map<Map.Entry<String, String>, String> map;
        map = new HashMap<>();
        String production0 = "T E2";
        map.put(new SimpleEntry<>("E", "id"), production0);
        String production1 = "T E2";
        map.put(new SimpleEntry<>("E", "("), production1);
        String production2 = "+ T E2";
        map.put(new SimpleEntry<>("E2", "+"), production2);
        String production3 = "epsilon";
        map.put(new SimpleEntry<>("E2", ")"), production3);
        String production4 = "epsilon";
        map.put(new SimpleEntry<>("E2", "$"), production4);
        String production5 = "F T2";
        map.put(new SimpleEntry<>("T", "id"), production5);
        String production6 = "F T2";
        map.put(new SimpleEntry<>("T", "("), production6);
        String production7 = "epsilon";
        map.put(new SimpleEntry<>("T2", "+"), production7);
        String production8 = "* F T2";
        map.put(new SimpleEntry<>("T2", "*"), production8);
        String production9 = "epsilon";
        map.put(new SimpleEntry<>("T2", ")"), production9);
        String production10 = "epsilon";
        map.put(new SimpleEntry<>("T2", "$"), production10);
        String production11 = "id";
        map.put(new SimpleEntry<>("F", "id"), production11);
        String production12 = "( E )";
        map.put(new SimpleEntry<>("F", "("), production12);

        Grammar grammar = new Grammar(terminals, nonterminals,
                                      startSymbol, epsilonSymbol,
                                      null);

        return new LL1ParsingTable(grammar, map);
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
    void testIfThenElseFromFile() throws MyParseException, IOException, URISyntaxException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/SimpleGrammar0.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

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

        String[] token1 = {"id", "+", "id"};
        String[] token2 = {"id", "*", "id", "*", "id"};
        String[] token3 = {"id", "+", "id", "*", "id"};

        assertThat(parser.parse(Arrays.asList(token1))).isTrue();
        assertThat(parser.parse(Arrays.asList(token2))).isTrue();
        assertThat(parser.parse(Arrays.asList(token3))).isTrue();
    }

    @Test
    void testArithExpressionFromFile() throws MyParseException, IOException, URISyntaxException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/SimpleGrammar1.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        String[] token1 = {"id", "+", "id"};
        String[] token2 = {"id", "*", "id", "*", "id"};
        String[] token3 = {"id", "+", "id", "*", "id"};

        assertThat(parser.parse(Arrays.asList(token1))).isTrue();
        assertThat(parser.parse(Arrays.asList(token2))).isTrue();
        assertThat(parser.parse(Arrays.asList(token3))).isTrue();
    }

    @Test
    void testException2() {
        LL1Parser parser = new LL1Parser(table1);
        String[] token1 = {"id", "id"};

        assertThatThrownBy(() -> parser.parse(Arrays.asList(token1))).isInstanceOf(MyParseException.class);
    }

    @Test
    void testDanglingElse() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/DanglingElse.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        //        String[] token1 = {"if", "expr", "then", "other"};
        //        String[] token2 = {"if", "expr", "then", "other", "else", "other"};

        //        assertThat(parser.parse(Arrays.asList(token1))).isTrue();
        //        assertThat(parser.parse(Arrays.asList(token2))).isTrue();
    }

    @Test
    void testGrammarEmptyMain() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("exampleGrammars/Grammar.grammar").toURI());
        LL1Parser parser = LL1Parser.fromGrammar(path);

        Lexer lex = this.initLexer("EmptyMain.stups");
        List<String> token = this.getSymbols(lex);

        assertThat(parser.parse(token)).isTrue();
    }
}
