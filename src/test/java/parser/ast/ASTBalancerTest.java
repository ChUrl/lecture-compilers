package parser.ast;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ASTBalancerTest {

    //expr
    //├── expr: SUB
    //|   └── INTEGER_LIT: 2
    //└── INTEGER_LIT: 1
    private static AST tree1() {
        final AST tree = new AST(new ASTNode("epxr", 1));

        final ASTNode right = new ASTNode("INTEGER_LIT", 1);
        right.setValue("1");

        final ASTNode left = new ASTNode("expr", 1);
        left.setValue("SUB");

        final ASTNode lleft = new ASTNode("INTEGER_LIT", 1);
        lleft.setValue("2");
        left.setChildren(lleft);

        tree.getRoot().setChildren(left, right);

        return tree;
    }

    private static AST tree2() {
        final AST tree = new AST(new ASTNode("expr", 1));

        final ASTNode right = new ASTNode("INTEGER_LIT", 1);
        right.setValue("1");

        final ASTNode left = new ASTNode("expr", 1);
        left.setValue("SUB");

        final ASTNode lleft = new ASTNode("expr", 1);
        lleft.setValue("SUB");

        final ASTNode lright = new ASTNode("INTEGER_LIT", 1);
        lright.setValue("2");

        final ASTNode llleft = new ASTNode("INTEGER_LIT", 1);
        llleft.setValue("3");

        lleft.setChildren(llleft);
        left.setChildren(lleft, lright);

        tree.getRoot().setChildren(left, right);

        return tree;
    }

    private static AST tree3() {
        final AST tree = new AST(new ASTNode("expr", 1));

        final ASTNode right = new ASTNode("INTEGER_LIT", 1);
        right.setValue("1");

        final ASTNode left = new ASTNode("expr", 1);
        left.setValue("SUB");

        final ASTNode lleft = new ASTNode("expr", 1);
        lleft.setValue("MUL");

        final ASTNode lright = new ASTNode("INTEGER_LIT", 1);
        lright.setValue("2");

        final ASTNode llleft = new ASTNode("INTEGER_LIT", 1);
        llleft.setValue("3");

        lleft.setChildren(llleft);
        left.setChildren(lleft, lright);

        tree.getRoot().setChildren(left, right);

        return tree;
    }

    @Test
    void testTree1Flip() {
        final AST tree = tree1();

        ASTBalancer.flip(tree);

        assertThat(tree.getRoot().getChildren().get(0).getName()).isEqualTo("INTEGER_LIT");
        assertThat(tree.getRoot().getChildren().get(1).getName()).isEqualTo("expr");
    }

    @Test
    void testTree1Flip2x() {
        final AST tree = tree1();

        ASTBalancer.flip(tree);
        ASTBalancer.flip(tree);

        assertThat(tree).isEqualTo(tree1());
    }

    @Test
    void testTree2Flip() {
        final AST tree = tree2();

        ASTBalancer.flip(tree);

        assertThat(tree.getRoot().getChildren().get(0).getName()).isEqualTo("INTEGER_LIT");
        assertThat(tree.getRoot().getChildren().get(1).getName()).isEqualTo("expr");
        assertThat(tree.getRoot().getChildren().get(1).getChildren().get(0).getName()).isEqualTo("INTEGER_LIT");
        assertThat(tree.getRoot().getChildren().get(1).getChildren().get(1).getName()).isEqualTo("expr");
    }

    @Test
    void testTree2Flip2x() {
        final AST tree = tree2();

        ASTBalancer.flip(tree);
        ASTBalancer.flip(tree);

        assertThat(tree).isEqualTo(tree2());
    }

    @Test
    void testTree1LeftPrecedence() {
        final AST tree = tree1();
        ASTBalancer.flip(tree);

        ASTBalancer.leftPrecedence(tree);

        assertThat(tree.size()).isEqualTo(3);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }

    @Test
    void testTree2LeftPrecedence() {
        final AST tree = tree2();
        ASTBalancer.flip(tree);

        ASTBalancer.leftPrecedence(tree);

        assertThat(tree.size()).isEqualTo(5);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }

    @Test
    void testTree2OperatorPrecedence() {
        final AST tree = tree2();
        ASTBalancer.flip(tree);
        ASTBalancer.leftPrecedence(tree);

        final AST tree1 = tree2();
        ASTBalancer.flip(tree1);
        ASTBalancer.leftPrecedence(tree1);

        ASTBalancer.operatorPrecedence(tree);

        assertThat(tree).isEqualTo(tree1);
    }

    @Test
    void testTree3OperatorPrecedence() {
        final AST tree = tree3();
        ASTBalancer.flip(tree);
        ASTBalancer.leftPrecedence(tree);

        assertThat(tree.getRoot().getValue()).isEqualTo("MUL");

        ASTBalancer.operatorPrecedence(tree);

        assertThat(tree.size()).isEqualTo(5);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }
}
