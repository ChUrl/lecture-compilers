package parser.grammar;

import parser.ILL1ParsingTable;
import parser.LL1ParsingTable;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LL1GrammarAnalyzer {

    private final Grammar grammar;

    private final Map<String, Set<String>> first;
    private final Map<String, Set<String>> follow;

    private final ILL1ParsingTable table;

    public LL1GrammarAnalyzer(Grammar grammar) {
        this.grammar = grammar;

        // Es muss zwingend in der Reihenfolge [Nullable < First < Follow < Table] initialisiert werden
        this.first = this.initFirst();
        this.follow = this.initFollow();

        this.table = this.initParseTable();

        //        System.out.println("First:\n" + this.first);
        //        System.out.println("Follow:\n" + this.follow);
        System.out.println("LL-Table:\n" + this.table);
    }

    private Map<String, Set<String>> initFirst() {
        final Map<String, Set<String>> firstOut = new HashMap<>();

        // Die Methode funktioniert erst, nachdem first initialisiert ist.
        // Deshalb hier doppelt.
        final Predicate<String> nullable = sym -> sym.equals(this.grammar.getEpsilonSymbol())
                                                  || sym.isBlank()
                                                  || firstOut.get(sym).contains(this.grammar.getEpsilonSymbol());
        final Predicate<String[]> allNullable = split -> split.length == 0
                                                         || Arrays.stream(split).allMatch(nullable);

        // Initialisieren
        for (String nterm : this.grammar.getNonterminals()) {
            firstOut.put(nterm, new HashSet<>());
        }
        for (String term : this.grammar.getTerminals()) {
            // 1. If X is a terminal, then first(X) = {X}.

            firstOut.put(term, new HashSet<>());
            firstOut.get(term).add(term);
        }

        boolean change;

        do {
            change = false;

            for (String leftside : this.grammar.getLeftSides()) {
                // 2. (a) If X is a nonterminal...

                for (String rightside : this.grammar.getRightsides(leftside)) {
                    // ...and X -> Y1 Y2 ... Yk is a production...

                    if (!rightside.equals(this.grammar.getEpsilonSymbol())) {
                        // ...for some k >= 1...

                        final String[] split = rightside.split(" ");

                        // !: Dumm implementiert, alles wird mehrfach auf nullable gecheckt:
                        // !: nullable(Y1), nullable(Y1 Y2), nullable(Y1 Y2 Y3)...
                        for (int i = 0; i < split.length; i++) {

                            // All Y1 ... Yi-1
                            final String[] sub = Arrays.copyOfRange(split, 0, i);

                            if (allNullable.test(sub)) {
                                // ...then place a in first(X) if a is in first(Yi) for some i...
                                // ...and epsilon is in all of first(Y1) ... first(Yi-1).

                                // Because a != epsilon
                                Set<String> firstYiNoEps = firstOut.get(split[i]).stream()
                                                                   .filter(sym -> !sym.equals(this.grammar.getEpsilonSymbol()))
                                                                   .collect(Collectors.toSet());

                                change = change || firstOut.get(leftside).addAll(firstYiNoEps);
                            }

                            if (i == split.length - 1 && allNullable.test(split)) {
                                // 2. (b) If epsilon is in first(Y1) ... first(Yk), then add epsilon to first(X).

                                change = change || firstOut.get(leftside).add(this.grammar.getEpsilonSymbol());
                            }
                        }
                    }

                    if (rightside.equals(this.grammar.getEpsilonSymbol())) {
                        // 3. If X -> epsilon is a production, then add epsilon to first(X).

                        change = change || firstOut.get(leftside).add(this.grammar.getEpsilonSymbol());
                    }
                }
            }
        } while (change);

        return firstOut;
    }

    private Map<String, Set<String>> initFollow() {
        final Map<String, Set<String>> followOut = new HashMap<>();

        // Initialisieren
        for (String nterm : this.grammar.getNonterminals()) {
            followOut.put(nterm, new HashSet<>());
        }

        // 1. Place $ in follow(S), where S is the start symbol, and $ is the input right endmarker
        followOut.get(this.grammar.getStartSymbol()).add("$");

        boolean change;

        do {
            change = false;

            for (String leftside : this.grammar.getLeftSides()) {

                for (String rightside : this.grammar.getRightsides(leftside)) {

                    final String[] split = rightside.split(" ");

                    for (int i = 1; i < split.length; i++) {
                        // 2. If there is a production A -> aBb, then everything in first(b) except epsilon
                        //    is in follow(B).

                        if (!this.grammar.getNonterminals().contains(split[i - 1])) {
                            // Follow nur für Nichtterminale berechnen

                            continue;
                        }

                        // !: Hier wird wieder alles doppelt geprüft
                        for (int k = i; k < split.length; k++) {
                            // Behandelt solche Fälle: X -> Y1 Y2 Y3, wo Y2 nullable ist.
                            // Dann beinhaltet follow(Y1) auch first(Y3)

                            final String[] sub = Arrays.copyOfRange(split, i, k);

                            if (this.allNullable(sub)) {

                                final Set<String> firstXkNoEps = this.first(split[k]).stream()
                                                                     .filter(sym -> !sym.equals(this.grammar.getEpsilonSymbol()))
                                                                     .collect(Collectors.toSet());

                                change = change || followOut.get(split[i - 1]).addAll(firstXkNoEps);
                            }
                        }

                        // 3. (b) If there is a production A -> aBb, where b is nullable, then everything in
                        //        follow(A) is in follow(B)
                        final String[] sub = Arrays.copyOfRange(split, i, split.length);

                        if (this.allNullable(sub)) {

                            change = change || followOut.get(split[i - 1]).addAll(followOut.get(leftside));
                        }
                    }

                    if (this.grammar.getNonterminals().contains(split[split.length - 1])) {
                        // 3. (a) If there is a production A -> aB, then everything in follow(A) is in follow(B).

                        change = change || followOut.get(split[split.length - 1]).addAll(followOut.get(leftside));
                    }
                }
            }

        } while (change);

        return followOut;
    }

    private ILL1ParsingTable initParseTable() {
        Map<Map.Entry<String, String>, String> tableOut = new HashMap<>();

        for (String leftside : this.grammar.getLeftSides()) {

            for (String rightside : this.grammar.getRightsides(leftside)) {
                // For each production A -> a of the grammar, do the following:

                final Set<String> firstRightside = this.stringFirst(rightside);

                for (String sym : firstRightside) {
                    // 1. For each terminal t in first(a), add A -> a to table[A, t]

                    tableOut.put(new AbstractMap.SimpleEntry<>(leftside, sym), rightside);
                }

                final Set<String> followLeftside = this.follow(leftside);

                System.out.println(leftside + " -> " + rightside);
                System.out.println("First: " + firstRightside);

                if (firstRightside.contains(this.grammar.getEpsilonSymbol())) {
                    // 2. If epsilon in first(a), then...

                    for (String sym : followLeftside) {
                        // ...for each terminal b in follow(A), add A -> a to table[A, b].

                        tableOut.put(new AbstractMap.SimpleEntry<>(leftside, sym), rightside);
                    }

                    if (followLeftside.contains("$")) {
                        // If epsilon is in first(a) and $ is in follow(A), add A -> a to table[A, $].

                        tableOut.put(new AbstractMap.SimpleEntry<>(leftside, "$"), rightside);
                    }
                }
            }
        }

        return new LL1ParsingTable(this.grammar, tableOut);
    }


    public boolean nullable(String sym) {
        return sym.isBlank()
               || sym.equals(this.grammar.getEpsilonSymbol())
               || this.first.get(sym).contains(this.grammar.getEpsilonSymbol());
    }

    public boolean allNullable(String rightside) {
        return rightside.isBlank()
               || Arrays.stream(rightside.split(" ")).allMatch(this::nullable);
    }

    public boolean allNullable(String[] split) {
        return split.length == 0
               || Arrays.stream(split).allMatch(this::nullable);
    }

    public Set<String> first(String sym) {
        return this.first.get(sym);
    }

    public Set<String> stringFirst(String rightside) {
        return this.stringFirst(rightside.split(" "));
    }

    public Set<String> stringFirst(String[] split) {
        final Set<String> firstOut = new HashSet<>();

        // !: Hier wird wieder doppelt getestet
        for (int i = 0; i < split.length; i++) {
            final String[] sub = Arrays.copyOfRange(split, 0, i);

            if (this.allNullable(sub)) {
                // X1 ... Xi-1 are nullable, so first(X1 ... Xn) contains first(Xi)

                Set<String> firstXiNoEps;
                if (split.length == 1 && split[0].equals(this.grammar.getEpsilonSymbol())) {
                    // Stream collect has to be evaluated, doesn't work on empty stream

                    firstXiNoEps = Collections.emptySet();
                } else {
                    // Only non-epsilon symbols

                    firstXiNoEps = this.first(split[i]).stream()
                                       .filter(sym -> !sym.equals(this.grammar.getEpsilonSymbol()))
                                       .collect(Collectors.toSet());
                }

                firstOut.addAll(firstXiNoEps);

                if (i == split.length - 1) {
                    // Finally, add epsilon to first(X1 X2 ... Xn) if, for all i, epsilon is in first(Xi).

                    firstOut.add(this.grammar.getEpsilonSymbol());
                }
            }
        }

        return firstOut;
    }

    public Set<String> follow(String sym) {
        return this.follow.get(sym);
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
