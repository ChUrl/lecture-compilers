package parser.grammar;

import parser.ast.SyntaxTreeNode;
import util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

/**
 * Repräsentiert die Parse-Grammatik und die Kontextaktionen.
 */
public class Grammar {

    // Grammar
    public static final String START_SYMBOL = "S";
    public static final String EPSILON_SYMBOL = "eps";

    private final Set<String> terminals;
    private final Set<String> nonterminals;

    // Actions

    /**
     * Jeder Kontextaktion werden alle leftsides zugewiesen, welche diese Aktion ausführen.
     */
    private final Map<GrammarAction, Set<String>> actionMap;

    /**
     * Jeder leftside mit [renameto=name] wird der entsprechende neue Name zugewiesen.
     */
    private final Map<String, String> renameMappings;

    /**
     * Jeder leftside mit [nametoval=children] werden die entpsrechenden Children zugewiesen,
     * deren Namen in die Parentvalue gschoben werden.
     */
    private final Map<String, List<String>> nameToValMappings;

    /**
     * Jeder leftside mit [valtoval=children] werden die entpsrechenden Children zugewiesen,
     * deren Values in die Parentvalue gschoben werden.
     */
    private final Map<String, List<String>> valToValMappings;

    /**
     * Jeder Leftside mit [delchild=children] werden die entpsrechenden Children zugewiesen, welche entfernt werden.
     */
    private final Map<String, List<String>> delChildMappings;

    /**
     * Die eigentlichen Produktionsregeln der Form leftside -> rightside.
     */
    private final Set<GrammarRule> rules;

    public Grammar(Set<String> terminals, Set<String> nonterminals,
                   Map<GrammarAction, Set<String>> actionMap,
                   Map<String, String> renameMappings,
                   Map<String, List<String>> nameToValMappings,
                   Map<String, List<String>> valToValMappings,
                   Map<String, List<String>> delChildMappings,
                   Set<GrammarRule> rules) {

        this.terminals = Collections.unmodifiableSet(terminals);
        this.nonterminals = Collections.unmodifiableSet(nonterminals);
        this.rules = Collections.unmodifiableSet(rules);

        this.actionMap = Collections.unmodifiableMap(actionMap);
        this.renameMappings = Collections.unmodifiableMap(renameMappings);
        this.nameToValMappings = Collections.unmodifiableMap(nameToValMappings);
        this.valToValMappings = Collections.unmodifiableMap(valToValMappings);
        this.delChildMappings = Collections.unmodifiableMap(delChildMappings);
    }

    public static Grammar fromFile(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        // Remove Whitespace + Comments
        lines = lines.stream()
                     .map(String::trim)
                     .filter(line -> !(line.isBlank() || line.startsWith("//")))
                     .collect(Collectors.toUnmodifiableList());

        // Grammar
        final Set<String> terminals = new HashSet<>();
        final Set<String> nonterminals = new HashSet<>();
        final Set<GrammarRule> rules = new HashSet<>();

        // Actions
        final Map<GrammarAction, Set<String>> actionMap = new EnumMap<>(GrammarAction.class);
        final Map<String, String> renameMappings = new HashMap<>();
        final Map<String, List<String>> nameToValMappings = new HashMap<>();
        final Map<String, List<String>> valToValMappings = new HashMap<>();
        final Map<String, List<String>> delChildMappings = new HashMap<>();

        // Init actionMap
        for (GrammarAction action : GrammarAction.values()) {
            actionMap.put(action, new HashSet<>());
        }

        Logger.logDebug("Beginning grammar parsing", Grammar.class);
        for (String currentLine : lines) {

            Logger.logInfo("Parsing: \"" + currentLine + "\"", Grammar.class);

            // Parse Keywords
            if (currentLine.startsWith("TERM:")) {

                terminals.addAll(Arrays.stream(currentLine.split(" ")).skip(1).collect(Collectors.toSet()));

                Arrays.stream(currentLine.split(" "))
                      .skip(1)
                      .forEach(term -> Logger.logInfo(" :: Registered terminal symbol \"" + term + "\"", Grammar.class));
            } else if (currentLine.startsWith("NTERM:")) {

                nonterminals.addAll(Arrays.stream(currentLine.split(" ")).skip(1).collect(Collectors.toSet()));

                Arrays.stream(currentLine.split(" "))
                      .skip(1)
                      .forEach(nterm -> Logger.logInfo(" :: Registered nonterminal symbol \"" + nterm + "\"", Grammar.class));
            } else {
                // Parse regular lines

                parseRegularLine(currentLine, actionMap,
                                 delChildMappings, valToValMappings, nameToValMappings, renameMappings,
                                 rules);
            }
        }

        Logger.logInfo("Grammar terminals: " + terminals, Grammar.class);
        Logger.logInfo("Grammar nonterminals: " + nonterminals, Grammar.class);
        Logger.logInfo("Grammar productions: " + rules, Grammar.class);
        Logger.logInfo("Grammar actions: " + actionMap, Grammar.class);
        Logger.logDebug("Grammar parsed successfully", Grammar.class);

        return new Grammar(terminals, nonterminals,
                           actionMap, renameMappings, nameToValMappings,
                           valToValMappings, delChildMappings, rules);
    }


