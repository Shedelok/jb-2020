package sharepo;

import sharepo.exception.SyntaxErrorException;
import sharepo.exception.TypeErrorException;

public class Main {
    public static void main(final String[] args) {
        try {
            if (args == null) {
                throw new NullPointerException("Given args array is null");
            }
            if (args.length != 1) {
                throw new IllegalArgumentException("Expected 1 argument");
            }
            if (args[0] == null) {
                throw new NullPointerException("Given argument is null");
            }
        } catch (final IllegalArgumentException | NullPointerException e) {
            return;
        }
        try {
            System.out.println(Converter.convert(args[0]));
        } catch (SyntaxErrorException e) {
            System.out.println("SYNTAX ERROR");
        } catch (TypeErrorException e) {
            System.out.println("TYPE ERROR");
        }
    }
}
