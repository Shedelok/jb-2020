package sharepo.expression;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExpressionTest {
    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER)
    @ValueSource(chars = {'+', '-', '*', '>', '<', '=', '&', '|'})
    public void isOperationCharReturnsTrueForOperationCharacters(final char c) {
        assertTrue(Expression.isOperationChar(c));
    }
}