    /**
     * Es wird eine normale Zeile der Form leftside[actions] -> rightside geparst.
     * Die Produktionsregeln sowie die Kontextaktionen werden registriert.
     */
    private static void parseRegularLine(String currentLine,
                                         Map<GrammarAction, Set<String>> actions,
                                         Map<String, List<String>> delChildMappings,
                                         Map<String, List<String>> valToValMappings,
                                         Map<String, List<String>> nameToValMappings,
                                         Map<String, String> renameMappings,
                                         Collection<GrammarRule> rules) {

        // "S[...] -> E T2 | eps" wird zu leftside = "S[...]" und rightside = "E T2 | eps"
        final String[] split = currentLine.split("->");
        String leftside = split[0].trim();
        final String rightside = split[1].trim();

        final int open = leftside.indexOf('[');
        final int close = leftside.indexOf(']');

        if (open >= 0 && close >= 0) {
            // Handle actions if they are given

            final Set<String> actionSet = parseActionSet(leftside, open, close);

            // Validate Actions
            throwOnInvalidActionSet(actionSet);

            // "S[C R=...]" wird zu "S"
            leftside = leftside.substring(0, open).trim();

            // Register actions, flagSet = {"C", "R=..."}
            for (String flag : actionSet) {
                registerAction(flag, leftside, actions,
                               delChildMappings, valToValMappings, nameToValMappings, renameMappings);
            }
        }

        registerProductionRules(leftside, rightside, rules);
    }

