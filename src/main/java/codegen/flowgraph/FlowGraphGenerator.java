package codegen.flowgraph;

import codegen.CodeGenerationException;
import codegen.analysis.StackSizeAnalyzer;
import parser.ast.AST;
import parser.ast.ASTNode;
import util.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;
import static util.Logger.log;

/**
 * Erzeugt den SourceCode in FlussGraph-Darstellung.
 */
public final class FlowGraphGenerator {

    private static final Map<String, Method> methodMap;

    static {
        // Init the method mappings: ASTNode.getName() -> FlowGraphGenerator.method
        Map<String, Method> map;
        try {
            final Class<?> gen = FlowGraphGenerator.class;
            map = Map.ofEntries(
                    entry("cond", gen.getDeclaredMethod("condNode", ASTNode.class)),
                    entry("loop", gen.getDeclaredMethod("loopNode", ASTNode.class)),
                    entry("assignment", gen.getDeclaredMethod("assignNode", ASTNode.class)),
                    entry("expr", gen.getDeclaredMethod("exprNode", ASTNode.class)),
                    // Leafs
                    entry("INTEGER_LIT", gen.getDeclaredMethod("intStringLiteralNode", ASTNode.class)),
                    entry("BOOLEAN_LIT", gen.getDeclaredMethod("boolLiteralNode", ASTNode.class)),
                    entry("STRING_LIT", gen.getDeclaredMethod("intStringLiteralNode", ASTNode.class)),
                    entry("IDENTIFIER", gen.getDeclaredMethod("identifierNode", ASTNode.class)),
                    entry("print", gen.getDeclaredMethod("printlnNode", ASTNode.class))
            );
        } catch (NoSuchMethodException e) {
            map = null;
            e.printStackTrace();
        }
        methodMap = map;
    }

    private final AST tree;
    private final Map<String, Integer> varMap;
    private final Map<ASTNode, String> nodeTypeMap;
    private final FlowGraph graph;
    private int labelCounter;

    private FlowGraphGenerator(Map<String, Integer> varMap, AST tree, Map<ASTNode, String> nodeTypeMap, FlowGraph graph) {
        this.varMap = varMap;
        this.tree = tree;
        this.nodeTypeMap = nodeTypeMap;
        this.graph = graph;
    }

    public static FlowGraphGenerator fromAST(AST tree, Map<ASTNode, String> nodeTypeMap, String source) {
        if (!tree.getRoot().hasChildren()) {
            throw new CodeGenerationException("Empty File can't be compiled");
        }

        final Map<String, Integer> varMap = initVarMap(tree);

        final String bytecodeVersion = "49.0";
        final String clazz = tree.getRoot().getChildren().get(1).getValue();
        final int stackSize = StackSizeAnalyzer.runStackModel(tree);
        final int localCount = varMap.size() + 1;
        final FlowGraph graph = new FlowGraph(bytecodeVersion, source, clazz, stackSize, localCount);

        return new FlowGraphGenerator(varMap, tree, nodeTypeMap, graph);
    }

    private static Map<String, Integer> initVarMap(AST tree) {
        final Map<String, Integer> varMap = new HashMap<>();

        // Assign variables to map: Symbol -> jasminLocalVarNr.
        int varCount = 0;
        final Deque<ASTNode> stack = new ArrayDeque<>();
        stack.push(tree.getRoot());
        while (!stack.isEmpty()) {
            final ASTNode current = stack.pop();

            if ("declaration".equals(current.getName())) {
                varCount++;
                varMap.put(current.getChildren().get(0).getValue(), varCount);
                log("New local " + current.getValue() + " variable "
                    + current.getChildren().get(0).getValue()
                    + " assigned to slot " + varCount + ".");
            }

            current.getChildren().forEach(stack::push);
        }

        return Collections.unmodifiableMap(varMap);
    }

    /**
     * Erzeugt den Flussgraphen für den gespeicherten AST.
     * Der Flussgraph ist dabei die Graphenform des generierten SourceCodes:
     * Die Instruktionen sind unterteilt in BasicBlocks, welche über Kanten verbunden sind.
     */
    public FlowGraph generateGraph() {
        System.out.println(" - Generating Source Graph...");

        // Skip the first 2 identifiers: ClassName, MainArgs
        this.generateNode(this.tree.getRoot().getChildren().get(3).getChildren().get(11));
        this.graph.purgeEmptyBlocks();
        Logger.call(this.graph::printToImage);

        log("\n\nSourceGraph print:\n" + "-".repeat(100) + "\n" + this.graph.print() + "-".repeat(100));
        System.out.println("Graph-generation successful.");

        return this.graph;
    }

