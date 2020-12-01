package util.dfa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DFATest {

    @Test
    @DisplayName("DFA has 2 Nodes, 2 Edges (Valid transitions)")
    void testTwoNodesTwoEdges() {
        Set<INode> nodes = new HashSet<>();
        Node b = new Node("B", false);
        Node c = new Node("C", true);
        nodes.add(b);
        nodes.add(c);

        Set<IEdge> edges = new HashSet<>();
        edges.add(new Edge(c, '0', c));
        edges.add(new Edge(c, '1', b));

        DFA dfa = new DFA(nodes, c, edges); // Hier werden die Nodes mit Edges befüllt

        assertThat(c.getNext('0').getName()).isEqualTo("C");
        assertThat(c.getNext('1').getName()).isEqualTo("B");
    }

    @Test
    @DisplayName("DFA has 2 Nodes, 1 Edge (Invalid transition)")
    void testTwoNodesNoSuchEdge() {
        Set<INode> nodes = new HashSet<>();
        Node b = new Node("B", false);
        Node c = new Node("C", true);
        nodes.add(b);
        nodes.add(c);

        Set<IEdge> edges = new HashSet<>();
        edges.add(new Edge(c, '0', c));

        DFA dfa = new DFA(nodes, c, edges);

        assertThat(c.getNext('0').getName()).isEqualTo("C");
        assertThatThrownBy(() -> c.getNext('1')).isInstanceOf(DFANoSuchEdgeException.class);
    }

    @Test
    @DisplayName("DFA has 5 Nodes, 10 Edges (Valid transitions)")
    void testAcceptByFinalState() {
        Set<INode> nodes = new HashSet<>();
        Node a = new Node("A", false);
        Node b = new Node("B", false);
        Node c = new Node("C", true);
        Node d = new Node("D", false);
        Node e = new Node("E", false);
        nodes.add(a);
        nodes.add(b);
        nodes.add(c);
        nodes.add(d);
        nodes.add(e);

        Set<IEdge> edges = new HashSet<>();
        edges.add(new Edge(c, '0', c));
        edges.add(new Edge(c, '1', b));
        edges.add(new Edge(b, '0', e));
        edges.add(new Edge(b, '1', a));
        edges.add(new Edge(a, '0', b));
        edges.add(new Edge(a, '1', e));
        edges.add(new Edge(d, '0', a));
        edges.add(new Edge(d, '1', d));
        edges.add(new Edge(e, '0', d));
        edges.add(new Edge(e, '1', c));

        DFA dfa = new DFA(nodes, c, edges);
        // System.out.println(DFAViewUtil.toDot(dfa));

        assertThat(dfa.accept("10010")).isEqualTo("CBEDDA ablehnen");
        assertThat(dfa.accept("11001")).isEqualTo("CBABEC akzeptieren");
        assertThat(dfa.accept("1")).isEqualTo("CB ablehnen");

        assertThat(dfa.accept("10")).isEqualTo("CBE ablehnen");

        assertThat(dfa.accept("1011")).isEqualTo("CBECB ablehnen");

        assertThat(dfa.accept("1011")).isEqualTo("CBECB ablehnen");
        assertThat(dfa.accept("100100011")).isEqualTo("CBEDDABECB ablehnen");

        assertThat(dfa.accept("11001")).isEqualTo("CBABEC akzeptieren");
        assertThat(dfa.accept("110010")).isEqualTo("CBABECC akzeptieren");
        assertThat(dfa.accept("1100100")).isEqualTo("CBABECCC akzeptieren");
        assertThat(dfa.accept("11001000")).isEqualTo("CBABECCCC akzeptieren");
        assertThat(dfa.accept("101")).isEqualTo("CBEC akzeptieren");
        assertThat(dfa.accept("10010001")).isEqualTo("CBEDDABEC akzeptieren");
        assertThat(dfa.accept("1001110001")).isEqualTo("CBEDDDDABEC akzeptieren");
        assertThat(dfa.accept("100111110001")).isEqualTo("CBEDDDDDDABEC akzeptieren");
    }

    @Test
    @DisplayName("DFA has 5 Nodes, 10 Edges (Invalid transitions)")
    void testAcceptByInput() {
        Set<INode> nodes = new HashSet<>();
        Node a = new Node("A", false);
        Node b = new Node("B", true);
        Node c = new Node("C", false);
        Node d = new Node("D", true);
        Node e = new Node("E", false);
        nodes.add(a);
        nodes.add(b);
        nodes.add(c);
        nodes.add(d);
        nodes.add(e);

        Set<IEdge> edges = new HashSet<>();
        edges.add(new Edge(a, 'a', b));
        edges.add(new Edge(a, 'b', c));
        edges.add(new Edge(b, 'a', d));
        edges.add(new Edge(b, 'b', e));
        edges.add(new Edge(c, 'b', b));
        edges.add(new Edge(d, 'b', b));
        edges.add(new Edge(e, 'a', d));

        DFA dfa = new DFA(nodes, a, edges);
        // System.out.println(ViewUtil.toDot(dfa));

        assertThat(dfa.accept("a")).isEqualTo("AB akzeptieren");
        assertThat(dfa.accept("aa")).isEqualTo("ABD akzeptieren");
        assertThat(dfa.accept("bbab")).isEqualTo("ACBDB akzeptieren");

        assertThat(dfa.accept("abb")).isEqualTo("ABE ablehnen");
        assertThat(dfa.accept("aabb")).isEqualTo("ABDBE ablehnen");
        assertThat(dfa.accept("bbb")).isEqualTo("ACBE ablehnen");
    }
}