    /**
     * Es wird die Menge an Kontextaktionen [action1,action2,...] ermittelt.
     */
    private static Set<String> parseActionSet(String leftside, int open, int close) {
        // Aus "S[C R=...]" wird flags = {"C", "R=..."}
        final String[] flags = leftside.substring(open + 1, close).split(" ");

        return Arrays.stream(flags)
                     .map(String::trim)
                     .filter(flag -> !flag.isEmpty())
                     .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Es wird eine beliebige Kontextaktion geparst und der entsprechenden Map hinzugefügt.
     */
    private static void registerAction(String flag, String leftside,
                                       Map<GrammarAction, Set<String>> actions,
                                       Map<String, List<String>> delChildMappings,
                                       Map<String, List<String>> valToValMappings,
                                       Map<String, List<String>> nameToValMappings,
                                       Map<String, String> renameMappings) {

        final String[] flagSplit = flag.split("=");
        final GrammarAction action = GrammarAction.valueOf(flagSplit[0].toUpperCase());

        registerAction(action, leftside, flag, actions);

        if (flagSplit.length > 1) {

            registerActionArguments(flag, action, leftside,
                                    delChildMappings, valToValMappings, nameToValMappings, renameMappings);
        }
    }

    /**
     * Es wird ein Eintrag in der action-Map mit der entsprechenden leftside hinzugefügt.
     */
    private static void registerAction(GrammarAction action, String leftside, String flag,
                                       Map<GrammarAction, Set<String>> actions) {

        actions.get(action).add(leftside.trim());
        Logger.logInfo(" :: Registered action [" + flag + "] for \"" + leftside.trim() + "\"", Grammar.class);
    }

    /**
     * Es wird eine Kontextaktion der Form [action=arguments] geparst und der entsprechenden Map hinzugefügt.
     */
    private static void registerActionArguments(String flag, GrammarAction action, String leftside,
                                                Map<String, List<String>> delChildMappings,
                                                Map<String, List<String>> valToValMappings,
                                                Map<String, List<String>> nameToValMappings,
                                                Map<String, String> renameMappings) {

        // "R=A,B,C" -> argSplit = {"A", "B", "C"}
        final int argStart = flag.indexOf('=');
        final String[] argSplit = flag.substring(argStart + 1).split(",");

        Arrays.stream(argSplit)
              .forEach(arg -> Logger.logInfo(" :: Action has arg " + arg, Grammar.class));

        switch (action) {
            case DELCHILD -> delChildMappings.put(leftside, Arrays.asList(argSplit));
            case VALTOVAL -> valToValMappings.put(leftside, Arrays.asList(argSplit));
            case NAMETOVAL -> nameToValMappings.put(leftside, Arrays.asList(argSplit));
            case RENAMETO -> renameMappings.put(leftside, argSplit[0]);
            default -> throw new GrammarParseException("Unexpected value for arguments: " + action);
        }
    }

    /**
     * Der Regelmenge wird eine neue Regel der Form leftside -> rightside hinzugefügt.
     * Ist rightside dabei verodert, also leftside -> right1 | right2 | right3, dann
     * wird rightside gesplittet.
     */
    private static void registerProductionRules(String leftside, String rightside, Collection<GrammarRule> rules) {
        // "E T2 | epsilon" wird zu prods[0] = "E T2" und prods[1] = "epsilon"
        final String[] prods = rightside.split("\\|");

        for (String prod : prods) {
            final GrammarRule rule = new GrammarRule(leftside, prod.split(" "));
            rules.add(rule);

            Logger.logInfo(" :: Registered production \"" + rule + "\"", Grammar.class);
        }
    }

    private static void throwOnInvalidActionSet(Iterable<String> flagSet) {
        final Set<String> actionSet = Arrays.stream(GrammarAction.values())
                                            .map(Enum::toString)
                                            .collect(Collectors.toUnmodifiableSet());

        for (String flag : flagSet) {
            if (!actionSet.contains(flag.split("=")[0].toUpperCase())) {

                Logger.logError("Action " + flag.split("=")[0] + " is invalid.", Grammar.class);
                throw new GrammarParseException("Invalid Action: " + flag);
            }
        }
    }

    // Getters

    public Set<String> getTerminals() {
        return this.terminals;
    }

    public Set<String> getNonterminals() {
        return this.nonterminals;
    }

    public Set<GrammarRule> getRules() {
        return this.rules;
    }

    /**
     * Ermittelt alle möglichen Produktionen, welche zu einer leftside gehören können.
     */
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
    public boolean canPromoteChild(SyntaxTreeNode root) {
        return this.canPromoteChild(root.getName())
               && root.getChildren().size() == 1
               && root.getValue().isEmpty();
    }

    private boolean canPromoteChild(String rootName) {
        return this.actionMap.get(PROMOTE).contains(rootName);
    }

    /**
     * Checkt auch auf Anzahl der Kinder und vorhandene Value.
     */
    public boolean canDeleteIfEmpty(SyntaxTreeNode root) {
        return this.canDeleteIfEmpty(root.getName())
               && root.getValue().isEmpty()
               && root.isEmpty();
    }

    public boolean canDeleteIfEmpty(String rootName) {
        return this.actionMap.get(DELIFEMPTY).contains(rootName);
    }

    /**
     * Checkt auch auf Anzahl der Kinder.
     * Epsilon-Knoten werden immer gelöscht.
     */
    public boolean canDeleteChild(SyntaxTreeNode parent, SyntaxTreeNode child) {
        return this.canDeleteChild(parent.getName(), child.getName())
               && child.isEmpty();
    }

    public boolean canDeleteChild(String parentName, String childName) {
        return (this.actionMap.get(DELCHILD).contains(parentName)
                && this.delChildMappings.get(parentName).contains(childName))
               || (Grammar.EPSILON_SYMBOL.equals(childName));
    }

    public boolean canBeRenamed(SyntaxTreeNode root) {
        return this.canBeRenamed(root.getName());
    }

    public boolean canBeRenamed(String rootName) {
        return this.actionMap.get(RENAMETO).contains(rootName);
    }

    public String getNewName(SyntaxTreeNode root) {
        return this.getNewName(root.getName());
    }

    public String getNewName(String rootName) {
        return this.renameMappings.get(rootName);
    }

    public boolean hasValToVal(SyntaxTreeNode parent, SyntaxTreeNode child) {
        return this.hasValToVal(parent.getName(), child.getName());
    }

    public boolean hasValToVal(String parentName, String childName) {
        return this.actionMap.get(VALTOVAL).contains(parentName)
               && this.valToValMappings.get(parentName).contains(childName);
    }

    /**
     * Checkt auch auf bereits existierende Values.
     */
    public boolean canMoveNameToVal(SyntaxTreeNode parent, SyntaxTreeNode child) {
        return this.canMoveNameToVal(parent.getName(), child.getName())
               && parent.getValue().isEmpty();
    }

    public boolean canMoveNameToVal(String parentName, String childName) {
        return this.actionMap.get(NAMETOVAL).contains(parentName)
               && this.nameToValMappings.get(parentName).contains(childName);
    }
}
