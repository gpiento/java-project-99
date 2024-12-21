package hexlet.code.exception;

public class TaskStatusNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Task status with id '%s' not found";

    private static final String SLUG_MESSAGE = "Task status with slug '%s' not found";

    public TaskStatusNotFoundException(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }

    public TaskStatusNotFoundException(String slug) {
        super(String.format(SLUG_MESSAGE, slug));
    }
}
