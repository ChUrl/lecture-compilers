package parser.ast;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ASTTest {

    @Test
    void testOneNode() {
        final ASTNode root = new ASTNode("Wurzel", 1);

        final AST tree = new AST(root);

        assertThat(tree).hasToString("Wurzel\n");
    }

    @Test
    void testThreeNodesBinary() {
        final ASTNode root = new ASTNode("Wurzel", 1);
        final ASTNode childA = new ASTNode("A", 1);
        final ASTNode childB = new ASTNode("B", 1);

        root.addChild(childA);
        root.addChild(childB);

        final AST tree = new AST(root);

        assertThat(tree).hasToString("Wurzel\n├── A\n└── B\n");
    }

    @Test
    void testThreeNodesLinear() {
        final ASTNode root = new ASTNode("Wurzel", 1);
        final ASTNode childA = new ASTNode("A", 1);
        final ASTNode childB = new ASTNode("B", 1);

        root.addChild(childA);
        childA.addChild(childB);

        final AST tree = new AST(root);

        assertThat(tree).hasToString("Wurzel\n└── A\n    └── B\n");
    }
}
