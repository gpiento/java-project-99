package hexlet.code.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "User with id '%s' not found";

    public UserNotFoundException(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
