package sharepo.expression;

import sharepo.Polynomial;

public interface Expression {
    private static boolean isArithmeticalOperationChar(final char c) {
        return c == '+' || c == '-' || c == '*';
    }

    private static boolean isArithmeticalToBooleanOperationChar(final char c) {
        return c == '>' || c == '<' || c == '=';
    }

    private static boolean isBooleanToBooleanOperationChar(final char c) {
        return c == '&' || c == '|';
    }

    static boolean isOperationChar(final char c) {
        return isArithmeticalOperationChar(c)
                || isArithmeticalToBooleanOperationChar(c)
                || isBooleanToBooleanOperationChar(c);
    }

    static Expression buildExpression(final Expression left, final Expression right, final char operationChar) {
        if (isArithmeticalOperationChar(operationChar)) {
            return new PolynomialExpression(left, right, operationChar);
        } else if (isArithmeticalToBooleanOperationChar(operationChar)) {
            return new ArithmeticalToBooleanExpression(left, right, operationChar);
        } else if (isBooleanToBooleanOperationChar(operationChar)) {
            return new BooleanToBooleanExpression(left, right, operationChar);
        } else {
            // cannot be covered with tests
            throw new IllegalArgumentException();
        }
    }

    default Polynomial asPolynomial() {
        // cannot be covered with tests
        throw new UnsupportedOperationException();
    }

    ExpressionType getType();

    String toString(Polynomial replacement);

    enum ExpressionType {
        ARITHMETICAL,
        BOOLEAN
    }
}
