package sharepo.exception;

public class TypeErrorException extends Exception {
    public TypeErrorException(final String message) {
        super(message);
    }

    public TypeErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
