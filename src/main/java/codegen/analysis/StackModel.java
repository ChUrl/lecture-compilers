package codegen.analysis;

import parser.ast.SyntaxTreeNode;
import util.Logger;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Simuliert den Laufzeit-Stack während einer Programmausführung.
 */
public class StackModel {

    private final Deque<SyntaxTreeNode> stack;

    /**
     * Speichert die maximale Stacktiefe während der Ausführung.
     */
    private int max;

    public StackModel() {
        this.stack = new ArrayDeque<>();
    }

    public void push(SyntaxTreeNode root) {
        this.stack.push(root);
        this.updateMax();
    }

    public void pop() {
        if (this.stack.isEmpty()) {
            throw new IllegalStateException("Can't pop empty stack");
        }

        this.stack.pop();
    }

    private void updateMax() {
        if (this.stack.size() > this.max) {
            this.max = this.stack.size();
            Logger.logInfo(" :: New maximum: " + this.max, StackModel.class);
        }
    }

    public int getMax() {
        return this.max;
    }
}
