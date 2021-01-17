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

public final class CodeGenerator {

    private static final Map<String, Method> nodeMap;

    static {
        Map<String, Method> map;
        try {
            final Class<?> gen = CodeGenerator.class;
            map = Map.ofEntries(
                    Map.entry("assignment", gen.getDeclaredMethod("assign", ASTNode.class, StringBuilder.class)),
                    Map.entry("expr", gen.getDeclaredMethod("expr", ASTNode.class, StringBuilder.class)),
                    Map.entry("INTEGER_LIT", gen.getDeclaredMethod("integer_lit", ASTNode.class, StringBuilder.class))
            );
        } catch (NoSuchMethodException e) {
            map = null;
            e.printStackTrace();
        }
        nodeMap = map;
    }

    private final Map<String, Integer> varMap;

    private CodeGenerator(Map<String, Integer> varMap) {
        this.varMap = Collections.unmodifiableMap(varMap);
    }

    public static CodeGenerator fromAST(AST tree) {
        final Map<String, Integer> varMap = new HashMap<>();

        // Assign variables to map
        int varCount = 0;
        final Deque<ASTNode> stack = new ArrayDeque<>();
        stack.push(tree.getRoot());
        while (!stack.isEmpty()) {
            final ASTNode current = stack.pop();

            if ("assignment".equals(current.getName())) {
                varCount++;
                varMap.put(current.getValue(), varCount);
                Logger.log("Found local variable " + current.getValue() + ", assigned to slot " + varCount + ".");
            }

            current.getChildren().forEach(stack::push);
        }

        return new CodeGenerator(varMap);
    }

    private static void generateHeader(AST ast, String source, StringBuilder jasmin) {
        System.out.println(" - Generating Jasmin Assembler...");
        final String clazz = ast.getRoot().getChildren().get(1).getValue();

        jasmin.append(".bytecode 49.0\n")
              .append(".source ").append(source).append("\n")
              .append(".class public ").append(clazz).append("\n")
              .append(".super java/lang/Object\n\n");

        System.out.println("Code-generation successfull.");
    }

    private static void generateInit(StringBuilder jasmin) {
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

    public StringBuilder generateCode(AST ast, String source) {
        final StringBuilder jasmin = new StringBuilder();

        generateHeader(ast, source, jasmin);
        generateInit(jasmin);
        this.generateMain(ast, jasmin);

        Logger.log("Jasmin Assembler:\n" + jasmin);

        return jasmin;
    }

    private void generateMain(AST ast, StringBuilder jasmin) {
        jasmin.append(".method public static main([Ljava/lang/String;)V\n");

        this.generateNode(ast.getRoot(), jasmin);

        jasmin.append(".end method\n");

        Logger.log("Generated Jasmin Main.\n");
    }

    private void generateNode(ASTNode node, StringBuilder jasmin) {
        if (nodeMap.containsKey(node.getName())) {
            try {
                nodeMap.get(node.getName()).invoke(this, node, jasmin);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            node.getChildren().forEach(child -> this.generateNode(child, jasmin));
        }
    }

    private void assign(ASTNode node, StringBuilder jasmin) {
        this.generateNode(node.getChildren().get(0), jasmin);

        jasmin.append("istore ")
              .append(this.varMap.get(node.getValue()))
              .append("\n");
    }

    private void expr(ASTNode node, StringBuilder jasmin) {
        this.generateNode(node.getChildren().get(0), jasmin);
        this.generateNode(node.getChildren().get(1), jasmin);

        final String inst = switch (node.getValue()) {
            case "ADD" -> "iadd";
            default -> throw new IllegalStateException("Unexpected value: " + node.getValue());
        };

        jasmin.append(inst)
              .append("\n");
    }

    private void integer_lit(ASTNode node, StringBuilder jasmin) {
        jasmin.append("ldc ")
              .append(node.getValue())
              .append("\n");
    }
}
