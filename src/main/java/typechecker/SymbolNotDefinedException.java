package typechecker;

public class SymbolNotDefinedException extends RuntimeException {

    public SymbolNotDefinedException(String message) {
        super("\n" + message);
    }
}
