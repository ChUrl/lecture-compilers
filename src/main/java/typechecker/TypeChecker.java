package typechecker;

import parser.ast.AST;
import parser.ast.ASTNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.Logger.log;

public final class TypeChecker {

    private static final Collection<String> lit = Arrays.asList("INTEGER_LIT", "STRING_LIT", "BOOLEAN_LIT");

    private TypeChecker() {}

    // Wirft exception bei typeerror, returned nix
    public static void validate(AST tree) {
        SymbolTable table = SymbolTable.fromAST(tree);
        Map<ASTNode, String> nodeTable = new HashMap<>();

        log("Typevalidation:");
        validate(tree.getRoot(), table, nodeTable);
        log("-".repeat(100));
    }

    private static void validate(ASTNode root, SymbolTable table, Map<ASTNode, String> nodeTable) {
        for (ASTNode child : root.getChildren()) {
            validate(child, table, nodeTable);
        }

        if (lit.contains(root.getName())) {
            // NodeTable Eintrag für Literal hinzufügen

            String literalType = getLiteralType(root.getName());

            nodeTable.put(root, literalType);
            return;
        } else if ("EXPR".equals(root.getName())) {
            // NodeTable Eintrag für Expression hinzufügen

            String exprType = table.getMethodReturnType(root.getValue());

            nodeTable.put(root, exprType);
        } else if ("PAR_EXPR".equals(root.getName())) {
            // Nodetable Eintrag für Klammern

            ASTNode centerChild = root.getChildren().get(1);

            nodeTable.put(root, nodeTable.get(centerChild));
        }

        if ("ASSIGNMENT".equals(root.getName())) {
            validateAssignment(root, table, nodeTable);
        } else if ("EXPR".equals(root.getName())) {
            validateExpression(root, table, nodeTable);
        }
    }

    private static void validateAssignment(ASTNode root, SymbolTable table, Map<ASTNode, String> nodeTable) {
        String identifier = root.getValue();
        String identifierType = table.getSymbolType(identifier);
        ASTNode literalNode = root.getChildren().get(0);
        String literalType = nodeTable.get(literalNode);

        log("Validating Assignment: " + identifierType + ": " + identifier + " = " + literalNode.toString().trim());

        if (!literalType.equals(identifierType)) {
            throw new AssignmentTypeMismatchException("Trying to assign " + literalType + " to a " + identifierType + " variable.");
        }
    }

    private static void validateExpression(ASTNode root, SymbolTable table, Map<ASTNode, String> nodeTable) {
        Collection<String> unary = Arrays.asList("ADD", "SUB", "NOT");
        String op = root.getValue();

        log("Validating Expression: " + root.getValue());

        if (root.getChildren().size() != 1 && "NOT".equals(op)) {
            // Unärer Operator mit  != 1 Child
            // SUB, ADD müssen nicht geprüft werden, da diese doppelt belegt sind mit ihrem binären Gegenstück

            throw new OperatorUsageException("Versuche unären Operator " + op + " mit mehreren Argument aufzurufen.");
        } else if (root.getChildren().size() == 1 && !unary.contains(op)) {
            // Binärer Operator mit 1 Child

            throw new OperatorUsageException("Versuche binären Operator " + op + " mit einem Argument aufzurufen.");
        }

        List<String> requiredType = table.getMethodArgumentType(op);
        for (ASTNode child : root.getChildren()) {
            // Jedes Child muss korrekten Typ zurückgeben

            String childReturnType = nodeTable.get(child);

            if (!requiredType.contains(childReturnType)) {
                // Child returned Typ, welcher nicht im SymbolTable als Argumenttyp steht
                // Der NodeTable enthält auch Literale, diese müssen also nicht einzeln behandelt werden

                throw new OperatorTypeMismatchException("Versuche Operator " + op + " mit Argument vom Typ " + nodeTable.get(child) + " aufzurufen.");
            }

        }
    }

    private static String getLiteralType(String literal) {
        return switch (literal) {
            case "BOOLEAN_LIT" -> "BOOLEAN_TYPE";
            case "INTEGER_LIT" -> "INTEGER_TYPE";
            case "STRING_LIT" -> "STRING_TYPE";
            default -> null;
        };
    }
}
