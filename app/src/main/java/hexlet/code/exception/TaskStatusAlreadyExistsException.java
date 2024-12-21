package hexlet.code.exception;

public class TaskStatusAlreadyExistsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Task status with slug '%s' already exists";

    public TaskStatusAlreadyExistsException(String slug) {
        super(String.format(DEFAULT_MESSAGE, slug));
    }
}
