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
                    entry("cond", gen.getDeclaredMethod("cond", ASTNode.class)),
                    entry("loop", gen.getDeclaredMethod("loop", ASTNode.class)),
                    entry("assignment", gen.getDeclaredMethod("assign", ASTNode.class)),
                    entry("expr", gen.getDeclaredMethod("expr", ASTNode.class)),
                    // Leafs
                    entry("INTEGER_LIT", gen.getDeclaredMethod("intLiteral", ASTNode.class)),
                    entry("BOOLEAN_LIT", gen.getDeclaredMethod("boolLiteral", ASTNode.class)),
                    entry("STRING_LIT", gen.getDeclaredMethod("stringLiteral", ASTNode.class)),
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

    private static String genComparisonInst(String inst, String labelPre, int currentLabel) {
        return inst + " " + labelPre + "true" + currentLabel // If not equal jump to NEtrue
               + "\n\t\tldc 0" // If false load 0
               + "\n\t\tgoto " + labelPre + "end" + currentLabel // If false skip true branch
               + "\n" + labelPre + "true" + currentLabel + ":"
               + "\n\t\tldc 1" // if true load 1
               + "\n" + labelPre + "end" + currentLabel + ":";
    }

    private void generateHeader(String source) {
        System.out.println(" - Generating Jasmin Assembler...");
        final String clazz = this.tree.getRoot().getChildren().get(1).getValue();

        this.jasmin.append(".bytecode 49.0\n") // 55.0 has stricter verification => stackmap frames missing
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

        log("Jasmin Assembler:\n" + "-".repeat(100) + "\n" + this.jasmin + "-".repeat(100));
        System.out.println("Code-generation successful.");

        return this.jasmin;
    }

    // TODO: Stack size
    private void generateMain() {
        this.jasmin.append(".method public static main([Ljava/lang/String;)V\n")
                   .append("\t.limit stack 10\n")
                   .append("\t.limit locals ").append(this.varMap.size() + 1).append("\n");

        // Needs to be skipped to not trigger generation for IDENTIFIER: args or IDENTIFIER: ClassName
        this.generateNode(this.tree.getRoot().getChildren().get(3).getChildren().get(11));

        this.jasmin.append("\t\treturn\n")
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

    // ifeq - if value is 0
    // ifne - if value is not 0
    private void cond(ASTNode node) {
        final int currentLabel = this.labelCounter;
        this.labelCounter++;

        // Condition
        this.generateNode(node.getChildren().get(0));

        // Jump
        this.jasmin.append("\t\tifeq IFfalse").append(currentLabel).append("\n"); // ifeq: == 0 => false

        // IFtrue branch
        this.generateNode(node.getChildren().get(1));
        this.jasmin.append("\t\tgoto IFend").append(currentLabel).append("\n");

        // IFfalse branch
        this.jasmin.append("IFfalse").append(currentLabel).append(":\n");
        if (node.getChildren().size() == 3) {
            // Else exists

            this.generateNode(node.getChildren().get(2));
        }

        // IFend branch
        this.jasmin.append("IFend").append(currentLabel).append(":\n");
    }

    private void loop(ASTNode node) {
        final int currentLabel = this.labelCounter;
        this.labelCounter++;

        this.jasmin.append("LOOPstart").append(currentLabel).append(":\n");

        // Condition
        this.generateNode(node.getChildren().get(0).getChildren().get(1));

        // Jump
        this.jasmin.append("\t\tifeq LOOPend").append(currentLabel).append("\n"); // ifeq: == 0 => Loop stopped

        // Loop body
        this.generateNode(node.getChildren().get(1));
        this.jasmin.append("\t\tgoto LOOPstart").append(currentLabel).append("\n"); // Jump to Loop start

        // Loop end
        this.jasmin.append("LOOPend").append(currentLabel).append(":\n");
    }

    private void assign(ASTNode node) {
        this.generateNode(node.getChildren().get(0));

        final String type = this.nodeTypeMap.get(node.getChildren().get(0));
        final String inst = switch (type) {
            case "INTEGER_TYPE", "BOOLEAN_TYPE" -> "istore ";
            case "STRING_TYPE" -> "astore ";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        log("assign(): " + node.getName() + ": " + node.getValue() + " => " + inst);

        this.jasmin.append("\t\t")
                   .append(inst)
                   .append(this.varMap.get(node.getValue()))
                   .append("\n");
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

        if (node.getChildren().size() == 1) {
            // Unary operator

            this.generateNode(node.getChildren().get(0));

            inst = switch (node.getValue()) {
                case "ADD" -> "";
                case "SUB" -> "ldc -1\n\t\timul";
                default -> throw new IllegalStateException("Unexpected value: " + node.getValue());
            };
        } else if (node.getChildren().size() == 2) {
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

        this.jasmin.append("\t\t")
                   .append(inst)
                   .append("\n");
    }

    private void boolExpr(ASTNode node) {
        String inst = "";

        if (node.getChildren().size() == 1) {
            // Unary operator

            if (!"NOT".equals(node.getValue())) {
                // Diese Möglichkeit gibts eigentlich nicht
                throw new IllegalStateException("Unexpected value: " + node.getValue());
            }

            this.generateNode(node.getChildren().get(0));

            // 1 -> 0, 0 -> 1?
            //            inst = "ldc 1\n\t\tisub\n\t\tdup\n\t\timul"; // Subtract 1 and square for now
            inst = "ldc 1\n\t\tixor"; // 0  ^1 = 1, 1  ^1 = 0

        } else if (node.getChildren().size() == 2) {
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

            inst = switch (node.getValue()) {
                case "AND" -> "iand"; // Boolean
                case "OR" -> "ior";
                case "EQUAL" -> genComparisonInst(cmpeq, "EQ", currentLabel);
                case "NOT_EQUAL" -> genComparisonInst(cmpne, "NE", currentLabel);
                case "LESS" -> genComparisonInst("if_icmplt", "LT", currentLabel);
                case "LESS_EQUAL" -> genComparisonInst("if_icmple", "LE", currentLabel);
                case "GREATER" -> genComparisonInst("if_icmpgt", "GT", currentLabel);
                case "GREATER_EQUAL" -> genComparisonInst("if_icmpge", "GE", currentLabel);
                default -> throw new IllegalStateException("Unexpected value: " + node.getValue());
            };
        }

        log("boolExpr(): " + node.getName() + ": " + node.getValue() + " => \n\t\t" + inst);

        this.jasmin.append("\t\t")
                   .append(inst)
                   .append("\n");
    }

    // Leafs

    private void intLiteral(ASTNode node) {
        log("literal(): " + node.getName() + ": " + node.getValue() + " => ldc");

        // bipush only pushes 1 byte as int
        this.jasmin.append("\t\tldc ")
                   .append(node.getValue())
                   .append("\n");
    }

    private void stringLiteral(ASTNode node) {
        log("literal(): " + node.getName() + ": " + node.getValue() + " => ldc");

        // bipush only pushes 1 byte as int
        this.jasmin.append("\t\tldc ")
                   .append(node.getValue())
                   .append("\n");
    }

    private void boolLiteral(ASTNode node) {
        log("booleanLiteral(): " + node.getName() + ": " + node.getValue() + " => ldc");

        final String val = "true".equals(node.getValue()) ? "1" : "0";

        this.jasmin.append("\t\tldc ")
                   .append(val)
                   .append("\n");
    }

    private void identifier(ASTNode node) {
        final String type = this.nodeTypeMap.get(node);
        final String inst = switch (type) {
            case "INTEGER_TYPE", "BOOLEAN_TYPE" -> "iload ";
            case "STRING_TYPE" -> "aload ";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        log("identifier(): " + node.getName() + ": " + node.getValue() + " => " + inst);

        this.jasmin.append("\t\t")
                   .append(inst)
                   .append(this.varMap.get(node.getValue()))
                   .append("\n");
    }

    private void println(ASTNode node) {
        this.jasmin.append("\t\tgetstatic java/lang/System/out Ljava/io/PrintStream;\n"); // Push System.out to stack

        final ASTNode expr = node.getChildren().get(1).getChildren().get(1);
        final String type = switch (this.nodeTypeMap.get(expr)) {
            case "BOOLEAN_TYPE" -> "Z";
            case "INTEGER_TYPE" -> "I";
            case "STRING_TYPE" -> "Ljava/lang/String;";
            default -> throw new IllegalStateException("Unexpected value: " + this.nodeTypeMap.get(expr));
        };

        this.generateNode(expr);

        log("println(): " + expr.getName() + ": " + expr.getValue() + " => " + type);

        this.jasmin.append("\t\tinvokevirtual java/io/PrintStream/println(")
                   .append(type)
                   .append(")V\n");
    }
}
