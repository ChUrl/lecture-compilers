package typechecker;

public class AssignmentTypeMismatchException extends RuntimeException {

    public AssignmentTypeMismatchException(String message) {
        super("\n" + message);
    }
}
