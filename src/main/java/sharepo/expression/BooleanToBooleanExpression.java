package sharepo.expression;

import sharepo.Polynomial;

class BooleanToBooleanExpression extends AbstractBooleanExpression {
    private final Expression left;
    private final Expression right;
    private final char operationChar;

    BooleanToBooleanExpression(final Expression left, final Expression right, final char operationChar) {
        ensureTypes(left, right);
        this.left = left;
        this.right = right;
        this.operationChar = operationChar;
    }

    @Override
    public ExpressionType getArgumentsType() {
        return ExpressionType.BOOLEAN;
    }

    @Override
    public String toString(final Polynomial replacement) {
        return '(' + left.toString(replacement) + operationChar + right.toString(replacement) + ')';
    }
}
