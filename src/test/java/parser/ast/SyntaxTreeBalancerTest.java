package parser.ast;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SyntaxTreeBalancerTest {

    //expr
    //├── expr: SUB
    //|   └── INTEGER_LIT: 2
    //└── INTEGER_LIT: 1
    private static SyntaxTree tree1() {
        final SyntaxTree tree = new SyntaxTree(new SyntaxTreeNode("epxr", 1));

        final SyntaxTreeNode right = new SyntaxTreeNode("INTEGER_LIT", 1);
        right.setValue("1");

        final SyntaxTreeNode left = new SyntaxTreeNode("expr", 1);
        left.setValue("SUB");

        final SyntaxTreeNode lleft = new SyntaxTreeNode("INTEGER_LIT", 1);
        lleft.setValue("2");
        left.setChildren(lleft);

        tree.getRoot().setChildren(left, right);

        return tree;
    }

    private static SyntaxTree tree2() {
        final SyntaxTree tree = new SyntaxTree(new SyntaxTreeNode("expr", 1));

        final SyntaxTreeNode right = new SyntaxTreeNode("INTEGER_LIT", 1);
        right.setValue("1");

        final SyntaxTreeNode left = new SyntaxTreeNode("expr", 1);
        left.setValue("SUB");

        final SyntaxTreeNode lleft = new SyntaxTreeNode("expr", 1);
        lleft.setValue("SUB");

        final SyntaxTreeNode lright = new SyntaxTreeNode("INTEGER_LIT", 1);
        lright.setValue("2");

        final SyntaxTreeNode llleft = new SyntaxTreeNode("INTEGER_LIT", 1);
        llleft.setValue("3");

        lleft.setChildren(llleft);
        left.setChildren(lleft, lright);

        tree.getRoot().setChildren(left, right);

        return tree;
    }

    private static SyntaxTree tree3() {
        final SyntaxTree tree = new SyntaxTree(new SyntaxTreeNode("expr", 1));

        final SyntaxTreeNode right = new SyntaxTreeNode("INTEGER_LIT", 1);
        right.setValue("1");

        final SyntaxTreeNode left = new SyntaxTreeNode("expr", 1);
        left.setValue("SUB");

        final SyntaxTreeNode lleft = new SyntaxTreeNode("expr", 1);
        lleft.setValue("MUL");

        final SyntaxTreeNode lright = new SyntaxTreeNode("INTEGER_LIT", 1);
        lright.setValue("2");

        final SyntaxTreeNode llleft = new SyntaxTreeNode("INTEGER_LIT", 1);
        llleft.setValue("3");

        lleft.setChildren(llleft);
        left.setChildren(lleft, lright);

        tree.getRoot().setChildren(left, right);

        return tree;
    }

    @Test
    void testTree1Flip() {
        final SyntaxTree tree = tree1();

        ASTBalancer.flip(tree);

        assertThat(tree.getRoot().getChildren().get(0).getName()).isEqualTo("INTEGER_LIT");
        assertThat(tree.getRoot().getChildren().get(1).getName()).isEqualTo("expr");
    }

    @Test
    void testTree1Flip2x() {
        final SyntaxTree tree = tree1();

        ASTBalancer.flip(tree);
        ASTBalancer.flip(tree);

        assertThat(tree).isEqualTo(tree1());
    }

    @Test
    void testTree2Flip() {
        final SyntaxTree tree = tree2();

        ASTBalancer.flip(tree);

        assertThat(tree.getRoot().getChildren().get(0).getName()).isEqualTo("INTEGER_LIT");
        assertThat(tree.getRoot().getChildren().get(1).getName()).isEqualTo("expr");
        assertThat(tree.getRoot().getChildren().get(1).getChildren().get(0).getName()).isEqualTo("INTEGER_LIT");
        assertThat(tree.getRoot().getChildren().get(1).getChildren().get(1).getName()).isEqualTo("expr");
    }

    @Test
    void testTree2Flip2x() {
        final SyntaxTree tree = tree2();

        ASTBalancer.flip(tree);
        ASTBalancer.flip(tree);

        assertThat(tree).isEqualTo(tree2());
    }

    @Test
    void testTree1LeftPrecedence() {
        final SyntaxTree tree = tree1();
        ASTBalancer.flip(tree);

        ASTBalancer.leftPrecedence(tree);

        assertThat(tree.size()).isEqualTo(3);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }

    @Test
    void testTree2LeftPrecedence() {
        final SyntaxTree tree = tree2();
        ASTBalancer.flip(tree);

        ASTBalancer.leftPrecedence(tree);

        assertThat(tree.size()).isEqualTo(5);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }

    @Test
    void testTree2OperatorPrecedence() {
        final SyntaxTree tree = tree2();
        ASTBalancer.flip(tree);
        ASTBalancer.leftPrecedence(tree);

        final SyntaxTree tree1 = tree2();
        ASTBalancer.flip(tree1);
        ASTBalancer.leftPrecedence(tree1);

        ASTBalancer.operatorPrecedence(tree);

        assertThat(tree).isEqualTo(tree1);
    }

    @Test
    void testTree3OperatorPrecedence() {
        final SyntaxTree tree = tree3();
        ASTBalancer.flip(tree);
        ASTBalancer.leftPrecedence(tree);

        assertThat(tree.getRoot().getValue()).isEqualTo("MUL");

        ASTBalancer.operatorPrecedence(tree);

        assertThat(tree.size()).isEqualTo(5);
        assertThat(tree.getRoot().getValue()).isEqualTo("SUB");
    }
}
