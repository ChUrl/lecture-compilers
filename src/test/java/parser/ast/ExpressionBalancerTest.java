package parser.ast;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionBalancerTest {

    //EXPR
    //├── EXPR: SUB
    //|   └── INTEGER_LIT: 2
    //└── INTEGER_LIT: 1
    private static AST tree1() {
        AST tree = new AST(new Node("EXPR"));

        Node right = new Node("INTEGER_LIT");
        right.setValue("1");

        Node left = new Node("EXPR");
        left.setValue("SUB");

        Node lleft = new Node("INTEGER_LIT");
        lleft.setValue("2");
        left.setChildren(lleft);

        tree.getRoot().setChildren(left, right);

        return tree;
    }

    private static AST tree2() {
        AST tree = new AST(new Node("EXPR"));

        Node right = new Node("INTEGER_LIT");
        right.setValue("1");

        Node left = new Node("EXPR");
        left.setValue("SUB");

        Node lleft = new Node("EXPR");
        lleft.setValue("SUB");

        Node lright = new Node("INTEGER_LIT");
        lright.setValue("2");

        Node llleft = new Node("INTEGER_LIT");
        llleft.setValue("3");

        lleft.setChildren(llleft);
        left.setChildren(lleft, lright);

        tree.getRoot().setChildren(left, right);

        return tree;
    }

    @Test
    void testTree1Flip() {
        AST tree = tree1();
        System.out.println("Before:\n" + tree);

        ExpressionBalancer.flip(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree.getRoot().getChildren().get(0).getName()).isEqualTo("INTEGER_LIT");
        assertThat(tree.getRoot().getChildren().get(1).getName()).isEqualTo("EXPR");
    }

    @Test
    void testTree1Flip2x() {
        AST tree = tree1();
        System.out.println("Before:\n" + tree);

        ExpressionBalancer.flip(tree);
        ExpressionBalancer.flip(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree).isEqualTo(tree1());
    }

    @Test
    void testTree2Flip() {
        AST tree = tree2();
        System.out.println("Before:\n" + tree);

        ExpressionBalancer.flip(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree.getRoot().getChildren().get(0).getName()).isEqualTo("INTEGER_LIT");
        assertThat(tree.getRoot().getChildren().get(1).getName()).isEqualTo("EXPR");
        assertThat(tree.getRoot().getChildren().get(1).getChildren().get(0).getName()).isEqualTo("INTEGER_LIT");
        assertThat(tree.getRoot().getChildren().get(1).getChildren().get(1).getName()).isEqualTo("EXPR");
    }

    @Test
    void testTree2Flip2x() {
        AST tree = tree2();
        System.out.println("Before:\n" + tree);

        ExpressionBalancer.flip(tree);
        ExpressionBalancer.flip(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree).isEqualTo(tree2());
    }

    @Test
    void testTree1Rotate() {
        AST tree = tree1();
        ExpressionBalancer.flip(tree);
        System.out.println("Before:\n" + tree);

        ExpressionBalancer.leftPrecedence(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree.size()).isEqualTo(3);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }

    @Test
    void testTree2Rotate() {
        AST tree = tree2();
        ExpressionBalancer.flip(tree);
        System.out.println("Before:\n" + tree);

        ExpressionBalancer.leftPrecedence(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree.size()).isEqualTo(5);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }
}
