package sharepo.expression;

import sharepo.Polynomial;

class ArithmeticalToBooleanExpression extends AbstractBooleanExpression {
    private final Polynomial polynomial;
    private final char operationChar;

    ArithmeticalToBooleanExpression(Expression left, Expression right, char operationChar) {
        ensureTypes(left, right);
        if (operationChar == '<') {
            final var t = left;
            left = right;
            right = t;
            operationChar = '>';
        }
        polynomial = new Polynomial(left.asPolynomial());
        polynomial.subtract(right.asPolynomial());
        this.operationChar = operationChar;
    }

    @Override
    public ExpressionType getArgumentsType() {
        return ExpressionType.ARITHMETICAL;
    }

    @Override
    public String toString(final Polynomial replacement) {
        return '(' + Polynomial.compose(polynomial, replacement).toString() + operationChar + "0)";
    }
}
