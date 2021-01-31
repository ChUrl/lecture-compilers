package parser.ast;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SyntaxTreeTest {

    @Test
    void testOneNode() {
        final SyntaxTreeNode root = new SyntaxTreeNode("Wurzel", 1);

        final SyntaxTree tree = new SyntaxTree(root);

        assertThat(tree).hasToString("Wurzel\n");
    }

    @Test
    void testThreeNodesBinary() {
        final SyntaxTreeNode root = new SyntaxTreeNode("Wurzel", 1);
        final SyntaxTreeNode childA = new SyntaxTreeNode("A", 1);
        final SyntaxTreeNode childB = new SyntaxTreeNode("B", 1);

        root.addChild(childA);
        root.addChild(childB);

        final SyntaxTree tree = new SyntaxTree(root);

        assertThat(tree).hasToString("Wurzel\n├── A\n└── B\n");
    }

    @Test
    void testThreeNodesLinear() {
        final SyntaxTreeNode root = new SyntaxTreeNode("Wurzel", 1);
        final SyntaxTreeNode childA = new SyntaxTreeNode("A", 1);
        final SyntaxTreeNode childB = new SyntaxTreeNode("B", 1);

        root.addChild(childA);
        childA.addChild(childB);

        final SyntaxTree tree = new SyntaxTree(root);

        assertThat(tree).hasToString("Wurzel\n└── A\n    └── B\n");
    }
}
