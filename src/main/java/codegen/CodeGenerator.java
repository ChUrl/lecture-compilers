package codegen;

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

public final class CodeGenerator {

    private static final Map<String, Method> methodMap;

    static {
        Map<String, Method> map;
        try {
            final Class<?> gen = CodeGenerator.class;
            map = Map.ofEntries(
                    entry("assignment", gen.getDeclaredMethod("assign", ASTNode.class)),
                    entry("expr", gen.getDeclaredMethod("expr", ASTNode.class)),
                    entry("INTEGER_LIT", gen.getDeclaredMethod("literal", ASTNode.class)),
                    entry("BOOLEAN_LIT", gen.getDeclaredMethod("literal", ASTNode.class)),
                    entry("STRING_LIT", gen.getDeclaredMethod("literal", ASTNode.class)),
                    entry("IDENTIFIER", gen.getDeclaredMethod("identifier", ASTNode.class)),
                    entry("print", gen.getDeclaredMethod("println", ASTNode.class)),
                    entry("cond", gen.getDeclaredMethod("cond", ASTNode.class))
            );
        } catch (NoSuchMethodException e) {
            map = null;
            e.printStackTrace();
        }
        methodMap = map;
    }

    private final Map<String, Integer> varMap;
    private final Map<ASTNode, String> nodeTypeMap;
    private final StringBuilder jasmin;
    private final AST tree;

    private int labelCounter;

    private CodeGenerator(Map<String, Integer> varMap, AST tree, Map<ASTNode, String> nodeTypeMap) {
        this.varMap = varMap;
        this.tree = tree;
        this.nodeTypeMap = nodeTypeMap;
        this.jasmin = new StringBuilder();
    }

    public static CodeGenerator fromAST(AST tree, Map<ASTNode, String> nodeTypeMap) {
        return new CodeGenerator(varMapFromAST(tree), tree, nodeTypeMap);
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

    private void generateHeader(String source) {
        System.out.println(" - Generating Jasmin Assembler...");
        final String clazz = this.tree.getRoot().getChildren().get(1).getValue();

        this.jasmin.append(".bytecode 55.0\n")
                   .append(".source ").append(source).append("\n")
                   .append(".class public ").append(clazz).append("\n")
                   .append(".super java/lang/Object\n");

        log("Generated Jasmin Header.");
    }

    private void generateConstructor() {
        this.jasmin.append(".method public <init>()V\n")
                   .append("\t.limit stack 1\n")
                   .append("\t.limit locals 1\n")
                   .append("\t.line 1\n")
                   .append("\t\taload_0\n")
                   .append("\t\tinvokespecial java/lang/Object/<init>()V\n")
                   .append("\t\treturn\n")
                   .append(".end method\n\n");

        log("Generated Jasmin Constructor.");
    }

    public StringBuilder generateCode(String source) {
        this.generateHeader(source);
        this.generateConstructor();
        this.generateMain();

        log("Jasmin Assembler:\n" + this.jasmin);
        System.out.println("Code-generation successfull.");

        return this.jasmin;
    }

    // TODO: Indentation?
    // TODO: Stack size
    // TODO: Typen
    // TODO: Variablengröße
    private void generateMain() {
        this.jasmin.append(".method public static main([Ljava/lang/String;)V\n")
                   .append(".limit stack 10\n")
                   .append(".limit locals ").append(this.varMap.size() + 1).append("\n");

        // Needs to be skipped to not trigger generation for IDENTIFIER: args or IDENTIFIER: ClassName
        this.generateNode(this.tree.getRoot().getChildren().get(3).getChildren().get(11));

        this.jasmin.append("return\n")
                   .append(".end method\n");

        log("Generated Jasmin Main.\n");
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

    // TODO: boolean expressions for conditions
    // ifeq - if value is 0
    // ifne - if value is not 0
    private void cond(ASTNode node) {
        this.labelCounter++;

        // Condition
        this.generateNode(node.getChildren().get(0));

        // Jump
        this.jasmin.append("ifeq LabelFalse").append(this.labelCounter).append("\n")
                   .append("ifne LabelTrue").append(this.labelCounter).append("\n");

        // If branch
        this.jasmin.append("LabelTrue").append(this.labelCounter).append(":\n");
        this.generateNode(node.getChildren().get(1));
        this.jasmin.append("goto LabelFinish").append(this.labelCounter).append("\n");

        // Else branch
        this.jasmin.append("LabelFalse").append(this.labelCounter).append(":\n");
        if (node.getChildren().size() == 3) {
            // Else exists

            this.generateNode(node.getChildren().get(2));
        }
        this.jasmin.append("goto LabelFinish").append(this.labelCounter).append("\n"); // Optional

        this.jasmin.append("LabelFinish").append(this.labelCounter).append(":\n");
    }

    private void assign(ASTNode node) {
        this.generateNode(node.getChildren().get(0));

        log("assign(): " + node.getName() + ": " + node.getValue() + " => istore");

        this.jasmin.append("istore ") //!: Type dependant
                   .append(this.varMap.get(node.getValue()))
                   .append("\n");
    }

    private void expr(ASTNode node) {
        String inst = "";

        if (node.getChildren().size() == 1) {
            // Unary operator

            this.generateNode(node.getChildren().get(0));

            inst = switch (node.getValue()) { //!: Type dependant
                case "ADD" -> "";
                case "SUB" -> "ldc -1\nimul";
                // case "NOT" -> ...
                default -> throw new IllegalStateException("Unexpected value: " + node.getValue());
            };
        } else if (node.getChildren().size() == 2) {
            // Binary operator

            this.generateNode(node.getChildren().get(0));
            this.generateNode(node.getChildren().get(1));

            inst = switch (node.getValue()) { //!: Type dependant
                case "ADD" -> "iadd"; // Integer
                case "SUB" -> "isub";
                case "MUL" -> "imul";
                case "DIV" -> "idiv";
                case "MOD" -> "irem"; // Remainder operator
                default -> throw new IllegalStateException("Unexpected value: " + node.getValue());
            };
        }

        log("expr(): " + node.getName() + ": " + node.getValue() + " => " + inst);

        this.jasmin.append(inst)
                   .append("\n");
    }

    // Leafs

    private void literal(ASTNode node) {
        log("literal(): " + node.getName() + ": " + node.getValue() + " => ldc");

        // bipush only pushes 1 byte as int
        this.jasmin.append("ldc ") //!: Type dependant
                   .append(node.getValue())
                   .append("\n");
    }

    private void identifier(ASTNode node) {
        log("identifier(): " + node.getName() + ": " + node.getValue() + " => iload");

        this.jasmin.append("iload ") //!: Type dependent
                   .append(this.varMap.get(node.getValue()))
                   .append("\n");
    }

    private void println(ASTNode node) {
        this.jasmin.append("getstatic java/lang/System/out Ljava/io/PrintStream;\n"); // Push System.out to stack

        this.generateNode(node.getChildren().get(1).getChildren().get(1));

        this.jasmin.append("invokevirtual java/io/PrintStream/println(I)V\n"); //!: Type dependent
    }
}
