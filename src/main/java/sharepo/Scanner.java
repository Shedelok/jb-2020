package sharepo;

import sharepo.exception.SyntaxErrorException;

class Scanner {
    private final String input;
    private int pos;

    Scanner(final String input) {
        this.input = input;
        pos = 0;
    }

    void ensure(final String expected) throws SyntaxErrorException {
        for (final var c : expected.toCharArray()) {
            ensure(c);
        }
    }

    void ensure(final char expected) throws SyntaxErrorException {
        final var actual = nextChar();
        if (actual != expected) {
            throw new SyntaxErrorException(String.format("Expected %c at position %d, but found %c", expected, pos, actual));
        }
    }

    char getChar() throws SyntaxErrorException {
        if (!hasNextChar()) {
            throw new SyntaxErrorException("Unexpected end of the input string");
        }
        return input.charAt(pos);
    }

    int getPosition() {
        return pos;
    }

    boolean hasNextChar() {
        return pos < input.length();
    }

    char nextChar() throws SyntaxErrorException {
        final var result = getChar();
        pos++;
        return result;
    }

    boolean test(final char expected) throws SyntaxErrorException {
        return getChar() == expected;
    }

    boolean testDigit() throws SyntaxErrorException {
        return Character.isDigit(getChar());
    }

    String substring(final int fromInclusive, final int toExclusive) {
        return input.substring(fromInclusive, toExclusive);
    }
}
