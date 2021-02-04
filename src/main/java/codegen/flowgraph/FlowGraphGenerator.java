package codegen.flowgraph;

import codegen.CodeGenerationException;
import codegen.analysis.StackSizeAnalyzer;
import parser.ast.SyntaxTree;
import parser.ast.SyntaxTreeNode;
import typechecker.TypeChecker;
import util.Logger;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Erzeugt den SourceCode in FlussGraph-Darstellung.
 */
public final class FlowGraphGenerator {

    private final SyntaxTree tree;

    /**
     * Enthält den Rückgabetypen von jedem Expression-Node.
     * Wird erstellt im {@link TypeChecker}.
     */
    private final Map<SyntaxTreeNode, String> nodeTypeMap;

    /**
     * Enthält die Mappings vom Symbol/Variablennamen auf die Position in der JVM-Locals-Tabelle.
     */
    private final Map<String, Integer> varMap;

    private final FlowGraph graph;

    private int labelCounter;

    private FlowGraphGenerator(Map<String, Integer> varMap, SyntaxTree tree, Map<SyntaxTreeNode, String> nodeTypeMap, FlowGraph graph) {
        this.varMap = varMap;
        this.tree = tree;
        this.nodeTypeMap = nodeTypeMap;
        this.graph = graph;
    }

    /**
     * @param source Das Source-File, welches compiliert wird (Optionaler Jasmin-Parameter)
     */
    public static FlowGraphGenerator fromAST(SyntaxTree tree, Map<SyntaxTreeNode, String> nodeTypeMap, String source) {
        if (tree.isEmpty()) {
            throw new CodeGenerationException("Empty File can't be compiled");
        }

        final Map<String, Integer> varMap = initVarMap(tree);
        final FlowGraph graph = initFlowGraph(tree, varMap, source);

        return new FlowGraphGenerator(varMap, tree, nodeTypeMap, graph);
    }

    private static Map<String, Integer> initVarMap(SyntaxTree tree) {
        Logger.logDebug("Initializing variable-map", FlowGraphGenerator.class);

        final Map<String, Integer> varMap = new HashMap<>();

        final Deque<SyntaxTreeNode> stack = new ArrayDeque<>();
        stack.push(tree.getRoot());

        int currentVarNumber = 0;

        // Assign variables to map: Symbol -> jasminLocalVarNr.
        while (!stack.isEmpty()) {
            final SyntaxTreeNode current = stack.pop();

            if ("declaration".equals(current.getName())) {
                // New variables only come from declarations

                currentVarNumber++;
                varMap.put(current.getChildren().get(0).getValue(), currentVarNumber);
                Logger.logInfo("New local " + current.getValue() + " variable "
                               + current.getChildren().get(0).getValue()
                               + " assigned to slot " + currentVarNumber + ".", FlowGraphGenerator.class);
            }

            current.getChildren().forEach(stack::push);
        }

        Logger.logDebug("Successfully initialized variable-map", FlowGraphGenerator.class);

        return Collections.unmodifiableMap(varMap);
    }

    private static FlowGraph initFlowGraph(SyntaxTree tree, Map<String, Integer> varMap, String source) {
        final String bytecodeVersion = "49.0";
        final String clazz = tree.getRoot().getChildren().get(1).getValue();
        final int stackSize = StackSizeAnalyzer.runStackModel(tree);
        final int localCount = varMap.size() + 1;

        return new FlowGraph(bytecodeVersion, source, clazz, stackSize, localCount);
    }

    /**
     * Erzeugt den Flussgraphen für den gespeicherten AST.
     * Der Flussgraph ist dabei die Graphenform des generierten SourceCodes:
     * Die Instruktionen sind unterteilt in BasicBlocks, welche über Kanten verbunden sind.
     */
    public FlowGraph generateGraph() {
        Logger.logDebug("Beginning generation of source-graph", FlowGraphGenerator.class);

        // Skip the first 2 identifiers: ClassName, MainArgs
        this.generateNode(this.tree.getRoot().getChildren().get(3).getChildren().get(11));
        this.graph.purgeEmptyBlocks();

        Logger.logDebug("Source-graph generation complete", FlowGraphGenerator.class);
        Logger.logInfo("\n\nSourceGraph print:\n" + "-".repeat(100) + "\n" + this.graph + "-".repeat(100), FlowGraphGenerator.class);

        return this.graph;
    }

