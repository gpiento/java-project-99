package hexlet.code.exception;

public class LabelAlreadyExistsException extends RuntimeException {
    public LabelAlreadyExistsException(String message) {
        super(message);
    }
}
