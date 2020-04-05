package sharepo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PolynomialTest {
    private static Object[][] toStringMethod() {
        final Supplier<Polynomial> zero = () -> new Polynomial(BigInteger.ZERO);
        final Supplier<Polynomial> one = () -> new Polynomial(BigInteger.ONE);
        final Supplier<Polynomial> seven = () -> new Polynomial(new BigInteger("7"));
        final Supplier<Polynomial> twenty = () -> new Polynomial(new BigInteger("20"));
        final Supplier<Polynomial> element = Polynomial::new;
        final Supplier<Polynomial> square = () -> Polynomial.multiply(element.get(), element.get());
        final BiFunction<Polynomial, Polynomial, Polynomial> sum = (p1, p2) -> {
            p1.add(p2);
            return p1;
        };
        final BiFunction<Polynomial, Polynomial, Polynomial> difference = (p1, p2) -> {
            p1.subtract(p2);
            return p1;
        };
        final BiFunction<Polynomial, Polynomial, Polynomial> product = Polynomial::multiply;
        final BiFunction<Polynomial, Polynomial, Polynomial> composition = Polynomial::compose;

        return new Object[][]{
                {zero.get(), "0"},
                {one.get(), "1"},
                {seven.get(), "7"},
                {twenty.get(), "20"},
                {sum.apply(zero.get(), zero.get()), "0"},
                {difference.apply(one.get(), zero.get()), "1"},
                {difference.apply(zero.get(), one.get()), "-1"},
                {sum.apply(seven.get(), twenty.get()), "27"},
                {difference.apply(product.apply(sum.apply(seven.get(), twenty.get()), twenty.get()), zero.get()), "540"},
                {product.apply(product.apply(seven.get(), seven.get()), zero.get()), "0"},
                {composition.apply(seven.get(), twenty.get()), "7"},
                {element.get(), "element"},
                {sum.apply(element.get(), seven.get()), "(7+element)"},
                {difference.apply(element.get(), twenty.get()), "(-20+element)"},
                {product.apply(element.get(), one.get()), "element"},
                {product.apply(element.get(), seven.get()), "(7*element)"},
                {composition.apply(product.apply(seven.get(), element.get()), one.get()), "7"},
                {composition.apply(product.apply(seven.get(), element.get()), sum.apply(one.get(), element.get())),
                        "(7+(7*element))"},
                {square.get(), "(element*element)"},
                {product.apply(element.get(), square.get()), "((element*element)*element)"},
                {product.apply(square.get(), element.get()), "((element*element)*element)"},
                {product.apply(square.get(), square.get()), "(((element*element)*element)*element)"},
                {sum.apply(product.apply(square.get(), square.get()), seven.get()),
                        "(7+(((element*element)*element)*element))"},
                {difference.apply(product.apply(square.get(), square.get()), seven.get()),
                        "(-7+(((element*element)*element)*element))"},
                {product.apply(product.apply(square.get(), square.get()), seven.get()),
                        "(7*(((element*element)*element)*element))"},
                {composition.apply(square.get(), element.get()), "(element*element)"},
                {composition.apply(square.get(), square.get()), "(((element*element)*element)*element)"},
                {composition.apply(
                        sum.apply(sum.apply(
                                product.apply(square.get(), square.get()),
                                product.apply(seven.get(), element.get())),
                                twenty.get()),
                        sum.apply(element.get(), seven.get())),
                        "((((2470+(1379*element))+(294*(element*element)))+(28*((element*element)*element)))+" +
                                "(((element*element)*element)*element))"},
        };
    }

    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER)
    @MethodSource
    void toStringMethod(final Polynomial polynomial, final String expected) {
        assertEquals(expected, polynomial.toString());
    }
}