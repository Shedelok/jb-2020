package sharepo.exception;

public class UnexpectedCharacterException extends SyntaxErrorException {
    public UnexpectedCharacterException(final int position) {
        super("Unexpected character at position " + position);
    }
}
