package hexlet.code.exception;

public class TaskStatusNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Task status with id '%s' not found";

    public TaskStatusNotFoundException(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }
}
