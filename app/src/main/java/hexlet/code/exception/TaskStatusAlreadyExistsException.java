package hexlet.code.exception;

public class TaskStatusAlreadyExistsException extends RuntimeException {
    public TaskStatusAlreadyExistsException(String message) {
        super(message);
    }

    public TaskStatusAlreadyExistsException(String message, Object... args) {
        super(String.format(message, args));
    }
}
