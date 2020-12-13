package parser.grammar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static parser.grammar.GrammarActions.COMPACT;
import static parser.grammar.GrammarActions.NULLABLE;
import static util.Logger.log;

public class Grammar {

    private final Set<String> terminals;
    private final Set<String> nonterminals;
    private final String startSymbol;
    private final String epsilonSymbol;

    // Actions
    private final Set<String> compact;
    private final Set<String> nullable;

    private final Set<GrammarRule> rules;

    public Grammar(Set<String> terminals, Set<String> nonterminals,
                   String startSymbol, String epsilonSymbol,
                   Set<String> compact, Set<String> nullable,
                   Set<GrammarRule> rules) {
        this.terminals = terminals;
        this.nonterminals = nonterminals;
        this.startSymbol = startSymbol;
        this.epsilonSymbol = epsilonSymbol;
        this.compact = compact;
        this.nullable = nullable;
        this.rules = rules;
    }

    public static Grammar fromFile(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        lines = lines.stream()
                     .map(String::trim)
                     .filter(line -> !(line.isBlank() || line.startsWith("//")))
                     .collect(Collectors.toUnmodifiableList());

        try {
            String startSymbol = "";
            String epsilonSymbol = "";
            Set<String> terminals = new HashSet<>();
            Set<String> nonterminals = new HashSet<>();

            Set<String> compact = new HashSet<>();
            Set<String> nullable = new HashSet<>();
            Set<GrammarRule> rules = new HashSet<>();

            log("Parsing Grammar from File:");
            for (String line : lines) {

                log("Parsed: " + line);

                if (line.startsWith("START:")) {

                    startSymbol = line.split(" ")[1];
                } else if (line.startsWith("EPS:")) {

                    epsilonSymbol = line.split(" ")[1];
                } else if (line.startsWith("TERM:")) {

                    terminals.addAll(Arrays.stream(line.split(" ")).skip(1).collect(Collectors.toSet()));
                } else if (line.startsWith("NTERM:")) {

                    nonterminals.addAll(Arrays.stream(line.split(" ")).skip(1).collect(Collectors.toSet()));
                } else {
                    // "S[] -> E T2 | EPS" wird zu leftside = "S[]" und rightside = "E T2 | epsilon"
                    String[] split = line.replaceAll("EPS", epsilonSymbol)
                                         .split("->");

                    String leftside = split[0].trim();
                    String rightside = split[1].trim();

                    if (leftside.indexOf('[') >= 0) {
                        // Handle actions if they exist

                        int open = leftside.indexOf('[');
                        int close = leftside.indexOf(']');

                        // Aus "S[C R]" wird flags = {"C", "R"} extrahiert
                        String[] flags = leftside.substring(open + 1, close).split(" ");
                        List<String> flagList = Arrays.stream(flags)
                                                      .map(String::trim)
                                                      .filter(flag -> !flag.isEmpty())
                                                      .collect(Collectors.toList());

                        // Check for action validity
                        List<String> enumActions = Arrays.stream(GrammarActions.values())
                                                         .map(action -> action.toString().toLowerCase())
                                                         .collect(Collectors.toList());
                        for (String flag : flagList) {
                            if (!enumActions.contains(flag)) {
                                throw new GrammarParseException("Falsche Action in Grammatik");
                            }
                        }

                        // "S[C R]" wird zu "S"
                        leftside = leftside.substring(0, open).trim();

                        // Register action
                        if (flagList.contains(COMPACT.toString().toLowerCase())) {
                            compact.add(leftside.trim());
                            log("Registered compact: " + leftside.trim());
                        }
                        if (flagList.contains(NULLABLE.toString().toLowerCase())) {
                            nullable.add(leftside.trim());
                            log("Registered nullable: " + leftside.trim());
                        }
                    }

                    // "E T2 | epsilon" wird zu prods[0] = "E T2" und prods[1] = "epsilon"
                    String[] prods = rightside.split("\\|");

                    for (String prod : prods) {
                        GrammarRule rule = new GrammarRule(leftside, prod.split(" "));
                        rules.add(rule);

                    }
                }
            }

            log("\n" + compact);
            log("-".repeat(100));

            return new Grammar(terminals, nonterminals,
                               startSymbol, epsilonSymbol,
                               compact, nullable,
                               rules);
        } catch (Exception e) {
            log("Die Grammatik kann nicht gelesen werden!");
            log(path.toString());
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

    public boolean hasCompact(String leftside) {
        return this.compact != null && this.compact.contains(leftside);
    }

    public boolean hasNullable(String leftside) {
        return this.nullable != null && this.nullable.contains(leftside);
    }
}
