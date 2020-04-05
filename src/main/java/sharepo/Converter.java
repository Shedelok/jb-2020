package sharepo;

import sharepo.exception.SyntaxErrorException;
import sharepo.exception.TypeErrorException;
import sharepo.exception.UnexpectedCharacterException;
import sharepo.expression.Expression;
import sharepo.expression.PolynomialExpression;

import java.math.BigInteger;

class Converter {
    private Converter() {
    }

    static String convert(final String input) throws TypeErrorException, SyntaxErrorException {
        return new InternalConverter(input).convert();
    }

    private static class InternalConverter {
        private static final String CALL_JOINER = "%>%";

        private final Scanner scanner;

        InternalConverter(final String input) {
            scanner = new Scanner(input);
        }

        private static int getRealOneBasedIndex(final int index) {
            return index + 1;
        }

        private BigInteger parseInteger() throws SyntaxErrorException {
            final var from = scanner.getPosition();
            if (scanner.test('-')) {
                scanner.nextChar();
            }
            while (scanner.testDigit()) {
                scanner.nextChar();
            }
            try {
                return new BigInteger(scanner.substring(from, scanner.getPosition()));
            } catch (final NumberFormatException e) {
                throw new SyntaxErrorException("Error parsing integer", e);
            }
        }

        private Expression parseExpression() throws SyntaxErrorException, TypeErrorException {
            final Expression result;
            if (scanner.test('e')) {
                scanner.ensure("element");
                result = new PolynomialExpression(new Polynomial());
            } else if (scanner.test('-') || scanner.testDigit()) {
                result = new PolynomialExpression(new Polynomial(parseInteger()));
            } else {
                scanner.ensure('(');
                final var left = parseExpression();
                final var operationCharPosition = scanner.getPosition();
                final var operationChar = scanner.nextChar();
                if (!Expression.isOperationChar(operationChar)) {
                    throw new UnexpectedCharacterException(getRealOneBasedIndex(operationCharPosition));
                }
                final var right = parseExpression();
                scanner.ensure(')');
                try {
                    result = Expression.buildExpression(left, right, operationChar);
                } catch (final IllegalArgumentException e) {
                    throw new TypeErrorException(
                            String.format("The operator at position %d is applied to arguments of incorrect type",
                                    getRealOneBasedIndex(operationCharPosition)),
                            e);
                }
            }
            return result;
        }

        private Expression parseFilterClause() throws TypeErrorException, SyntaxErrorException {
            return parseCallClause("filter", Expression.ExpressionType.BOOLEAN);
        }

        private Expression parseMapClause() throws TypeErrorException, SyntaxErrorException {
            return parseCallClause("map", Expression.ExpressionType.ARITHMETICAL);
        }

        private Expression parseCallClause(final String startsWith,
                                           final Expression.ExpressionType expectedType) throws SyntaxErrorException, TypeErrorException {
            final var startPosition = scanner.getPosition();
            scanner.ensure(startsWith);
            scanner.ensure('{');
            final var result = parseExpression();
            scanner.ensure('}');
            if (result.getType() != expectedType) {
                throw new TypeErrorException("Incorrect type of the expression in call at position " + getRealOneBasedIndex(startPosition));
            }
            return result;
        }

        String convert() throws SyntaxErrorException, TypeErrorException {
            final var filterClauseBuilder = new FilterClauseBuilder();
            var currentPolynomial = new Polynomial();

            boolean firstCall = true;
            do {
                if (!firstCall) {
                    scanner.ensure(CALL_JOINER);
                } else {
                    firstCall = false;
                }

                if (scanner.test('f')) {
                    filterClauseBuilder.add(parseFilterClause(), currentPolynomial);
                } else if (scanner.test('m')) {
                    currentPolynomial = Polynomial.compose(parseMapClause().asPolynomial(), currentPolynomial);
                } else {
                    throw new UnexpectedCharacterException(getRealOneBasedIndex(scanner.getPosition()));
                }
            } while (scanner.hasNextChar());
            return String.format("filter{%s}%smap{%s}", filterClauseBuilder.toString(), CALL_JOINER, currentPolynomial.toString());
        }
    }
}
