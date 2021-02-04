package parser;

import parser.ast.SyntaxTree;
import util.Logger;

public class ParseException extends RuntimeException {

    public ParseException(String message, SyntaxTree syntaxTree) {
        super("\n" + message);

        Logger.logException("\nAST at last state:\n" + syntaxTree, ParseException.class);
    }
}
