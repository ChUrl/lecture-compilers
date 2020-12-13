package typechecker;

import parser.ast.AST;
import parser.ast.ASTNode;

import java.util.HashMap;
import java.util.Map;

import static util.Logger.log;

public class SymbolTable {

    private final Map<String, String> symbolTable;
    private final Map<String, String> methodTable;

    public SymbolTable(Map<String, String> symbolTable) {
        this.symbolTable = symbolTable;

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

        this.methodTable = methodTable;
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
                System.out.println("Typfehler - Symbol bereits definiert: " + right.getValue());
                throw new SymbolAlreadyDefinedException("Das Symbol " + right.getValue() + " wurde bereits deklariert.");
            }
        }
    }

    public String getSymbolType(String sym) {
        return this.symbolTable.get(sym);
    }

    public String getMethodType(String meth) {
        return this.methodTable.get(meth);
    }

    public int getSymbolCount() {
        return this.symbolTable.size();
    }
}
