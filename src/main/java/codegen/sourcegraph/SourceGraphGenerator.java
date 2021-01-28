package codegen.sourcegraph;

import codegen.CodeGenerationException;
import codegen.analysis.StackSizeAnalyzer;
import parser.ast.AST;
import parser.ast.ASTNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;
import static util.Logger.log;

public final class SourceGraphGenerator {

    private static final Map<String, Method> methodMap;

    static {
        Map<String, Method> map;
        try {
            final Class<?> gen = SourceGraphGenerator.class;
            map = Map.ofEntries(
                    entry("cond", gen.getDeclaredMethod("cond", ASTNode.class)),
                    entry("loop", gen.getDeclaredMethod("loop", ASTNode.class)),
                    entry("assignment", gen.getDeclaredMethod("assign", ASTNode.class)),
                    entry("expr", gen.getDeclaredMethod("expr", ASTNode.class)),
                    // Leafs
                    entry("INTEGER_LIT", gen.getDeclaredMethod("intStringLiteral", ASTNode.class)),
                    entry("BOOLEAN_LIT", gen.getDeclaredMethod("boolLiteral", ASTNode.class)),
                    entry("STRING_LIT", gen.getDeclaredMethod("intStringLiteral", ASTNode.class)),
                    entry("IDENTIFIER", gen.getDeclaredMethod("identifier", ASTNode.class)),
                    entry("print", gen.getDeclaredMethod("println", ASTNode.class))
            );
        } catch (NoSuchMethodException e) {
            map = null;
            e.printStackTrace();
        }
        methodMap = map;
    }

    private final Map<String, Integer> varMap;
    private final Map<ASTNode, String> nodeTypeMap;
    private final String source;
    private final AST tree;
    private final SourceGraph graph;

    private int labelCounter;

    private SourceGraphGenerator(Map<String, Integer> varMap, AST tree, Map<ASTNode, String> nodeTypeMap, SourceGraph graph, String source) {
        this.varMap = varMap;
        this.tree = tree;
        this.nodeTypeMap = nodeTypeMap;
        this.graph = graph;
        this.source = source;
    }

    public static SourceGraphGenerator fromAST(AST tree, Map<ASTNode, String> nodeTypeMap, String source) {
        if (!tree.getRoot().hasChildren()) {
            throw new CodeGenerationException("Empty File can't be compiled");
        }

        final Map<String, Integer> varMap = varMapFromAST(tree);

        final String bytecodeVersion = "49.0";
        final String clazz = tree.getRoot().getChildren().get(1).getValue();
        final int stackSize = StackSizeAnalyzer.runStackModel(tree);
        final int localCount = varMap.size() + 1;
        final SourceGraph graph = new SourceGraph(bytecodeVersion, source, clazz, stackSize, localCount);

        return new SourceGraphGenerator(varMap, tree, nodeTypeMap, graph, source);
    }

