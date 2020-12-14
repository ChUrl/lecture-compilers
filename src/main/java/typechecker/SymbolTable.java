package typechecker;

import parser.ast.AST;
import parser.ast.ASTNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.Logger.log;

public class SymbolTable {

    private final Map<String, String> symbolTable;
    private final Map<String, String> methodReturnTable;
    private final Map<String, List<String>> methodArgumentTable;

    public SymbolTable(Map<String, String> symbolTable) {
        this.symbolTable = symbolTable;

        // Enthält die Return-Types der Operatoren
        final Map<String, String> methodTable = new HashMap<>();

        methodTable.put("ADD", "INTEGER_TYPE");
        methodTable.put("SUB", "INTEGER_TYPE");
        methodTable.put("MUL", "INTEGER_TYPE");
        methodTable.put("DIV", "INTEGER_TYPE");
        methodTable.put("MOD", "INTEGER_TYPE");

        methodTable.put("NOT", "BOOLEAN_TYPE");
        methodTable.put("AND", "BOOLEAN_TYPE");
        methodTable.put("OR", "BOOLEAN_TYPE");

        methodTable.put("LESS", "BOOLEAN_TYPE");
        methodTable.put("LESS_EQUAL", "BOOLEAN_TYPE");
        methodTable.put("GREATER", "BOOLEAN_TYPE");
        methodTable.put("GREATER_EQUAL", "BOOLEAN_TYPE");
        methodTable.put("EQUAL", "BOOLEAN_TYPE");
        methodTable.put("NOT_EQUAL", "BOOLEAN_TYPE");

        this.methodReturnTable = methodTable;

        final Map<String, List<String>> argumentTable = new HashMap<>();

        argumentTable.put("ADD", Arrays.asList("INTEGER_TYPE"));
        argumentTable.put("SUB", Arrays.asList("INTEGER_TYPE"));
        argumentTable.put("MUL", Arrays.asList("INTEGER_TYPE"));
        argumentTable.put("DIV", Arrays.asList("INTEGER_TYPE"));
        argumentTable.put("MOD", Arrays.asList("INTEGER_TYPE"));

        argumentTable.put("AND", Arrays.asList("BOOLEAN_TYPE"));
        argumentTable.put("OR", Arrays.asList("BOOLEAN_TYPE"));
        argumentTable.put("NOT", Arrays.asList("BOOLEAN_TYPE"));

        argumentTable.put("LESS", Arrays.asList("INTEGER_TYPE"));
        argumentTable.put("LESS_EQUAL", Arrays.asList("INTEGER_TYPE"));
        argumentTable.put("GREATER", Arrays.asList("INTEGER_TYPE"));
        argumentTable.put("GREATER_EQUAL", Arrays.asList("INTEGER_TYPE"));
        argumentTable.put("EQUAL", Arrays.asList("INTEGER_TYPE", "BOOLEAN_TYPE", "STRING_TYPE"));
        argumentTable.put("NOT_EQUAL", Arrays.asList("INTEGER_TYPE", "BOOLEAN_TYPE", "STRING_TYPE"));

        this.methodArgumentTable = argumentTable;
    }

    public static SymbolTable fromAST(AST tree) {
        final Map<String, String> tableOut = new HashMap<>();

        log("Creating SymbolTable");
        scanTree(tree.getRoot(), tableOut);
        log("-".repeat(100));

        return new SymbolTable(tableOut);
    }

    private static void scanTree(ASTNode root, Map<String, String> table) {
        for (ASTNode child : root.getChildren()) {
            scanTree(child, table);
        }

        if ("DECLARATION".equals(root.getName())) {
            ASTNode left = root.getChildren().get(0);
            ASTNode right = root.getChildren().get(1);

            log("Adding Entry " + right.getValue() + " -> " + left.getName());
            String oldEntry = table.put(right.getValue(), left.getName());

            if (oldEntry != null) {
                System.out.println("Line " + root.getLine() + " Symbolerror: [" + right.getValue() + "] already defined");
                throw new SymbolAlreadyDefinedException("Das Symbol " + right.getValue() + " wurde bereits deklariert.");
            }
        }
    }

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
