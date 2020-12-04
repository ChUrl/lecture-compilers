package parser;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LL1GrammarAnalyzer {

    private final Set<String> nullable;
    private final Map<String, Set<String>> first;
    private final Map<String, Set<String>> follow;

    private final ILL1ParsingTable table;

    public LL1GrammarAnalyzer(Set<String> terminals, Set<String> nonterminals,
                              String start, String epsilon,
                              Map<String, List<String>> productions) {

        // Es muss zwingend in der Reihenfolge [Nullable < First < Follow < Table] initialisiert werden
        this.nullable = this.initNullable(productions, epsilon);
        this.first = this.initFirst(productions, terminals, nonterminals, epsilon);
        this.follow = this.initFollow(productions, terminals, nonterminals, epsilon);

        this.table = this.initParseTable(productions, terminals, nonterminals, start, epsilon);
    }

    private Set<String> initNullable(Map<String, List<String>> productions, String epsilon) {
        Set<String> nullableOut = new HashSet<>();
        boolean change;

        do {
            change = false;

            for (Map.Entry<String, List<String>> prods : productions.entrySet()) {
                // Für jedes Nichtterminal

                final String leftX = prods.getKey();

                for (String prod : prods.getValue()) {
                    // Für jede Produktionsregel von diesem Nichtterminal
                    // Produktionsregel der Form X -> S1 S2 S3 ... Sk

                    final String[] split = prod.split(" ");

                    boolean allNullable = true; // Sind alle rechten Symbole nullable?
                    for (String rightSi : split) {
                        // Für jedes rechte Symbol dieser Produktionsregel

                        if (!(nullableOut.contains(rightSi) || rightSi.equals(epsilon))) {
                            allNullable = false;
                            break;
                        }
                    }

                    if (!(nullableOut.contains(leftX) || leftX.equals(epsilon)) && allNullable) {
                        // Alle rechten Symbole sind nullable, also ist X nullable

                        change = nullableOut.add(leftX);
                    }
                }
            }
        } while (change);

        return nullableOut;
    }

    public boolean nullable(String sym) {
        return this.nullable.contains(sym);
    }

    public boolean stringNullable(String prod) {
        for (String rightSi : prod.split(" ")) {
            if (!this.nullable.contains(rightSi)) {
                return false;
            }
        }

        return true;
    }

    private Map<String, Set<String>> initFirst(Map<String, List<String>> productions,
                                               Set<String> terminals, Set<String> nonterminals,
                                               String epsilon) {
        Map<String, Set<String>> firstOut = new HashMap<>();
        boolean change;

        for (String sym : nonterminals) {
            // Alle Nichtterminale mit leeren Sets initialisieren

            firstOut.put(sym, new HashSet<>());
        }
        for (String sym : terminals) {
            // Alle Terminale mit der Identität initialisieren

            firstOut.put(sym, new HashSet<>());
            firstOut.get(sym).add(sym);
        }

        do {
            change = false;

            for (Map.Entry<String, List<String>> prods : productions.entrySet()) {
                // Für jedes Nichtterminal

                final String leftX = prods.getKey();

                for (String prod : prods.getValue()) {
                    // Für jede Produktionsregel von diesem Nichtterminal
                    // Produktionsregel der Form X -> S1 S2 S3 ... Sk

                    if (prod.equals(epsilon)) {
                        // Epsilonregeln überspringen

                        continue;
                    }

                    final String[] split = prod.split(" ");

                    // Das First des linken Nichtterminals X enthält das first des ersten rechten Symbols dieser
                    // Produktionsregel S1 (da X -> S1 ... Sk)
                    firstOut.get(leftX).addAll(firstOut.get(split[0]));

                    for (int i = 1; i < split.length; i++) {
                        // Für das 2-te bis k-te rechte Symbol dieser Produktionsregel

                        final String sym = split[i];

                        if (this.nullable(split[i - 1])) {
                            change = firstOut.get(leftX).addAll(firstOut.get(sym));
                        } else {
                            break;
                        }
                    }
                }
            }
        } while (change);

        return firstOut;
    }

    public Set<String> first(String sym) {
        return this.first.get(sym);
    }

    public Set<String> stringFirst(String prod) {
        if (prod.isEmpty()) {
            return Collections.emptySet();
        }

        String front;
        String rest;
        if (prod.indexOf(' ') < 0) {
            front = prod;
            rest = "";
        } else {
            front = prod.substring(0, prod.indexOf(' '));
            rest = prod.substring(prod.indexOf(' ') + 1);
        }

        Set<String> firstOut = new HashSet<>(this.first(front));
        if (this.nullable(front)) {
            firstOut.addAll(this.stringFirst(rest));
        }

        return firstOut;
    }

    private Map<String, Set<String>> initFollow(Map<String, List<String>> productions,
                                                Set<String> terminals, Set<String> nonterminals,
                                                String epsilon) {
        Map<String, Set<String>> followOut = new HashMap<>();
        boolean change;

        for (String sym : terminals) {
            // Alle Nichtterminale mit leeren Sets initialisieren

            followOut.put(sym, new HashSet<>());
        }
        for (String sym : nonterminals) {
            // Alle Terminale mit leeren Sets initialisieren

            followOut.put(sym, new HashSet<>());
        }

        do {
            change = false;

            for (Map.Entry<String, List<String>> prods : productions.entrySet()) {
                // Für jedes Nichtterminal

                final String leftX = prods.getKey();

                for (String prod : prods.getValue()) {
                    // Für jede Produktionsregel von diesem Nichtterminal
                    // Produktionsregel der Form X -> S1 S2 S3 ... Sk

                    final String[] split = prod.split(" ");

                    for (int i = 0; i < split.length - 1; i++) {
                        // Für das 1-te bis vorletzte rechte Symbol dieser Produktionsregel

                        final String sym = split[i];

                        // Das follow des i-ten rechten Symbols dieser Produktionsregel enthält das first des
                        // (i+1)-ten rechten Sybols dieser Produktionsregel
                        followOut.get(sym).addAll(this.first(split[i + 1]));

                        for (int j = i + 2; j < prods.getValue().size(); j++) {
                            // Für das (i+2)-te bis letzte rechte Symbol dieser Produktionsregel

                            boolean allNullable = true; // Sind alle rechten Symbole nullable?
                            for (int k = i + 1; k < j; k++) {
                                // Für das (i+1)-te bis letzte rechte Symbol dieser Produktionsregel

                                if (!this.nullable(split[k])) {
                                    allNullable = false;
                                    break;
                                }
                            }

                            if (allNullable) {
                                // Alle zwischen dem (i+1)-ten und j-ten rechten Symbol dieser Produktionsregel sind
                                // nullable, deshalb enthält follow(Si) auch follow(Sj)

                                change = followOut.get(sym).addAll(this.first(split[j]));
                            }
                        }

                        boolean allNullable = true; // Sind alle rechten Symbole nullable?
                        for (int k = i + 1; k < split.length; k++) {
                            // Für das (i+1)-te bis letzte rechte Symbol dieser Produktionsregel

                            if (!this.nullable(split[k])) {
                                allNullable = false;
                                break;
                            }
                        }

                        if (allNullable) {
                            // Alle zwischen dem (i+1)-ten bis letzten rechten Symbol dieser Produktionsregel sind
                            // nullable, deshalb enthält follow(Si) auch follow(X)

                            change = followOut.get(sym).addAll(followOut.get(leftX));
                        }
                    }

                    // Dem letzten rechten Symbol dieser Produktionsregel wird das follow des linken Nichtterminals
                    // hinzugefügt: follow(Sk) enthält follow(X) (da X -> S1 ... Sk)
                    if (!split[split.length - 1].equals(epsilon)) {
                        //Epsilonregeln überspringen

                        followOut.get(split[split.length - 1]).addAll(followOut.get(leftX));
                    }
                }
            }
        } while (change);

        return followOut;
    }

    public Set<String> follow(String sym) {
        return this.follow.get(sym);
    }

    private ILL1ParsingTable initParseTable(Map<String, List<String>> productions,
                                            Set<String> terminals, Set<String> nonterminals,
                                            String start, String epsilon) {
        Map<Map.Entry<String, String>, String> parseTableOut = new HashMap<>();

        for (String leftX : nonterminals) {
            // Für alle Nichtterminale (Zeilen der Tabelle)

            for (String terminal : terminals) {
                // Für alle Terminale (Spalten der Tabelle)

                final Map.Entry<String, String> cell = new AbstractMap.SimpleEntry<>(leftX, terminal);

                for (String prod : productions.get(leftX)) {
                    // Für jede Produktionsregel für dieses Nichtterminal

                    if (prod.equals(epsilon)) {
                        // Epsilonregeln überspringen

                        continue;
                    }

                    if (this.stringFirst(prod).contains(terminal)
                        || (this.stringNullable(prod) && this.follow(leftX).contains(terminal))) {
                        // Verwende Produktion X -> S1 ... Sk, wenn Eingabe c in first(S1 ... Sk) ist
                        // oder nullable(S1 ... Sk) und Eingabe c in follow(X) ist

                        parseTableOut.put(cell, prod);
                    }
                }
            }
        }

        return new LL1ParsingTable(nonterminals, terminals, start, epsilon, parseTableOut);
    }

    public Set<String> getNullable() {
        return this.nullable;
    }

    public Map<String, Set<String>> getFirst() {
        return this.first;
    }

    public Map<String, Set<String>> getFollow() {
        return this.follow;
    }

    public ILL1ParsingTable getTable() {
        return this.table;
    }
}
