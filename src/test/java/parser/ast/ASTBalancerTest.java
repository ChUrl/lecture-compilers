package parser.ast;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ASTBalancerTest {

    //EXPR
    //├── EXPR: SUB
    //|   └── INTEGER_LIT: 2
    //└── INTEGER_LIT: 1
    private static AST tree1() {
        AST tree = new AST(new ASTNode("EXPR"));

        ASTNode right = new ASTNode("INTEGER_LIT");
        right.setValue("1");

        ASTNode left = new ASTNode("EXPR");
        left.setValue("SUB");

        ASTNode lleft = new ASTNode("INTEGER_LIT");
        lleft.setValue("2");
        left.setChildren(lleft);

        tree.getRoot().setChildren(left, right);

        return tree;
    }

    private static AST tree2() {
        AST tree = new AST(new ASTNode("EXPR"));

        ASTNode right = new ASTNode("INTEGER_LIT");
        right.setValue("1");

        ASTNode left = new ASTNode("EXPR");
        left.setValue("SUB");

        ASTNode lleft = new ASTNode("EXPR");
        lleft.setValue("SUB");

        ASTNode lright = new ASTNode("INTEGER_LIT");
        lright.setValue("2");

        ASTNode llleft = new ASTNode("INTEGER_LIT");
        llleft.setValue("3");

        lleft.setChildren(llleft);
        left.setChildren(lleft, lright);

        tree.getRoot().setChildren(left, right);

        return tree;
    }

    private static AST tree3() {
        AST tree = new AST(new ASTNode("EXPR"));

        ASTNode right = new ASTNode("INTEGER_LIT");
        right.setValue("1");

        ASTNode left = new ASTNode("EXPR");
        left.setValue("SUB");

        ASTNode lleft = new ASTNode("EXPR");
        lleft.setValue("MUL");

        ASTNode lright = new ASTNode("INTEGER_LIT");
        lright.setValue("2");

        ASTNode llleft = new ASTNode("INTEGER_LIT");
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

        ASTBalancer.flip(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree.getRoot().getChildren().get(0).getName()).isEqualTo("INTEGER_LIT");
        assertThat(tree.getRoot().getChildren().get(1).getName()).isEqualTo("EXPR");
    }

    @Test
    void testTree1Flip2x() {
        AST tree = tree1();
        System.out.println("Before:\n" + tree);

        ASTBalancer.flip(tree);
        ASTBalancer.flip(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree).isEqualTo(tree1());
    }

    @Test
    void testTree2Flip() {
        AST tree = tree2();
        System.out.println("Before:\n" + tree);

        ASTBalancer.flip(tree);
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

        ASTBalancer.flip(tree);
        ASTBalancer.flip(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree).isEqualTo(tree2());
    }

    @Test
    void testTree1LeftPrecedence() {
        AST tree = tree1();
        ASTBalancer.flip(tree);
        System.out.println("Before:\n" + tree);

        ASTBalancer.leftPrecedence(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree.size()).isEqualTo(3);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }

    @Test
    void testTree2LeftPrecedence() {
        AST tree = tree2();
        ASTBalancer.flip(tree);
        System.out.println("Before:\n" + tree);

        ASTBalancer.leftPrecedence(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree.size()).isEqualTo(5);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }

    @Test
    void testTree2OperatorPrecedence() {
        AST tree = tree2();
        ASTBalancer.flip(tree);
        ASTBalancer.leftPrecedence(tree);
        System.out.println("Before:\n" + tree);

        AST tree1 = tree2();
        ASTBalancer.flip(tree1);
        ASTBalancer.leftPrecedence(tree1);

        ASTBalancer.operatorPrecedence(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree).isEqualTo(tree1);
    }

    @Test
    void testTree3OperatorPrecedence() {
        AST tree = tree3();
        ASTBalancer.flip(tree);
        ASTBalancer.leftPrecedence(tree);
        System.out.println("Before:\n" + tree);

        assertThat(tree.getRoot().getValue()).isEqualTo("MUL");

        ASTBalancer.operatorPrecedence(tree);
        System.out.println("After:\n" + tree);

        assertThat(tree.size()).isEqualTo(5);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }
}
