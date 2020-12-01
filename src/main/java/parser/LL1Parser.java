package parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class LL1Parser {

    private final ILL1ParsingTable parsetable;

    public LL1Parser(ILL1ParsingTable parsetable) {
        this.parsetable = parsetable;
    }

    public boolean parse(List<String> token) {
        Deque<String> stack = new ArrayDeque<>();
        stack.push(this.parsetable.getStartSymbol());

        int current = 0;
        System.out.println("\nParsing " + token + ":");

        while (!stack.isEmpty()) {
            String top = stack.peek();

            // Wenn auf dem Stack mehr Nichtterminale liegen als Terminale in der Eingabe vorhanden sind
            if (current >= token.size()) {
                throw new MyParseException("Input too long");
            }
            List<String> prod = this.parsetable.get(top, token.get(current));

            if (top.equals(token.get(current))) {
                stack.pop();
                current++;

            } else if (this.parsetable.getTerminals().contains(top)) {
                throw new MyParseException("Invalid terminal on stack: " + top);

            } else if (prod == null) {
                throw new MyParseException("No prod. for nonterminal " + top + " and terminal " + token.get(current));

            } else {
                System.out.println(top + " -> " + prod);
                stack.pop();
                for (int i = prod.size() - 1; i >= 0; i--) {
                    stack.push(prod.get(i));
                }
            }
        }

        return true;
    }
}
