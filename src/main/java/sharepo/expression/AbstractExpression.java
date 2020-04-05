package sharepo.expression;

abstract class AbstractExpression implements Expression {
    protected abstract ExpressionType getArgumentsType();

    protected void ensureTypes(final Expression left, final Expression right) {
        if (left.getType() != getArgumentsType() || right.getType() != getArgumentsType()) {
            throw new IllegalArgumentException();
        }
    }
}
