package parser;

import util.ast.AST;
import util.ast.Node;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class LL1Parser {

    private final ILL1ParsingTable parsetable;

    public LL1Parser(ILL1ParsingTable parsetable) {
        this.parsetable = parsetable;
    }

    public boolean parse(List<String> token) {
        Node root = new Node(this.parsetable.getStartSymbol());
        AST tree = new AST(root);
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);

        int currentToken = 0;
        System.out.println("\nParsing " + token + ":");

        // Parsing
        while (!stack.isEmpty()) {
            final String top = stack.peek().getName();

            if (currentToken >= token.size()) {
                // Wenn auf dem Stack mehr Nichtterminale liegen als Terminale in der Eingabe vorhanden sind

                throw new MyParseException("Input too long");
            }
            final String prod = this.parsetable.get(top, token.get(currentToken));

            if (top.equals(token.get(currentToken))) {
                // Wenn auf dem Stack ein Terminal liegt

                stack.pop();
                currentToken++;

            } else if (this.parsetable.getTerminals().contains(top)) {
                // Wenn das Terminal auf dem Stack nicht mit der aktuellen Eingabe übereinstimmt

                throw new MyParseException("Invalid terminal on stack: " + top);

            } else if (prod == null) {
                // Wenn es für das aktuelle Terminal und das Nichtterminal auf dem Stack keine Regel gibt

                throw new MyParseException("No prod. for nonterminal " + top + ", terminal " + token.get(currentToken));

            } else {
                // Wenn das Nichtterminal auf dem Stack durch (s)eine Produktion ersetzt werden kann
                // Hier wird auch der AST aufgebaut

                final String[] split = prod.split(" ");

                System.out.println(top + " -> " + prod);
                Node pop = stack.pop();
                for (int i = split.length - 1; i >= 0; i--) {
                    Node node = new Node(split[i]);
                    stack.push(node);
                    pop.addChild(node);
                }
            }
        }

        System.out.println("\n" + tree);
        return true;
    }
}
