package parser;

import parser.ast.AST;

import static util.Logger.log;

public class ParseException extends RuntimeException {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, AST ast) {
        super(message);

        log("\nAST at last state:\n" + ast);
    }
}
