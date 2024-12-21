package hexlet.code.exception;

public class TaskAlreadyExistsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Task with name '%s' already exists";

    public TaskAlreadyExistsException(String name) {
        super(String.format(DEFAULT_MESSAGE, name));
    }
}
