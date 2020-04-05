package sharepo.expression;

import sharepo.Polynomial;

public class PolynomialExpression extends AbstractExpression {
    private final Polynomial polynomial;

    PolynomialExpression(final Expression left, final Expression right, final char operationChar) {
        ensureTypes(left, right);
        final var lp = left.asPolynomial();
        final var rp = right.asPolynomial();
        if (operationChar == '*') {
            polynomial = Polynomial.multiply(lp, rp);
        } else {
            polynomial = new Polynomial(lp);
            if (operationChar == '+') {
                polynomial.add(rp);
            } else if (operationChar == '-') {
                polynomial.subtract(rp);
            } else {
                // cannot be covered with tests
                throw new IllegalArgumentException("operation=" + operationChar);
            }
        }
    }

    public PolynomialExpression(final Polynomial polynomial) {
        this.polynomial = polynomial;
    }

    @Override
    protected ExpressionType getArgumentsType() {
        return ExpressionType.ARITHMETICAL;
    }

    @Override
    public Polynomial asPolynomial() {
        return polynomial;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.ARITHMETICAL;
    }

    @Override
    public String toString(final Polynomial replacement) {
        // cannot be covered with tests
        return Polynomial.compose(polynomial, replacement).toString();
    }
}
