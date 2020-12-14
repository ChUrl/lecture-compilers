package parser.ast;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ASTTest {

    @Test
    void testOneNode() {
        ASTNode root = new ASTNode("Wurzel", 1);

        AST tree = new AST(root);
        System.out.println(tree);

        assertThat(tree).hasToString("Wurzel\n");
    }

    @Test
    void testThreeNodesBinary() {
        ASTNode root = new ASTNode("Wurzel", 1);
        ASTNode childA = new ASTNode("A", 1);
        ASTNode childB = new ASTNode("B", 1);

        root.addChild(childA);
        root.addChild(childB);

        AST tree = new AST(root);
        System.out.println(tree);

        assertThat(tree).hasToString("Wurzel\n├── A\n└── B\n");
    }

    @Test
    void testThreeNodesLinear() {
        ASTNode root = new ASTNode("Wurzel", 1);
        ASTNode childA = new ASTNode("A", 1);
        ASTNode childB = new ASTNode("B", 1);

        root.addChild(childA);
        childA.addChild(childB);

        AST tree = new AST(root);
        System.out.println(tree);

        assertThat(tree).hasToString("Wurzel\n└── A\n    └── B\n");
    }
}
