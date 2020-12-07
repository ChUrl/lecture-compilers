package parser;

import util.ast.AST;

import static util.tools.Logger.log;

public class MyParseException extends RuntimeException {

    public MyParseException(String message) {
        super(message);
    }

    public MyParseException(String message, AST ast) {
        super(message);

        log("\nAST at last state:\n" + ast);
    }
}
