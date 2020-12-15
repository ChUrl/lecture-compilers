package parser.grammar;

import parser.ast.ASTNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static parser.grammar.GrammarAction.DELCHILD;
import static parser.grammar.GrammarAction.DELIFEMPTY;
import static parser.grammar.GrammarAction.NAMETOVAL;
import static parser.grammar.GrammarAction.PROMOTE;
import static parser.grammar.GrammarAction.RENAMETO;
import static parser.grammar.GrammarAction.VALTOVAL;
import static parser.grammar.GrammarAction.values;
import static util.Logger.log;

public class Grammar {

    private final Set<String> terminals;
    private final Set<String> nonterminals;
    private final String startSymbol;
    private final String epsilonSymbol;

    // Actions
    private final Map<GrammarAction, Set<String>> actions;
    private final Map<String, String> renameMappings;
    private final Map<String, List<String>> nameToValMappings;
    private final Map<String, List<String>> valToValMappings;
    private final Map<String, List<String>> delChildMappings;

    private final Set<GrammarRule> rules;

    public Grammar(Set<String> terminals, Set<String> nonterminals,
                   String startSymbol, String epsilonSymbol,
                   Map<GrammarAction, Set<String>> actions,
                   Map<String, String> renameMappings,
                   Map<String, List<String>> nameToValMappings,
                   Map<String, List<String>> valToValMappings,
                   Map<String, List<String>> delChildMappings,
                   Set<GrammarRule> rules) {
        this.terminals = terminals;
        this.nonterminals = nonterminals;
        this.startSymbol = startSymbol;
        this.epsilonSymbol = epsilonSymbol;
        this.actions = actions;
        this.renameMappings = renameMappings;
        this.nameToValMappings = nameToValMappings;
        this.valToValMappings = valToValMappings;
        this.delChildMappings = delChildMappings;
        this.rules = rules;
    }

