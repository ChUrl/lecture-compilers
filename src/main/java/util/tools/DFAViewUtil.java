package util.tools;

import util.dfa.DFA;
import util.dfa.IEdge;
import util.dfa.INode;

import java.util.HashMap;
import java.util.Map;

public final class DFAViewUtil {

    static int i;

    private DFAViewUtil() {}

    public static String toDot(DFA automaton) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("digraph G {\n");

        Map<INode, Integer> ids = new HashMap<>();

        for (INode node : automaton.getNodes()) {
            ids.put(node, i++);
        }


        for (INode node : automaton.getNodes()) {
            sb.append("node");
            sb.append(ids.get(node));
            sb.append(" [label=\"");
            sb.append(node.getName());
            sb.append("\"");
            if (node.isFinal()) {
                sb.append(" shape=doublecircle");
            }
            sb.append("]\n");
            if (automaton.getStart() == node) {
                sb.append("start [style=invisible]\n");
                sb.append("start -> node");
                sb.append(ids.get(node));
                sb.append("\n");
            }
            for (IEdge edge : node.getEdges()) {
                sb.append("node");
                sb.append(ids.get(node));
                sb.append("->");
                sb.append("node");
                sb.append(ids.get(edge.getEnd()));
                sb.append(" [label=\" ");
                if (edge.getChar() == '\0') {
                    sb.append("&epsilon;");
                } else {
                    sb.append(edge.getChar());
                }
                sb.append("\"]\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }
}
