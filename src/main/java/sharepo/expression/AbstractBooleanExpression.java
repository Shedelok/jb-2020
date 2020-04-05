package sharepo.expression;

abstract class AbstractBooleanExpression extends AbstractExpression {
    @Override
    public ExpressionType getType() {
        return ExpressionType.BOOLEAN;
    }
}
