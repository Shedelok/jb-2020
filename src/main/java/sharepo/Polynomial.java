package sharepo;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Polynomial {
    private final Map<BigInteger, BigInteger> terms;

    public Polynomial() {
        terms = new HashMap<>(Map.of(BigInteger.ONE, BigInteger.ONE));
    }

    public Polynomial(final BigInteger constant) {
        if (constant.equals(BigInteger.ZERO)) {
            terms = new HashMap<>();
        } else {
            terms = new HashMap<>(Map.of(BigInteger.ZERO, constant));
        }
    }

    public Polynomial(final Polynomial other) {
        terms = new HashMap<>(other.terms);
    }

    private static Polynomial pow(final Polynomial base, final BigInteger exponent) {
        if (exponent.equals(BigInteger.ZERO)) {
            return new Polynomial(BigInteger.ONE);
        } else if (exponent.testBit(0)) {
            return Polynomial.multiply(pow(base, exponent.subtract(BigInteger.ONE)), base);
        } else {
            return pow(Polynomial.multiply(base, base), exponent.shiftRight(1));
        }
    }

    public static Polynomial multiply(final Polynomial p1, final Polynomial p2) {
        final var result = new Polynomial(BigInteger.ZERO);
        p1.terms.forEach((k1, v1) -> {
            p2.terms.forEach((k2, v2) -> {
                result.terms.compute(k1.add(k2), (k, v) -> (v == null) ? v1.multiply(v2) : v.add(v1.multiply(v2)));
            });
        });
        result.removeZeros();
        return result;
    }

    public static Polynomial compose(final Polynomial base, final Polynomial replacement) {
        final var result = new Polynomial(BigInteger.ZERO);
        base.terms.forEach((k, v) -> result.add(multiply(new Polynomial(v), pow(replacement, k))));
        result.removeZeros();
        return result;
    }

    private void removeZeros() {
        terms.values().removeAll(List.of(BigInteger.ZERO));
    }

    public void add(final Polynomial other) {
        other.terms.forEach((k, v) -> terms.compute(k, (key, old) -> (old == null) ? v : old.add(v)));
        removeZeros();
    }

    public void subtract(final Polynomial other) {
        other.terms.forEach((k, v) -> terms.compute(k, (key, old) -> (old == null) ? v.negate() : old.subtract(v)));
        removeZeros();
    }

    @Override
    public String toString() {
        if (terms.isEmpty()) {
            return "0";
        }
        final var entries = terms.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
        final var pow = new StringBuilder("element");
        final var resultBuilder = new StringBuilder("(".repeat(terms.size() - 1));
        var currentExp = BigInteger.ONE;
        boolean first = true;
        for (final var e : entries) {
            final var exp = e.getKey();
            var coeff = e.getValue();

            if (!first) {
                if (coeff.signum() == -1) {
                    resultBuilder.append('-');
                    coeff = coeff.negate();
                } else {
                    resultBuilder.append('+');
                }
            }

            if (exp.equals(BigInteger.ZERO)) {
                resultBuilder.append(coeff);
            } else {
                while (!currentExp.equals(exp)) {
                    pow.append("*element)");
                    currentExp = currentExp.add(BigInteger.ONE);
                }
                final boolean coeffIsOne = coeff.equals(BigInteger.ONE);
                if (!coeffIsOne) {
                    resultBuilder.append('(').append(coeff).append('*');
                }
                for (var i = BigInteger.ONE; !i.equals(exp); i = i.add(BigInteger.ONE)) {
                    resultBuilder.append('(');
                }
                resultBuilder.append(pow);
                if (!coeffIsOne) {
                    resultBuilder.append(')');
                }
            }

            if (!first) {
                resultBuilder.append(')');
            } else {
                first = false;
            }
        }
        return resultBuilder.toString();
    }
}