    private static Map<String, Integer> varMapFromAST(AST tree) {
        final Map<String, Integer> varMap = new HashMap<>();

        // Assign variables to map
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

    public SourceGraph generateCode() {
        System.out.println(" - Generating Jasmin assembler...");

        this.generateNode(this.tree.getRoot().getChildren().get(3).getChildren().get(11));

        log("\n\nJasmin Assembler from FlowGraph:\n" + "-".repeat(100) + "\n" + this.graph + "-".repeat(100));
        log("\n\nFlowGraph print:\n" + "-".repeat(100) + "\n" + this.graph.print() + "-".repeat(100));
        System.out.println("Code-generation successful.");

        return this.graph;
    }

    private void generateNode(ASTNode node) {
        if (methodMap.containsKey(node.getName())) {
            try {
                methodMap.get(node.getName()).invoke(this, node);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            node.getChildren().forEach(this::generateNode);
        }
    }

    // ifeq - if value is 0
    // ifne - if value is not 0
    private void cond(ASTNode node) {
        final int currentLabel = this.labelCounter;
        this.labelCounter++;

        // Condition
        this.generateNode(node.getChildren().get(0));

        // Jump
        this.graph.addJump("ifeq", "IFfalse" + currentLabel);

        // IFtrue branch
        this.generateNode(node.getChildren().get(1));
        this.graph.addJump("goto", "IFend" + currentLabel);

        // IFfalse branch
        this.graph.addLabel("IFfalse" + currentLabel);
        if (node.getChildren().size() == 3) {
            // Else exists

            this.generateNode(node.getChildren().get(2));
        }

        // IFend branch
        this.graph.addLabel("IFend" + currentLabel);
    }

    private void loop(ASTNode node) {
        final int currentLabel = this.labelCounter;
        this.labelCounter++;

        this.graph.addLabel("LOOPstart" + currentLabel);

        // Condition
        this.generateNode(node.getChildren().get(0).getChildren().get(1));

        // Jump
        this.graph.addJump("ifeq", "LOOPend" + currentLabel);

        // Loop body
        this.generateNode(node.getChildren().get(1));
        this.graph.addJump("goto", "LOOPstart" + currentLabel);

        // Loop end
        this.graph.addLabel("LOOPend" + currentLabel);
    }

    private void assign(ASTNode node) { //! Stack - 1
        this.generateNode(node.getChildren().get(0));

        final String type = this.nodeTypeMap.get(node.getChildren().get(0));
        final String inst = switch (type) {
            case "INTEGER_TYPE", "BOOLEAN_TYPE" -> "istore";
            case "STRING_TYPE" -> "astore";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        log("assign(): " + node.getName() + ": " + node.getValue() + " => " + inst);

        this.graph.addInst(inst, this.varMap.get(node.getValue()).toString());
    }

    private void expr(ASTNode node) {
        if ("INTEGER_TYPE".equals(this.nodeTypeMap.get(node))) {
            this.intExpr(node);
        } else if ("BOOLEAN_TYPE".equals(this.nodeTypeMap.get(node))) {
            this.boolExpr(node);
        }
    }

    private void intExpr(ASTNode node) {
        String inst = "";

        if (node.getChildren().size() == 1) { //! Stack + 0
            // Unary operator

            this.generateNode(node.getChildren().get(0));

            inst = switch (node.getValue()) {
                case "ADD" -> "";
                case "SUB" -> "ineg";
                default -> throw new IllegalStateException("Unexpected value: " + node.getValue());
            };
        } else if (node.getChildren().size() == 2) { //! Stack - 1
            // Binary operator

            this.generateNode(node.getChildren().get(0));
            this.generateNode(node.getChildren().get(1));

            inst = switch (node.getValue()) {
                case "ADD" -> "iadd"; // Integer
                case "SUB" -> "isub";
                case "MUL" -> "imul";
                case "DIV" -> "idiv";
                case "MOD" -> "irem"; // Remainder operator
                default -> throw new IllegalStateException("Unexpected value: " + node.getValue());
            };
        }

        log("intExpr(): " + node.getName() + ": " + node.getValue() + " => " + inst);

        this.graph.addInst(inst);
    }

    private void boolExpr(ASTNode node) {
        if (node.getChildren().size() == 1) { //! Stack + 1
            // Unary operator

            if (!"NOT".equals(node.getValue())) {
                // Diese Möglichkeit gibts eigentlich nicht
                throw new IllegalStateException("Unexpected value: " + node.getValue());
            }

            this.generateNode(node.getChildren().get(0));

            // 0  ^1 = 1, 1  ^1 = 0
            this.graph.addInst("ldc", "1");
            this.graph.addInst("ixor");

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

            switch (node.getValue()) {
                case "AND" -> this.graph.addInst("iand"); // Boolean
                case "OR" -> this.graph.addInst("ior");
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

    private void genComparisonInst(String cmpInst, String labelPre, int currentLabel) {
        this.graph.addJump(cmpInst, labelPre + "true" + currentLabel); // If not equal jump to NEtrue
        this.graph.addInst("ldc", "0"); // If false load 0
        this.graph.addJump("goto", labelPre + "end" + currentLabel); // If false skip to true
        this.graph.addLabel(labelPre + "true" + currentLabel);
        this.graph.addInst("ldc", "1"); // If true load 1
        this.graph.addLabel(labelPre + "end" + currentLabel);
    }

    // Leafs

    private void intStringLiteral(ASTNode node) { //! Stack + 1
        log("literal(): " + node.getName() + ": " + node.getValue() + " => ldc");

        // bipush only pushes 1 byte as int
        this.graph.addInst("ldc", node.getValue());
    }

    private void boolLiteral(ASTNode node) { //! Stack + 1
        log("booleanLiteral(): " + node.getName() + ": " + node.getValue() + " => ldc");

        final String val = "true".equals(node.getValue()) ? "1" : "0";

        this.graph.addInst("ldc", val);
    }

    private void identifier(ASTNode node) { //! Stack + 1
        final String type = this.nodeTypeMap.get(node);
        final String inst = switch (type) {
            case "INTEGER_TYPE", "BOOLEAN_TYPE" -> "iload";
            case "STRING_TYPE" -> "aload";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        log("identifier(): " + node.getName() + ": " + node.getValue() + " => " + inst);

        this.graph.addInst(inst, this.varMap.get(node.getValue()).toString());
    }

    private void println(ASTNode node) { //! Stack + 1
        this.graph.addInst("getstatic", "java/lang/System/out", "Ljava/io/PrintStream;");

        final ASTNode expr = node.getChildren().get(1).getChildren().get(1);
        final String type = switch (this.nodeTypeMap.get(expr)) {
            case "BOOLEAN_TYPE" -> "Z";
            case "INTEGER_TYPE" -> "I";
            case "STRING_TYPE" -> "Ljava/lang/String;";
            default -> throw new IllegalStateException("Unexpected value: " + this.nodeTypeMap.get(expr));
        };

        this.generateNode(expr);

        log("println(): " + expr.getName() + ": " + expr.getValue() + " => " + type);

        this.graph.addInst("invokevirtual", "java/io/PrintStream/println(" + type + ")V");
    }
}
