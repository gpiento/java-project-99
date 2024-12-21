package hexlet.code.exception;

public class UserAlreadyExistsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "User with email '%s' already exits";

    public UserAlreadyExistsException(String email) {
        super(String.format(DEFAULT_MESSAGE, email));
    }
}
