package typechecker;

public class OperatorUsageException extends RuntimeException {

    public OperatorUsageException(String message) {
        super("\n" + message);
    }
}
