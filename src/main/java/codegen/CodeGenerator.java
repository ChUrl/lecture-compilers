package codegen;

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

public final class CodeGenerator {

    private static final Map<String, Method> nodeMap;

    static {
        Map<String, Method> map;
        try {
            final Class<?> gen = CodeGenerator.class;
            map = Map.ofEntries(
                    entry("assignment", gen.getDeclaredMethod("assign", ASTNode.class, StringBuilder.class, Map.class)),
                    entry("expr", gen.getDeclaredMethod("expr", ASTNode.class, StringBuilder.class, Map.class)),
                    entry("INTEGER_LIT", gen.getDeclaredMethod("literal", ASTNode.class, StringBuilder.class, Map.class)),
                    entry("IDENTIFIER", gen.getDeclaredMethod("identifier", ASTNode.class, StringBuilder.class, Map.class)),
                    entry("print", gen.getDeclaredMethod("println", ASTNode.class, StringBuilder.class, Map.class))
            );
        } catch (NoSuchMethodException e) {
            map = null;
            e.printStackTrace();
        }
        nodeMap = map;
    }

    private CodeGenerator() {}

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
                Logger.log("New local " + current.getValue() + " variable "
                           + current.getChildren().get(0).getValue()
                           + " assigned to slot " + varCount + ".");
            }

            current.getChildren().forEach(stack::push);
        }

        return Collections.unmodifiableMap(varMap);
    }

    private static void generateHeader(AST ast, String source, StringBuilder jasmin) {
        System.out.println(" - Generating Jasmin Assembler...");
        final String clazz = ast.getRoot().getChildren().get(1).getValue();

        jasmin.append(".bytecode 55.0\n")
              .append(".source ").append(source).append("\n")
              .append(".class public ").append(clazz).append("\n")
              .append(".super java/lang/Object\n");

        System.out.println("Code-generation successfull.");
    }

    private static void generateConstructor(StringBuilder jasmin) {
        jasmin.append(".method public <init>()V\n")
              .append("\t.limit stack 1\n")
              .append("\t.limit locals 1\n")
              .append("\t.line 1\n")
              .append("\t\taload_0\n")
              .append("\t\tinvokespecial java/lang/Object/<init>()V\n")
              .append("\t\treturn\n")
              .append(".end method\n\n");

        Logger.log("Generated Jasmin Init.");
    }

    public static StringBuilder generateCode(AST ast, String source) {
        final StringBuilder jasmin = new StringBuilder();
        final Map<String, Integer> varMap = varMapFromAST(ast);

        generateHeader(ast, source, jasmin);
        generateConstructor(jasmin);
        generateMain(ast, jasmin, varMap);

        Logger.log("Jasmin Assembler:\n" + jasmin);

        return jasmin;
    }

    // TODO: Indentation?
    // TODO: Stack size
    // TODO: Typen
    // TODO: Variablengröße
    private static void generateMain(AST ast, StringBuilder jasmin, Map<String, Integer> varMap) {
        jasmin.append(".method public static main([Ljava/lang/String;)V\n")
              .append(".limit stack 10\n")
              .append(".limit locals ").append(varMap.size() + 1).append("\n");

        generateNode(ast.getRoot().getChildren().get(3).getChildren().get(11), jasmin, varMap); // Skip crap

        jasmin.append("return\n")
              .append(".end method\n");

        Logger.log("Generated Jasmin Main.\n");
    }

    private static void generateNode(ASTNode node, StringBuilder jasmin, Map<String, Integer> varMap) {
        if (nodeMap.containsKey(node.getName())) {
            try {
                nodeMap.get(node.getName()).invoke(null, node, jasmin, varMap);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            node.getChildren().forEach(child -> generateNode(child, jasmin, varMap));
        }
    }

    private static void assign(ASTNode node, StringBuilder jasmin, Map<String, Integer> varMap) {
        generateNode(node.getChildren().get(0), jasmin, varMap);

        jasmin.append("istore ") //!: Type dependant
              .append(varMap.get(node.getValue()))
              .append("\n");
    }

    private static void expr(ASTNode node, StringBuilder jasmin, Map<String, Integer> varMap) {
        String inst = "";

        if (node.getChildren().size() == 1) {
            // Unary operator

            generateNode(node.getChildren().get(0), jasmin, varMap);

            inst = switch(node.getValue()) { //!: Type dependant
                case "ADD" -> "";
                case "SUB" -> "ldc -1\nimul";
                // case "NOT" -> ...
                default -> throw new IllegalStateException("Unexpected value: " + node.getValue());
            };
        } else if (node.getChildren().size() == 2) {
            // Binary operator

            generateNode(node.getChildren().get(0), jasmin, varMap);
            generateNode(node.getChildren().get(1), jasmin, varMap);

            inst = switch (node.getValue()) { //!: Type dependant
                case "ADD" -> "iadd"; // Integer
                case "SUB" -> "isub";
                case "MUL" -> "imul";
                case "DIV" -> "idiv";
                case "MOD" -> "irem"; // Remainder operator
                default -> throw new IllegalStateException("Unexpected value: " + node.getValue());
            };
        }

        jasmin.append(inst)
              .append("\n");
    }

    // Leafs

    private static void literal(ASTNode node, StringBuilder jasmin, Map<String, Integer> varMap) {
        jasmin.append("bipush ") //!: Type dependant
              .append(node.getValue())
              .append("\n");
    }

    private static void identifier(ASTNode node, StringBuilder jasmin, Map<String, Integer> varMap) {
        Logger.log(node.getName() + ": " + node.getValue() + " => iload");

        jasmin.append("iload ") //!: Type dependent
              .append(varMap.get(node.getValue()))
              .append("\n");
    }

    private static void println(ASTNode node, StringBuilder jasmin, Map<String, Integer> varMap) {
        jasmin.append("getstatic java/lang/System/out Ljava/io/PrintStream;\n"); // Push System.out to stack

        generateNode(node.getChildren().get(1).getChildren().get(1), jasmin, varMap);

        jasmin.append("invokevirtual java/io/PrintStream/println(I)V\n"); //!: Type dependent
    }
}
