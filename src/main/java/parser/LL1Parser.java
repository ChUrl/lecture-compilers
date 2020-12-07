package parser;

import parser.grammar.Grammar;
import parser.grammar.LL1GrammarAnalyzer;
import util.ast.AST;
import util.ast.Node;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static util.tools.Logger.log;

public class LL1Parser {

    private final ILL1ParsingTable parsetable;

    public LL1Parser(ILL1ParsingTable parsetable) {
        this.parsetable = parsetable;
    }

    public static LL1Parser fromGrammar(Path path) throws IOException {
        return LL1Parser.fromGrammar(Grammar.fromFile(path));
    }

    public static LL1Parser fromGrammar(Grammar grammar) {
        LL1GrammarAnalyzer analyzer = new LL1GrammarAnalyzer(grammar);
        return new LL1Parser(analyzer.getTable());
    }

    public boolean parse(List<String> token) {
        Node root = new Node(this.parsetable.getStartSymbol());
        AST tree = new AST(root);
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);

        int inputPosition = 0;

        log("\nParsing:");
        log("Input: " + token + "\n");

        // Parsing
        while (!stack.isEmpty()) {
            final String top = stack.peek().getName();

            final String currentToken;
            if (inputPosition >= token.size()) {
                // Wenn auf dem Stack mehr Nichtterminale liegen als Terminale in der Eingabe vorhanden sind
                // Die Eingabe wurde komplett konsumiert

                currentToken = "$"; // EOF
            } else {
                // Es sind noch Eingabesymbole vorhanden

                currentToken = token.get(inputPosition);
            }

            final String prod = this.parsetable.get(top, currentToken);

            if (top.equals(this.parsetable.getEpsilon())) {
                // Wenn auf dem Stack das Epsilonsymbol liegt

                stack.pop();
            } else if (top.equals(currentToken)) {
                // Wenn auf dem Stack ein Terminal liegt (dieses muss mit der Eingabe übereinstimmen)

                stack.pop();
                inputPosition++;
            } else if (this.parsetable.getTerminals().contains(top)) {
                // Wenn das Terminal auf dem Stack nicht mit der aktuellen Eingabe übereinstimmt

                throw new MyParseException("Invalid terminal on stack: " + top);
            } else if (prod == null) {
                // Wenn es für das aktuelle Terminal und das Nichtterminal auf dem Stack keine Regel gibt

                throw new MyParseException("No prod. for nonterminal " + top + ", terminal " + currentToken);
            } else {
                // Wenn das Nichtterminal auf dem Stack durch (s)eine Produktion ersetzt werden kann
                // Hier wird auch der AST aufgebaut

                log("Used: " + top + " -> " + prod);
                Node pop = stack.pop();

                final String[] split = prod.split(" ");
                for (int i = split.length - 1; i >= 0; i--) {
                    Node node = new Node(split[i]);
                    stack.push(node);
                    pop.addChild(node);
                }
            }
        }

        log("\nParsed AST:\n" + tree);
        log("-".repeat(100) + "\n");

        return true;
    }
}
