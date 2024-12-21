package hexlet.code.exception;

public class TaskNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Task with id '%s' not found";

    public TaskNotFoundException(final Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }
}
