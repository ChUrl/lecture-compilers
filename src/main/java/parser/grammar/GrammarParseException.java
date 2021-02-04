package parser.grammar;

import util.Logger;

public class GrammarParseException extends RuntimeException {

    public GrammarParseException(String message) {
        super(message);

        Logger.logException(message, GrammarParseException.class);
    }
}
