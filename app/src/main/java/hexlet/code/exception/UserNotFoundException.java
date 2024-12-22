package hexlet.code.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "User with id '%s' not found";

    private static final String EMAIL_MESSAGE = "User with email '%s' not found";

    public UserNotFoundException(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }

    public UserNotFoundException(String email) {
        super(String.format(EMAIL_MESSAGE, email));
    }
}