    /**
     * Erzeugt den FlussGraphen für die angegebene Wurzel.
     * Der Wurzelname wird über die methodMap einer Methode zugewiesen.
     * Diese wird aufgerufen und erzeugt den entsprechenden Teilbaum.
     */
    private void generateNode(ASTNode root) {
        if (methodMap.containsKey(root.getName())) {
            try {
                methodMap.get(root.getName()).invoke(this, root);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            root.getChildren().forEach(this::generateNode);
        }
    }

    // ifeq - if value is 0
    // ifne - if value is not 0

    /**
     * Erzeugt den Teilbaum für einen If-Knoten.
     */
    private void condNode(ASTNode root) {
        final int currentLabel = this.labelCounter;
        this.labelCounter++;

        // Condition If ( ... ) {
        this.generateNode(root.getChildren().get(0));

        // Jump if condition false
        this.graph.addJump("ifeq", "IFfalse" + currentLabel);

        // IFtrue branch (gets executed without jump)
        this.generateNode(root.getChildren().get(1));
        this.graph.addJump("goto", "IFend" + currentLabel); // Skip IFfalse branch

        // IFfalse branch (gets executed after jump)
        this.graph.addLabel("IFfalse" + currentLabel);
        if (root.getChildren().size() == 3) {
            // Else exists

            this.generateNode(root.getChildren().get(2));
        }

        // IFend branch
        this.graph.addLabel("IFend" + currentLabel);
    }

    /**
     * Erzeugt den Teilbaum für einen While-Knoten.
     */
    private void loopNode(ASTNode root) {
        final int currentLabel = this.labelCounter;
        this.labelCounter++;

        // LOOPstart label for loop repetition
        this.graph.addLabel("LOOPstart" + currentLabel);

        // Condition while ( ... ) {
        this.generateNode(root.getChildren().get(0).getChildren().get(1));

        // Jump out of loop if condition is false
        this.graph.addJump("ifeq", "LOOPend" + currentLabel);

        // Loop body (gets executed without jump)
        this.generateNode(root.getChildren().get(1));
        this.graph.addJump("goto", "LOOPstart" + currentLabel); // Repeat loop

        // Loop end
        this.graph.addLabel("LOOPend" + currentLabel);
    }

    /**
     * Erzeugt den Teilbaum für Assignment-Knoten.
     * Die JVM-Stacksize wird dabei um 1 verringert, da istore/astore 1 Argument konsumieren.
     */
    private void assignNode(ASTNode root) { //! Stack - 1
        this.generateNode(root.getChildren().get(0));

        final String type = this.nodeTypeMap.get(root.getChildren().get(0));
        final String inst = switch (type) {
            case "INTEGER_TYPE", "BOOLEAN_TYPE" -> "istore";
            case "STRING_TYPE" -> "astore";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        log("assign(): " + root.getName() + ": " + root.getValue() + " => " + inst);

        this.graph.addInstruction(inst, this.varMap.get(root.getValue()).toString());
    }

    /**
     * Wählt die entsprechende Methode für mathematische oder logische Ausdrücke.
     */
    private void exprNode(ASTNode root) {
        if ("INTEGER_TYPE".equals(this.nodeTypeMap.get(root))) {
            this.intExpr(root);
        } else if ("BOOLEAN_TYPE".equals(this.nodeTypeMap.get(root))) {
            this.boolExpr(root);
        }
    }

    /**
     * Erzeugt den Teilbaum für mathematische Ausdrücke.
     * Bei unären Operatoren bleibt die Stackgröße konstant (1 konsumiert, 1 Ergebnis),
     * bei binären Operatoren sinkt die Stackgröße um 1 (2 konsumiert, 1 Ergebnis).
     */
    private void intExpr(ASTNode root) {
        String inst = "";

        if (root.getChildren().size() == 1) { //! Stack + 0
            // Unary operator

            this.generateNode(root.getChildren().get(0));

            inst = switch (root.getValue()) {
                case "ADD" -> "";
                case "SUB" -> "ineg";
                default -> throw new IllegalStateException("Unexpected value: " + root.getValue());
            };
        } else if (root.getChildren().size() == 2) { //! Stack - 1
            // Binary operator

            this.generateNode(root.getChildren().get(0));
            this.generateNode(root.getChildren().get(1));

            inst = switch (root.getValue()) {
                case "ADD" -> "iadd"; // Integer
                case "SUB" -> "isub";
                case "MUL" -> "imul";
                case "DIV" -> "idiv";
                case "MOD" -> "irem"; // Remainder operator
                default -> throw new IllegalStateException("Unexpected value: " + root.getValue());
            };
        }

        log("intExpr(): " + root.getName() + ": " + root.getValue() + " => " + inst);

        this.graph.addInstruction(inst);
    }

    /**
     * Erzeugt den Teilbaum für logische Ausdrücke.
     * Bei unären Operatoren wächst der Stack temporär um 1 (NOT pusht eine 1 für xor),
     * bei binären Operatoren sinkt die Stackgröße um 1 (2 konsumiert, 1 Ergebnis).
     */
    private void boolExpr(ASTNode node) {
        if (node.getChildren().size() == 1) { //! Stack + 1
            // Unary operator

            if (!"NOT".equals(node.getValue())) {
                // Possibility doesn't exist, would be frontend-error

                throw new IllegalStateException("Unexpected value: " + node.getValue());
            }

            this.generateNode(node.getChildren().get(0));

            // 0 xor 1 = 1, 1 xor 1 = 0 => not
            this.graph.addInstruction("ldc", "1");
            this.graph.addInstruction("ixor");

        } else if (node.getChildren().size() == 2) { //! Stack - 1
            // Binary operator

            final int currentLabel = this.labelCounter;
            this.labelCounter++;

            this.generateNode(node.getChildren().get(0));
            this.generateNode(node.getChildren().get(1));

            final String type = this.nodeTypeMap.get(node.getChildren().get(0));
            final String cmpeq = switch (type) {
                case "INTEGER_TYPE", "BOOLEAN_TYPE" -> "if_icmpeq";
                case "STRING_TYPE" -> "if_accmpeq";
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };
            final String cmpne = switch (type) {
                case "INTEGER_TYPE", "BOOLEAN_TYPE" -> "if_icmpne";
                case "STRING_TYPE" -> "if_accmpne";
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };

            // The comparison operations need to jump
            switch (node.getValue()) {
                case "AND" -> this.graph.addInstruction("iand"); // Boolean
                case "OR" -> this.graph.addInstruction("ior");
                case "EQUAL" -> this.genComparisonInst(cmpeq, "EQ", currentLabel);
                case "NOT_EQUAL" -> this.genComparisonInst(cmpne, "NE", currentLabel);
                case "LESS" -> this.genComparisonInst("if_icmplt", "LT", currentLabel);
                case "LESS_EQUAL" -> this.genComparisonInst("if_icmple", "LE", currentLabel);
                case "GREATER" -> this.genComparisonInst("if_icmpgt", "GT", currentLabel);
                case "GREATER_EQUAL" -> this.genComparisonInst("if_icmpge", "GE", currentLabel);
                default -> throw new IllegalStateException("Unexpected value: " + node.getValue());
            }
        }
    }

    /**
     * Erzeugt die Instruktionen für eine Vergleichsoperation.
     *
     * @param cmpInst      Die Vergleichsanweisung
     * @param labelPre     Das Labelpräfix, abhängig von der Art des Vergleichs
     * @param currentLabel Der aktuelle Labelcounter
     */
    private void genComparisonInst(String cmpInst, String labelPre, int currentLabel) {
        this.graph.addJump(cmpInst, labelPre + "true" + currentLabel); // If not equal jump to NEtrue
        this.graph.addInstruction("ldc", "0"); // If false load 0
        this.graph.addJump("goto", labelPre + "end" + currentLabel); // If false skip to true
        this.graph.addLabel(labelPre + "true" + currentLabel);
        this.graph.addInstruction("ldc", "1"); // If true load 1
        this.graph.addLabel(labelPre + "end" + currentLabel);
    }

    // Leafs

    private void intStringLiteralNode(ASTNode node) { //! Stack + 1
        log("intStringLiteral(): " + node.getName() + ": " + node.getValue() + " => ldc");

        // bipush only pushes 1 byte as int
        this.graph.addInstruction("ldc", node.getValue());
    }

    private void boolLiteralNode(ASTNode node) { //! Stack + 1
        log("booleanLiteral(): " + node.getName() + ": " + node.getValue() + " => ldc");

        final String val = "true".equals(node.getValue()) ? "1" : "0";

        this.graph.addInstruction("ldc", val);
    }

    private void identifierNode(ASTNode node) { //! Stack + 1
        final String type = this.nodeTypeMap.get(node);
        final String inst = switch (type) {
            case "INTEGER_TYPE", "BOOLEAN_TYPE" -> "iload";
            case "STRING_TYPE" -> "aload";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        log("identifier(): " + node.getName() + ": " + node.getValue() + " => " + inst);

        this.graph.addInstruction(inst, this.varMap.get(node.getValue()).toString());
    }

    private void printlnNode(ASTNode node) { //! Stack + 1
        this.graph.addInstruction("getstatic", "java/lang/System/out", "Ljava/io/PrintStream;");

        final ASTNode expr = node.getChildren().get(1).getChildren().get(1);
        final String type = switch (this.nodeTypeMap.get(expr)) {
            case "BOOLEAN_TYPE" -> "Z";
            case "INTEGER_TYPE" -> "I";
            case "STRING_TYPE" -> "Ljava/lang/String;";
            default -> throw new IllegalStateException("Unexpected value: " + this.nodeTypeMap.get(expr));
        };

        this.generateNode(expr);

        log("println(): " + expr.getName() + ": " + expr.getValue() + " => " + type);

        this.graph.addInstruction("invokevirtual", "java/io/PrintStream/println(" + type + ")V");
    }

    // Getters, Setters

    public Map<String, Integer> getVarMap() {
        return this.varMap;
    }
}
