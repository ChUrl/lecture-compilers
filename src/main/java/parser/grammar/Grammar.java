package parser.grammar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Grammar {

    private final Set<String> terminals;
    private final Set<String> nonterminals;
    private final String startSymbol;
    private final String epsilonSymbol;

    private final Set<GrammarRule> rules;

    public Grammar(Set<String> terminals, Set<String> nonterminals,
                   String startSymbol, String epsilonSymbol,
                   Set<GrammarRule> rules) {
        this.terminals = terminals;
        this.nonterminals = nonterminals;
        this.startSymbol = startSymbol;
        this.epsilonSymbol = epsilonSymbol;
        this.rules = rules;
    }

    public static Grammar fromFile(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        lines = lines.stream().filter(line -> !(line.isBlank() || line.startsWith("//")))
                     .collect(Collectors.toUnmodifiableList());

        try {
            String startSymbol = lines.get(0).split(" ")[1];
            String epsilonSymbol = lines.get(1).split(" ")[1];

            String[] term = lines.get(2).split(" ");
            term = Arrays.copyOfRange(term, 1, term.length);
            Set<String> terminals = new HashSet<>(Arrays.asList(term));

            String[] nterm = lines.get(3).split(" ");
            nterm = Arrays.copyOfRange(nterm, 1, nterm.length);
            Set<String> nonterminals = new HashSet<>(Arrays.asList(nterm));

            Set<GrammarRule> rules = new HashSet<>();
            for (int i = 4; i < lines.size(); i++) {
                // "S -> E T2 | EPS" wird zu leftside = "S" und rightside = "E T2 | epsilon"
                String[] split = lines.get(i)
                                      .replaceAll("EPS", epsilonSymbol)
                                      .split("->");

                String leftside = split[0].trim();
                String rightside = split[1].trim();

                // "E T2 | epsilon" wird zu prods[0] = "E T2" und prods[1] = "epsilon"
                String[] prods = rightside.split("\\|");

                for (String prod : prods) {
                    GrammarRule rule = new GrammarRule(leftside, prod.split(" "));
                    rules.add(rule);
                }
            }

            return new Grammar(terminals, nonterminals,
                               startSymbol, epsilonSymbol,
                               rules);
        } catch (Exception e) {
            System.out.println("Die Grammatik kann nicht gelesen werden!");
            System.out.println(path);
            e.printStackTrace();
        }

        return null;
    }

    public Set<String> getTerminals() {
        return this.terminals;
    }

    public Set<String> getNonterminals() {
        return this.nonterminals;
    }

    public String getStartSymbol() {
        return this.startSymbol;
    }

    public String getEpsilonSymbol() {
        return this.epsilonSymbol;
    }

    public Set<GrammarRule> getRules() {
        return this.rules;
    }
}
