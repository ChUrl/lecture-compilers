package typechecker;

import parser.ast.SyntaxTree;
import parser.ast.SyntaxTreeNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.Logger.log;

/**
 * Speichert die Datentypen von Symbolen und Funktionen in einem Programm.
 */
public final class TypeTable {

    /**
     * Weist jeder deklarierter Variable ihren Typ zu.
     */
    private final Map<String, String> symbolTable;

    /**
     * Weist jedem Operator einen Rückgabetyp zu.
     */
    private final Map<String, String> methodReturnTable;

    /**
     * Weist jedem Operator die Typen seiner Argumente zu.
     */
    private final Map<String, List<String>> methodArgumentTable;

    private TypeTable(Map<String, String> symbolTable) {
        this.symbolTable = Collections.unmodifiableMap(symbolTable);

        // Enthält die Return-Types der Operatoren

        this.methodReturnTable = Map.ofEntries(Map.entry("ADD", "INTEGER_TYPE"),
                                               Map.entry("SUB", "INTEGER_TYPE"),
                                               Map.entry("MUL", "INTEGER_TYPE"),
                                               Map.entry("DIV", "INTEGER_TYPE"),
                                               Map.entry("MOD", "INTEGER_TYPE"),
                                               Map.entry("NOT", "BOOLEAN_TYPE"),
                                               Map.entry("AND", "BOOLEAN_TYPE"),
                                               Map.entry("OR", "BOOLEAN_TYPE"),
                                               Map.entry("LESS", "BOOLEAN_TYPE"),
                                               Map.entry("LESS_EQUAL", "BOOLEAN_TYPE"),
                                               Map.entry("GREATER", "BOOLEAN_TYPE"),
                                               Map.entry("GREATER_EQUAL", "BOOLEAN_TYPE"),
                                               Map.entry("EQUAL", "BOOLEAN_TYPE"),
                                               Map.entry("NOT_EQUAL", "BOOLEAN_TYPE"));

        this.methodArgumentTable = Map.ofEntries(Map.entry("ADD", Collections.singletonList("INTEGER_TYPE")),
                                                 Map.entry("SUB", Collections.singletonList("INTEGER_TYPE")),
                                                 Map.entry("MUL", Collections.singletonList("INTEGER_TYPE")),
                                                 Map.entry("DIV", Collections.singletonList("INTEGER_TYPE")),
                                                 Map.entry("MOD", Collections.singletonList("INTEGER_TYPE")),
                                                 Map.entry("AND", Collections.singletonList("BOOLEAN_TYPE")),
                                                 Map.entry("OR", Collections.singletonList("BOOLEAN_TYPE")),
                                                 Map.entry("NOT", Collections.singletonList("BOOLEAN_TYPE")),
                                                 Map.entry("LESS", Collections.singletonList("INTEGER_TYPE")),
                                                 Map.entry("LESS_EQUAL", Collections.singletonList("INTEGER_TYPE")),
                                                 Map.entry("GREATER", Collections.singletonList("INTEGER_TYPE")),
                                                 Map.entry("GREATER_EQUAL", Collections.singletonList("INTEGER_TYPE")),
                                                 Map.entry("EQUAL", Arrays.asList("INTEGER_TYPE", "BOOLEAN_TYPE", "STRING_TYPE")),
                                                 Map.entry("NOT_EQUAL", Arrays.asList("INTEGER_TYPE", "BOOLEAN_TYPE", "STRING_TYPE")));
    }

    public static TypeTable fromAST(SyntaxTree tree) {
        System.out.println(" - Building TypeTable...");
        final Map<String, String> symbolTable = new HashMap<>();

        log("Creating TypeTable");
        initSymbolTable(tree.getRoot(), symbolTable);
        log("-".repeat(100));

        return new TypeTable(symbolTable);
    }

    private static void initSymbolTable(SyntaxTreeNode root, Map<String, String> table) {
        for (SyntaxTreeNode child : root.getChildren()) {
            initSymbolTable(child, table);
        }

        if ("declaration".equals(root.getName())) {
            final SyntaxTreeNode child = root.getChildren().get(0);

            log("Adding Entry " + child.getValue() + " -> " + root.getValue());
            final String oldEntry = table.put(child.getValue(), root.getValue());

            if (oldEntry != null) {
                System.out.println("Line " + root.getLine() + " Symbolerror: [" + child.getValue() + "] already defined");
                throw new SymbolAlreadyDefinedException("Das Symbol " + child.getValue() + " wurde bereits deklariert.");
            }
        }
    }

    // Getters

    public String getSymbolType(String sym) {
        return this.symbolTable.get(sym);
    }

    public String getMethodReturnType(String meth) {
        return this.methodReturnTable.get(meth);
    }

    public List<String> getMethodArgumentType(String meth) {
        return this.methodArgumentTable.get(meth);
    }

    public int getSymbolCount() {
        return this.symbolTable.size();
    }
}
