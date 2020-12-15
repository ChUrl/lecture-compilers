package parser;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import parser.ast.AST;
import parser.ast.ASTNode;
import parser.grammar.Grammar;
import parser.grammar.GrammarAnalyzer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import static util.Logger.log;

public class StupsParser {

    private final ParsingTable parsetable;

    public StupsParser(ParsingTable parsetable) {
        this.parsetable = parsetable;
    }

    public static StupsParser fromGrammar(Grammar grammar) {
        final GrammarAnalyzer analyzer = new GrammarAnalyzer(grammar);
        return new StupsParser(analyzer.getTable());
    }

    public AST parse(List<? extends Token> token, Vocabulary voc) {
        System.out.println(" - Parsing program...");
        final ASTNode root = new ASTNode(this.parsetable.getStartSymbol(), 0);
        final AST tree = new AST(root);
        final Deque<ASTNode> stack = new ArrayDeque<>();
        stack.push(root);

        int inputPosition = 0;

        log("\nParsing:");
        log("Input: " + token + "\n");

        // Parsing
        while (!stack.isEmpty()) {
            final String top = stack.peek().getName();

            final String currentTokenSym;
            int currentLine = 0;
            if (inputPosition >= token.size()) {
                // Wenn auf dem Stack mehr Nichtterminale liegen als Terminale in der Eingabe vorhanden sind
                // Die Eingabe wurde komplett konsumiert

                currentTokenSym = "$"; // EOF
            } else {
                // Es sind noch Eingabesymbole vorhanden

                currentTokenSym = voc.getSymbolicName(token.get(inputPosition).getType());
                currentLine = token.get(inputPosition).getLine();
            }

            final String prod = this.parsetable.get(top, currentTokenSym);

            if (top.equals(this.parsetable.getEpsilon())) {
                // Wenn auf dem Stack das Epsilonsymbol liegt

                stack.pop();
            } else if (top.equals(currentTokenSym)) {
                // Wenn auf dem Stack ein Terminal liegt (dieses muss mit der Eingabe übereinstimmen)

                stack.pop();
                inputPosition++;
            } else if (this.parsetable.getTerminals().contains(top)) {
                // Wenn das Terminal auf dem Stack nicht mit der aktuellen Eingabe übereinstimmt

                System.out.println("\nLine " + currentLine + " Syntaxerror: Expected " + top + " but found " + currentTokenSym);
                this.printSourceLine(currentLine, token);

                throw new ParseException("Invalid terminal on stack: " + top, tree);
            } else if (prod == null) {
                // Wenn es für das aktuelle Terminal und das Nichtterminal auf dem Stack keine Regel gibt

                System.out.println("\nLine " + currentLine + " Syntaxerror: Didn't expect " + currentTokenSym);
                this.printSourceLine(currentLine, token);

                throw new ParseException("No prod. for nonterminal " + top + ", terminal " + currentTokenSym, tree);
            } else {
                // Wenn das Nichtterminal auf dem Stack durch (s)eine Produktion ersetzt werden kann
                // Hier wird auch der AST aufgebaut

                log("Used: " + top + " -> " + prod);
                final ASTNode pop = stack.pop();

                final String[] split = prod.split(" ");

                for (int i = split.length - 1; i >= 0; i--) {
                    final ASTNode node = new ASTNode(split[i], currentLine);

                    if (inputPosition + i < token.size()) {
                        // Die Schleife geht in der Eingabe weiter
                        final Token currentTok = token.get(inputPosition + i);

                        // Die Token mit semantischem Inhalt auswählen
                        if ("IDENTIFIER".equals(split[i]) || split[i].endsWith("_LIT")) {
                            node.setValue(currentTok.getText());
                        }
                    }

                    stack.push(node);
                    pop.addChild(node);
                }
            }
        }

        log("\nParsed AST:\n" + tree);
        log("-".repeat(100) + "\n");

        System.out.println("Parsing successful.");

        return tree;
    }

    private void printSourceLine(int line, List<? extends Token> token) {
        final Optional<String> srcLine = token.stream()
                                              .filter(tok -> tok.getLine() == line)
                                              .map(Token::getText)
                                              .reduce((s1, s2) -> s1 + " " + s2);

        srcLine.ifPresent(s -> System.out.println("  :: " + s));
    }
}
