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
    private static final Collection<String> unary = Arrays.asList("ADD", "SUB", "NOT");

    private TypeChecker() {}

    // TODO: nodeTable?
    // Wirft exception bei typeerror, return nodeTable?
    public static Map<ASTNode, String> validate(AST tree) {
        final TypeTable table = TypeTable.fromAST(tree);
        final Map<ASTNode, String> nodeTable = new HashMap<>();

        System.out.println(" - Validating syntax-tree...");

        log("Typevalidation:");
        validate(tree.getRoot(), table, nodeTable);
        log("-".repeat(100));

        System.out.println("Typechecking successful.");
        return nodeTable;
    }

    private static void validate(ASTNode root, TypeTable table, Map<ASTNode, String> nodeTable) {
        for (ASTNode child : root.getChildren()) {
            validate(child, table, nodeTable);
        }

        if (lit.contains(root.getName())) {
            // NodeTable Eintrag für Literal hinzufügen

            final String literalType = getLiteralType(root.getName());

            nodeTable.put(root, literalType);
            return;
        } else if ("expr".equals(root.getName())) {
            // NodeTable Eintrag für Expression hinzufügen

            final String exprType = table.getMethodReturnType(root.getValue());

            nodeTable.put(root, exprType);
        } else if ("par_expr".equals(root.getName())) {
            // Nodetable Eintrag für Klammern

            final ASTNode centerChild = root.getChildren().get(1);

            nodeTable.put(root, nodeTable.get(centerChild));
        } else if ("IDENTIFIER".equals(root.getName())) {
            // Nodedtable Eintrag fuer Identifier

            final String identifierType = table.getSymbolType(root.getValue());

            nodeTable.put(root, identifierType);
        }

        if ("assignment".equals(root.getName())) {
            validateAssignment(root, table, nodeTable);
        } else if ("expr".equals(root.getName())) {
            validateExpression(root, table, nodeTable);
        }
    }

    private static void validateAssignment(ASTNode root, TypeTable table, Map<ASTNode, String> nodeTable) {
        final String identifier = root.getValue();
        final String identifierType = table.getSymbolType(identifier);
        final ASTNode literalNode = root.getChildren().get(0);
        final String literalType = nodeTable.get(literalNode);

        log("Validating Assignment: " + identifierType + ": " + identifier + " = " + literalType);

        if (!literalType.equals(identifierType)) {
            System.out.println("Line " + root.getLine() + " Typeerror: Can't assign [" + literalNode.getValue() + "] to [" + identifier + "]: " + identifierType);

            throw new AssignmentTypeMismatchException("Trying to assign " + literalType + " to a " + identifierType + " variable.");
        }
    }

    private static void validateExpression(ASTNode root, TypeTable table, Map<ASTNode, String> nodeTable) {
        final String op = root.getValue();

        log("Validating Expression: " + root.getValue());

        if (!root.hasChildren()) {
            // Keine Kinder

            System.out.println("Line " + root.getLine() + " Operatorerror: Can't use [" + op + "] without arguments");

            throw new OperatorUsageException("Versuche Operator " + op + " ohne Argumente aufzurufen.");
        } else if (root.getChildren().size() != 1 && "NOT".equals(op)) {
            // Unärer Operator mit  != 1 Child
            // SUB, ADD müssen nicht geprüft werden, da diese doppelt belegt sind mit ihrem binären Gegenstück

            System.out.println("Line " + root.getLine() + " Operatorerror: Can't use [" + op + "] with more than 1 argument");

            throw new OperatorUsageException("Versuche unären Operator " + op + " mit mehreren Argument aufzurufen.");
        } else if (root.getChildren().size() == 1 && !unary.contains(op)) {
            // Binärer Operator mit 1 Child

            System.out.println("Line " + root.getLine() + " Operatorerror: Can't use [" + op + "] with only 1 argument");

            throw new OperatorUsageException("Versuche binären Operator " + op + " mit einem Argument aufzurufen.");
        }

        final List<String> requiredType = table.getMethodArgumentType(op);
        for (ASTNode child : root.getChildren()) {
            // Jedes Child muss korrekten Typ zurückgeben

            final String childReturnType = nodeTable.get(child);

            if (childReturnType == null) {
                System.out.println("Variable " + child.getValue() + " wurde nicht deklariert.");

                throw new SymbolNotDefinedException("Zugriff auf nicht deklarierte Variable " + child.getValue());
            }

            if (!requiredType.contains(childReturnType)) {
                // Child returned Typ, welcher nicht im SymbolTable als Argumenttyp steht
                // Der NodeTable enthält auch Literale, diese müssen also nicht einzeln behandelt werden

                System.out.println("Line " + root.getLine() + " Typeerror: Can't use [" + op + "] with argument of type [" + nodeTable.get(child) + "]");

                throw new OperatorTypeMismatchException("Versuche Operator " + op + " mit Argument vom Typ " + nodeTable.get(child) + " aufzurufen.");
            }
        }

        if ("EQUAL".equals(op) || "NOT_EQUAL".equals(op)) {
            final ASTNode left = root.getChildren().get(0);
            final ASTNode right = root.getChildren().get(1);

            if (!nodeTable.get(left).equals(nodeTable.get(right))) {
                System.out.println("Line " + root.getLine() + " Typeerror: Can't use [" + op + "] with arguments of type [" + nodeTable.get(left) + "] and [" + nodeTable.get(right) + "]");

                throw new OperatorTypeMismatchException("Versuche Operator" + op + " mit Argumenten ungleichen Types zu verwenden.");
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
