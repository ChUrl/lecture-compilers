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
            String startSymbol = "";
            String epsilonSymbol = "";
            Set<String> terminals = new HashSet<>();
            Set<String> nonterminals = new HashSet<>();

            Set<GrammarRule> rules = new HashSet<>();
            for (String line : lines) {

                if (line.startsWith("START:")) {

                    startSymbol = line.split(" ")[1];
                } else if (line.startsWith("EPS:")) {

                    epsilonSymbol = line.split(" ")[1];
                } else if (line.startsWith("TERM:")) {

                    terminals.addAll(Arrays.stream(line.split(" ")).skip(1).collect(Collectors.toSet()));
                } else if (line.startsWith("NTERM:")) {

                    nonterminals.addAll(Arrays.stream(line.split(" ")).skip(1).collect(Collectors.toSet()));
                } else {

                    // "S -> E T2 | EPS" wird zu leftside = "S" und rightside = "E T2 | epsilon"
                    String[] split = line.replaceAll("EPS", epsilonSymbol)
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

    public Set<String> getRightsides(String leftside) {
        return this.rules.stream()
                         .filter(rule -> rule.getLeftside().equals(leftside))
                         .map(GrammarRule::getRightside)
                         .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> getLeftSides() {
        return this.rules.stream()
                         .map(GrammarRule::getLeftside)
                         .collect(Collectors.toUnmodifiableSet());
    }
}
