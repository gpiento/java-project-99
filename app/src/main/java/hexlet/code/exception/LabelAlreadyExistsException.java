package hexlet.code.exception;

public class LabelAlreadyExistsException extends RuntimeException {
    public LabelAlreadyExistsException(String message) {
        super(message);
    }

    public LabelAlreadyExistsException(String message, Object... args) {
        super(String.format(message, args));
    }
}
