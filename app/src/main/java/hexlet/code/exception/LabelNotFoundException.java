package hexlet.code.exception;

public class LabelNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Label with id '%s' not found";

    public LabelNotFoundException(final Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }
}
