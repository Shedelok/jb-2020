package sharepo;

import sharepo.expression.Expression;

class FilterClauseBuilder {
    private final StringBuilder stringBuilder;
    private int leadingOpenBrackets;

    FilterClauseBuilder() {
        stringBuilder = new StringBuilder();
        leadingOpenBrackets = 0;
    }

    void add(final Expression booleanExpression, final Polynomial polynomial) {
        final var s = booleanExpression.toString(polynomial);
        if (stringBuilder.length() == 0) {
            stringBuilder.append(s);
        } else {
            leadingOpenBrackets++;
            stringBuilder.append('&').append(s).append(')');
        }
    }

    @Override
    public String toString() {
        if (stringBuilder.length() == 0) {
            return "(0=0)";
        } else {
            return "(".repeat(leadingOpenBrackets) + stringBuilder.toString();
        }
    }
}
