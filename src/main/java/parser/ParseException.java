package parser;

import parser.ast.SyntaxTree;

import static util.Logger.log;

public class ParseException extends RuntimeException {

    public ParseException(String message, SyntaxTree syntaxTree) {
        super("\n" + message);

        log("\nAST at last state:\n" + syntaxTree);
    }
}
