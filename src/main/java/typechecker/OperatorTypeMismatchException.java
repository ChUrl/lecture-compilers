package typechecker;

public class OperatorTypeMismatchException extends RuntimeException {

    public OperatorTypeMismatchException(String message) {
        super(message);
    }
}
