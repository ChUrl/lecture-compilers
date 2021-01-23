package codegen.analysis;

import parser.ast.ASTNode;
import util.Logger;

import java.util.ArrayDeque;
import java.util.Deque;

public class StackModel {

    private final Deque<ASTNode> stack;
    private int max;

    public StackModel() {
        this.stack = new ArrayDeque<>();
    }

    public void push(ASTNode root) {
        Logger.log("PUSH " + root.getName() + ": " + root.getValue());
        this.stack.push(root);
        this.updateMax();
    }

    public ASTNode pop() {
        if (this.stack.isEmpty()) {
            throw new IllegalStateException("Can't pop empty stack");
        }

        Logger.log("POP " + this.stack.peek().getName() + ": " + this.stack.peek().getValue());
        return this.stack.pop();
    }

    public ASTNode peek() {
        return this.stack.peek();
    }

    private void updateMax() {
        if (this.stack.size() > this.max) {
            this.max = this.stack.size();
        }
    }

    public int getMax() {
        return this.max;
    }
}
