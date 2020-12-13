package parser;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import parser.ast.AST;
import parser.ast.ASTNode;
import parser.grammar.Grammar;
import parser.grammar.GrammarAnalyzer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static util.Logger.log;

public class Parser {

    private final ParsingTable parsetable;

    public Parser(ParsingTable parsetable) {
        this.parsetable = parsetable;
    }

    public static Parser fromGrammar(Path path) throws IOException {
        return Parser.fromGrammar(Grammar.fromFile(path));
    }

    public static Parser fromGrammar(Grammar grammar) {
        GrammarAnalyzer analyzer = new GrammarAnalyzer(grammar);
        return new Parser(analyzer.getTable());
    }

    public AST parse(List<? extends Token> token, Vocabulary voc) {
        ASTNode root = new ASTNode(this.parsetable.getStartSymbol());
        AST tree = new AST(root);
        Deque<ASTNode> stack = new ArrayDeque<>();
        stack.push(root);

        int inputPosition = 0;

        log("\nParsing:");
        log("Input: " + token + "\n");

        // Parsing
        while (!stack.isEmpty()) {
            final String top = stack.peek().getName();

            final String currentTokenSym;
            if (inputPosition >= token.size()) {
                // Wenn auf dem Stack mehr Nichtterminale liegen als Terminale in der Eingabe vorhanden sind
                // Die Eingabe wurde komplett konsumiert

                currentTokenSym = "$"; // EOF
            } else {
                // Es sind noch Eingabesymbole vorhanden

                currentTokenSym = voc.getSymbolicName(token.get(inputPosition).getType());
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

                System.out.println("Syntaxfehler.");

                throw new ParseException("Invalid terminal on stack: " + top, tree);
            } else if (prod == null) {
                // Wenn es für das aktuelle Terminal und das Nichtterminal auf dem Stack keine Regel gibt

                System.out.println("Syntaxfehler.");

                throw new ParseException("No prod. for nonterminal " + top + ", terminal " + currentTokenSym, tree);
            } else {
                // Wenn das Nichtterminal auf dem Stack durch (s)eine Produktion ersetzt werden kann
                // Hier wird auch der AST aufgebaut

                log("Used: " + top + " -> " + prod);
                ASTNode pop = stack.pop();

                final String[] split = prod.split(" ");

                for (int i = split.length - 1; i >= 0; i--) {
                    ASTNode ASTNode = new ASTNode(split[i]);

                    if (inputPosition + i < token.size()) {
                        // Die Schleife geht in der Eingabe weiter
                        Token currentTok = token.get(inputPosition + i);

                        // Die Token mit semantischem Inhalt auswählen
                        if ("IDENTIFIER".equals(split[i]) || split[i].endsWith("_LIT")) {
                            ASTNode.setValue(currentTok.getText());
                        }
                    }

                    stack.push(ASTNode);
                    pop.addChild(ASTNode);
                }
            }
        }

        log("\nParsed AST:\n" + tree);
        log("-".repeat(100) + "\n");

        return tree;
    }
}
