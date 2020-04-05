package sharepo.exception;

public class SyntaxErrorException extends Exception {
    public SyntaxErrorException(final String message) {
        super(message);
    }

    public SyntaxErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
