package util.ast;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ASTTest {

    @Test
    void testOneNode() {
        Node root = new Node("Wurzel");

        AST tree = new AST(root);
        System.out.println(tree);

        assertThat(tree).hasToString("Wurzel\n");
    }

    @Test
    void testThreeNodesBinary() {
        Node root = new Node("Wurzel");
        Node childA = new Node("A");
        Node childB = new Node("B");

        root.addChild(childA);
        root.addChild(childB);

        AST tree = new AST(root);
        System.out.println(tree);

        assertThat(tree).hasToString("Wurzel\n├── A\n└── B\n");
    }

    @Test
    void testThreeNodesLinear() {
        Node root = new Node("Wurzel");
        Node childA = new Node("A");
        Node childB = new Node("B");

        root.addChild(childA);
        childA.addChild(childB);

        AST tree = new AST(root);
        System.out.println(tree);

        assertThat(tree).hasToString("Wurzel\n└── A\n    └── B\n");
    }
}
