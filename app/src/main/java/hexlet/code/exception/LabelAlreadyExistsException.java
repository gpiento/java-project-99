package hexlet.code.exception;

public class LabelAlreadyExistsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Label with name '%s' already exists";

    public LabelAlreadyExistsException(final String name) {
        super(String.format(DEFAULT_MESSAGE, name));
    }
}
