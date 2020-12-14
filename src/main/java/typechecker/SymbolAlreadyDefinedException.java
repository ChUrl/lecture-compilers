package typechecker;

public class SymbolAlreadyDefinedException extends RuntimeException {

    public SymbolAlreadyDefinedException(String message) {
        super("\n" + message);
    }
}