    /**
     * Erzeugt den FlussGraphen für die angegebene Wurzel.
     * Der Wurzelname wird einer Methode zugewiesen.
     * Diese wird aufgerufen und erzeugt den entsprechenden Teilbaum.
     */
    private void generateNode(SyntaxTreeNode root) {
        switch (root.getName()) {
            case "cond" -> this.condNode(root);
            case "loop" -> this.loopNode(root);
            case "assignment" -> this.assignNode(root);
            case "expr" -> this.exprNode(root);
            case "INTEGER_LIT", "STRING_LIT" -> this.intStringLiteralNode(root);
            case "BOOLEAN_LIT" -> this.boolLiteralNode(root);
            case "IDENTIFIER" -> this.identifierNode(root);
            case "print" -> this.printlnNode(root);
            default -> root.getChildren().forEach(this::generateNode);
        }
    }

    /**
     * Erzeugt den Teilbaum für einen If-Knoten.
     */
    private void condNode(SyntaxTreeNode root) {
        Logger.logInfo("Generating Conditional Node", FlowGraphGenerator.class);

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
    private void loopNode(SyntaxTreeNode root) {
        Logger.logInfo("Generating Loop Node", FlowGraphGenerator.class);

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
    private void assignNode(SyntaxTreeNode root) { //! Stack - 1
        Logger.logInfo("Generating Assignment Node", FlowGraphGenerator.class);

        this.generateNode(root.getChildren().get(0));

        final String type = this.nodeTypeMap.get(root.getChildren().get(0));
        final String inst = switch (type) {
            case "INTEGER_TYPE", "BOOLEAN_TYPE" -> "istore";
            case "STRING_TYPE" -> "astore";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        Logger.logInfo("assign(): " + root.getName() + ": " + root.getValue() + " => " + inst, FlowGraphGenerator.class);

        this.graph.addInstruction(inst, this.varMap.get(root.getValue()).toString());
    }

    /**
     * Wählt die entsprechende Methode für mathematische oder logische Ausdrücke.
     */
    private void exprNode(SyntaxTreeNode root) {
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
    private void intExpr(SyntaxTreeNode root) {
        Logger.logInfo("Generating Integer Expression Node", FlowGraphGenerator.class);

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

        Logger.logInfo("intExpr(): " + root.getName() + ": " + root.getValue() + " => " + inst, FlowGraphGenerator.class);

        this.graph.addInstruction(inst);
    }

    /**
     * Erzeugt den Teilbaum für logische Ausdrücke.
     * Bei unären Operatoren wächst der Stack temporär um 1 (NOT pusht eine 1 für xor),
     * bei binären Operatoren sinkt die Stackgröße um 1 (2 konsumiert, 1 Ergebnis).
     */
    private void boolExpr(SyntaxTreeNode node) {
        Logger.logInfo("Generating Boolean Expression", FlowGraphGenerator.class);

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

    private void intStringLiteralNode(SyntaxTreeNode node) { //! Stack + 1
        Logger.logInfo("intStringLiteral(): " + node.getName() + ": " + node.getValue() + " => ldc", FlowGraphGenerator.class);

        // bipush only pushes 1 byte as int
        this.graph.addInstruction("ldc", node.getValue());
    }

    private void boolLiteralNode(SyntaxTreeNode node) { //! Stack + 1
        Logger.logInfo("booleanLiteral(): " + node.getName() + ": " + node.getValue() + " => ldc", FlowGraphGenerator.class);

        final String val = "true".equals(node.getValue()) ? "1" : "0";

        this.graph.addInstruction("ldc", val);
    }

    private void identifierNode(SyntaxTreeNode node) { //! Stack + 1
        final String type = this.nodeTypeMap.get(node);
        final String inst = switch (type) {
            case "INTEGER_TYPE", "BOOLEAN_TYPE" -> "iload";
            case "STRING_TYPE" -> "aload";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        Logger.logInfo("identifier(): " + node.getName() + ": " + node.getValue() + " => " + inst, FlowGraphGenerator.class);

        this.graph.addInstruction(inst, this.varMap.get(node.getValue()).toString());
    }

    private void printlnNode(SyntaxTreeNode node) { //! Stack + 1
        this.graph.addInstruction("getstatic", "java/lang/System/out", "Ljava/io/PrintStream;");

        final SyntaxTreeNode expr = node.getChildren().get(1).getChildren().get(1);
        final String type = switch (this.nodeTypeMap.get(expr)) {
            case "BOOLEAN_TYPE" -> "Z";
            case "INTEGER_TYPE" -> "I";
            case "STRING_TYPE" -> "Ljava/lang/String;";
            default -> throw new IllegalStateException("Unexpected value: " + this.nodeTypeMap.get(expr));
        };

        this.generateNode(expr);

        Logger.logInfo("println(): " + expr.getName() + ": " + expr.getValue() + " => " + type, FlowGraphGenerator.class);

        this.graph.addInstruction("invokevirtual", "java/io/PrintStream/println(" + type + ")V");
    }

    // Getters, Setters

    public Map<String, Integer> getVarMap() {
        return Collections.unmodifiableMap(this.varMap);
    }
}
