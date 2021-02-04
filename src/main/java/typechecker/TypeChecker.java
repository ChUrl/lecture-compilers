package typechecker;

import parser.ast.SyntaxTree;
import parser.ast.SyntaxTreeNode;
import util.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypeChecker {

    private static final Collection<String> lit = Arrays.asList("INTEGER_LIT", "STRING_LIT", "BOOLEAN_LIT");
    private static final Collection<String> unary = Arrays.asList("ADD", "SUB", "NOT");

    private TypeChecker() {}

    // TODO: merge nodeTable into typetable?
    // Wirft exception bei typeerror
    public static Map<SyntaxTreeNode, String> validate(SyntaxTree tree) {
        final TypeTable table = TypeTable.fromAST(tree);
        final Map<SyntaxTreeNode, String> nodeTable = new HashMap<>();

        Logger.logDebug("Beginning typevalidation of abstract-syntax-tree", TypeChecker.class);

        validate(tree.getRoot(), table, nodeTable);

        Logger.logDebug("Successfully typevalidated the abstract-syntax-tree", TypeChecker.class);

        return nodeTable;
    }

    private static void validate(SyntaxTreeNode root, TypeTable table, Map<SyntaxTreeNode, String> nodeTable) {
        for (SyntaxTreeNode child : root.getChildren()) {
            validate(child, table, nodeTable);
        }

        if (lit.contains(root.getName())) {
            // NodeTable Eintrag für Literal hinzufügen

            final String literalType = getLiteralType(root.getName());

            Logger.logInfo("Type " + literalType + " for Node:\n" + root, TypeChecker.class);

            nodeTable.put(root, literalType);
            return;
        } else if ("expr".equals(root.getName())) {
            // NodeTable Eintrag für Expression hinzufügen

            final String exprType = table.getMethodReturnType(root.getValue());

            Logger.logInfo("Type " + exprType + " for Node:\n" + root, TypeChecker.class);

            nodeTable.put(root, exprType);
        } else if ("par_expr".equals(root.getName())) {
            // Nodetable Eintrag für Klammern

            final SyntaxTreeNode centerChild = root.getChildren().get(1);

            nodeTable.put(root, nodeTable.get(centerChild));
        } else if ("IDENTIFIER".equals(root.getName())) {
            // Nodedtable Eintrag fuer Identifier

            final String identifierType = table.getSymbolType(root.getValue());

            Logger.logInfo("Type " + identifierType + " for Node:\n" + root, TypeChecker.class);

            nodeTable.put(root, identifierType);
        }

        if ("assignment".equals(root.getName())) {
            validateAssignment(root, table, nodeTable);
        } else if ("expr".equals(root.getName())) {
            validateExpression(root, table, nodeTable);
        }
    }

    private static void validateAssignment(SyntaxTreeNode root, TypeTable table, Map<SyntaxTreeNode, String> nodeTable) {
        final String identifier = root.getValue();
        final String identifierType = table.getSymbolType(identifier);
        final SyntaxTreeNode literalNode = root.getChildren().get(0);
        final String literalType = nodeTable.get(literalNode);

        Logger.logInfo("Validating Assignment: " + identifierType + ": " + identifier + " = " + literalType, TypeChecker.class);

        if (!literalType.equals(identifierType)) {
            Logger.logError("Line " + root.getLine() + " Typeerror: Can't assign [" + literalNode.getValue()
                            + "] to [" + identifier + "]: " + identifierType, TypeChecker.class);

            throw new AssignmentTypeMismatchException("Trying to assign " + literalType + " to a " + identifierType + " variable.");
        }
    }

    private static void validateExpression(SyntaxTreeNode root, TypeTable table, Map<SyntaxTreeNode, String> nodeTable) {
        final String op = root.getValue();

        Logger.logInfo("Validating Expression: " + root.getValue(), TypeChecker.class);

        if (root.isEmpty()) {
            // Keine Kinder

            Logger.logError("Line " + root.getLine() + " Operatorerror: Can't use [" + op + "] without arguments", TypeChecker.class);

            throw new OperatorUsageException("Versuche Operator " + op + " ohne Argumente aufzurufen.");
        } else if (root.getChildren().size() != 1 && "NOT".equals(op)) {
            // Unärer Operator mit  != 1 Child
            // SUB, ADD müssen nicht geprüft werden, da diese doppelt belegt sind mit ihrem binären Gegenstück

            Logger.logError("Line " + root.getLine() + " Operatorerror: Can't use [" + op + "] with more than 1 argument", TypeChecker.class);

            throw new OperatorUsageException("Versuche unären Operator " + op + " mit mehreren Argument aufzurufen.");
        } else if (root.getChildren().size() == 1 && !unary.contains(op)) {
            // Binärer Operator mit 1 Child

            Logger.logError("Line " + root.getLine() + " Operatorerror: Can't use [" + op + "] with only 1 argument", TypeChecker.class);

            throw new OperatorUsageException("Versuche binären Operator " + op + " mit einem Argument aufzurufen.");
        }

        final List<String> requiredType = table.getMethodArgumentType(op);
        for (SyntaxTreeNode child : root.getChildren()) {
            // Jedes Child muss korrekten Typ zurückgeben

            final String childReturnType = nodeTable.get(child);

            if (childReturnType == null) {
                Logger.logError("Variable " + child.getValue() + " wurde nicht deklariert.", TypeChecker.class);

                throw new SymbolNotDefinedException("Zugriff auf nicht deklarierte Variable " + child.getValue());
            }

            if (!requiredType.contains(childReturnType)) {
                // Child returned Typ, welcher nicht im SymbolTable als Argumenttyp steht
                // Der NodeTable enthält auch Literale, diese müssen also nicht einzeln behandelt werden

                Logger.logError("Line " + root.getLine() + " Typeerror: Can't use [" + op
                                + "] with argument of type [" + nodeTable.get(child) + "]", TypeChecker.class);

                throw new OperatorTypeMismatchException("Versuche Operator " + op + " mit Argument vom Typ " + nodeTable.get(child) + " aufzurufen.");
            }
        }

        if ("EQUAL".equals(op) || "NOT_EQUAL".equals(op)) {
            final SyntaxTreeNode left = root.getChildren().get(0);
            final SyntaxTreeNode right = root.getChildren().get(1);

            if (!nodeTable.get(left).equals(nodeTable.get(right))) {
                Logger.logError("Line " + root.getLine() + " Typeerror: Can't use [" + op
                                + "] with arguments of type [" + nodeTable.get(left) + "] and [" + nodeTable.get(right)
                                + "]", TypeChecker.class);

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