    public static Grammar fromFile(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        lines = lines.stream()
                     .map(String::trim)
                     .filter(line -> !(line.isBlank() || line.startsWith("//")))
                     .collect(Collectors.toUnmodifiableList());

        try {
            // Grammar
            String startSymbol = "";
            String epsilonSymbol = "";
            final Set<String> terminals = new HashSet<>();
            final Set<String> nonterminals = new HashSet<>();
            final Set<GrammarRule> rules = new HashSet<>();

            // Actions
            final Map<GrammarAction, Set<String>> actions = new EnumMap<>(GrammarAction.class);
            final Map<String, String> renameMappings = new HashMap<>();
            final Map<String, List<String>> nameToValMappings = new HashMap<>();
            final Map<String, List<String>> valToValMappings = new HashMap<>();
            final Map<String, List<String>> delChildMappings = new HashMap<>();

            for (GrammarAction action : GrammarAction.values()) {
                actions.put(action, new HashSet<>());
            }

            // Init for validity check
            final Set<String> actionSet = Arrays.stream(values())
                                                .map(Enum::toString)
                                                .collect(Collectors.toUnmodifiableSet());

            log("Parsing Grammar from File:");
            for (String line : lines) {

                log("Parsed: " + line);

                // Parse Keywords
                if (line.startsWith("START:")) {

                    startSymbol = line.split(" ")[1];
                } else if (line.startsWith("EPS:")) {

                    epsilonSymbol = line.split(" ")[1];
                } else if (line.startsWith("TERM:")) {

                    terminals.addAll(Arrays.stream(line.split(" ")).skip(1).collect(Collectors.toSet()));
                } else if (line.startsWith("NTERM:")) {

                    nonterminals.addAll(Arrays.stream(line.split(" ")).skip(1).collect(Collectors.toSet()));
                } else {
                    // Parse Grammar Rules + Actions

                    // "S[...] -> E T2 | EPS" wird zu leftside = "S[...]" und rightside = "E T2 | eps"
                    final String[] split = line.replaceAll("EPS", epsilonSymbol)
                                               .split("->");
                    String leftside = split[0].trim();
                    final String rightside = split[1].trim();

                    if (leftside.indexOf('[') >= 0 && leftside.indexOf(']') >= 0) {
                        // Handle actions if they are given

                        final int open = leftside.indexOf('[');
                        final int close = leftside.indexOf(']');

                        // Aus "S[C R=...]" wird flags = {"C", "R=..."}
                        final String[] flags = leftside.substring(open + 1, close).split(" ");
                        final Set<String> flagSet = Arrays.stream(flags)
                                                          .map(String::trim)
                                                          .filter(flag -> !flag.isEmpty())
                                                          .collect(Collectors.toUnmodifiableSet());

                        // Check for action validity
                        for (String flag : flagSet) {
                            if (!actionSet.contains(flag.split("=")[0].toUpperCase())) {
                                throw new GrammarParseException("Invalid Action: " + flag);
                            }
                        }

                        // "S[C R=...]" wird zu "S"
                        leftside = leftside.substring(0, open).trim();

                        // Register actions, flagSet = {"C", "R=..."}
                        for (String flag : flagSet) {
                            final String[] flagSplit = flag.split("=");
                            final GrammarAction action = GrammarAction.valueOf(flagSplit[0].toUpperCase());

                            actions.get(action).add(leftside.trim());
                            log("Registered " + flag + ": " + leftside.trim());

                            if (flagSplit.length > 1) {
                                // Handle Action with arguments

                                // "R=A,B,C" -> argSplit = {"A", "B", "C"}
                                final int argStart = flag.indexOf('=');
                                final String[] argSplit = flag.substring(argStart + 1).split(",");

                                switch (action) {
                                    case DELCHILD -> delChildMappings.put(leftside, Arrays.asList(argSplit));
                                    case VALTOVAL -> valToValMappings.put(leftside, Arrays.asList(argSplit));
                                    case NAMETOVAL -> nameToValMappings.put(leftside, Arrays.asList(argSplit));
                                    case RENAMETO -> renameMappings.put(leftside, argSplit[0]);
                                }
                            }
                        }
                    }

                    // "E T2 | epsilon" wird zu prods[0] = "E T2" und prods[1] = "epsilon"
                    final String[] prods = rightside.split("\\|");

                    for (String prod : prods) {
                        final GrammarRule rule = new GrammarRule(leftside, prod.split(" "));
                        rules.add(rule);

                    }
                }
            }

            log("\n" + actions);
            log("-".repeat(100));

            return new Grammar(terminals, nonterminals,
                               startSymbol, epsilonSymbol,
                               actions,
                               renameMappings,
                               nameToValMappings,
                               valToValMappings,
                               delChildMappings,
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

    // Actions ---------------------------------------------------------------------------------------------------------

    /**
     * Es wird nicht root promoted, sondern roots einziges Kind.
     * Checkt auch auf Anzahl der Kinder.
     */
    public boolean canPromoteChild(ASTNode root) {
        return this.canPromoteChild(root.getName())
               && root.getChildren().size() == 1
               && root.getValue().isEmpty();
    }

    private boolean canPromoteChild(String sym) {
        return this.actions.get(PROMOTE).contains(sym);
    }


    /**
     * Checkt auch auf Anzahl der Kinder und vorhandene Value.
     */
    public boolean canDeleteIfEmpty(ASTNode root) {
        return this.canDeleteIfEmpty(root.getName())
               && root.getValue().isEmpty()
               && !root.hasChildren();
    }

    public boolean canDeleteIfEmpty(String sym) {
        return this.actions.get(DELIFEMPTY).contains(sym);
    }


    /**
     * Checkt auch auf Anzahl der Kinder.
     * Epsilon-Knoten werden immer gelöscht.
     */
    public boolean canDeleteChild(ASTNode parent, ASTNode child) {
        return this.canDeleteChild(parent.getName(), child.getName())
               && !child.hasChildren();
    }

    public boolean canDeleteChild(String parent, String child) {
        return (this.actions.get(DELCHILD).contains(parent)
                && this.delChildMappings.get(parent).contains(child))
               || (child.equals(this.epsilonSymbol));
    }


    public boolean canBeRenamed(ASTNode root) {
        return this.canBeRenamed(root.getName());
    }

    public boolean canBeRenamed(String sym) {
        return this.actions.get(RENAMETO).contains(sym);
    }

    public String getNewName(ASTNode root) {
        return this.getNewName(root.getName());
    }

    public String getNewName(String sym) {
        return this.renameMappings.get(sym);
    }


    public boolean hasValToVal(ASTNode parent, ASTNode child) {
        return this.hasValToVal(parent.getName(), child.getName());
    }

    public boolean hasValToVal(String parent, String child) {
        return this.actions.get(VALTOVAL).contains(parent)
               && this.valToValMappings.get(parent).contains(child);
    }


    /**
     * Checkt auch auf bereits existierende Values.
     */
    public boolean canMoveNameToVal(ASTNode parent, ASTNode child) {
        return this.canMoveNameToVal(parent.getName(), child.getName())
               && parent.getValue().isEmpty();
    }

    public boolean canMoveNameToVal(String parent, String child) {
        return this.actions.get(NAMETOVAL).contains(parent)
               && this.nameToValMappings.get(parent).contains(child);
    }
}
