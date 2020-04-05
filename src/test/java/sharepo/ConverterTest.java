package sharepo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import sharepo.exception.SyntaxErrorException;
import sharepo.exception.TypeErrorException;
import sharepo.exception.UnexpectedCharacterException;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConverterTest {
    private String run(final String input) throws TypeErrorException, SyntaxErrorException {
        return Converter.convert(input);
    }

    private void test(final String input,
                      final String expectedFilterClause,
                      final String expectedMapClause) throws Exception {
        final var expected = "filter{" + expectedFilterClause + "}%>%map{" + expectedMapClause + "}";
        final var actual = run(input);

        assertEquals(expected, actual);
        assertEquals(actual, run(actual));
    }

    private <E extends Exception> void assertThrowsRelevantException(final String input,
                                                                     final Class<E> exceptionType,
                                                                     final Consumer<E> assertRelevant) {
        final var exception = assertThrows(exceptionType, () -> run(input));

        assertEquals(exception.getClass(), exceptionType);
        assertRelevant.accept(exception);
    }

    private <E extends Exception> void assertThrowsExceptionWithPositionInMessage(String inputPrefix,
                                                                                  final char charAtExpectedPosition,
                                                                                  String inputSuffix,
                                                                                  final Class<E> exceptionType,
                                                                                  final Consumer<E> assertRelevant) {
        if (inputPrefix == null) {
            inputPrefix = "";
        }
        if (inputSuffix == null) {
            inputSuffix = "";
        }
        final var messageSubstring = "at position " + (inputPrefix.length() + 1);
        final var input = inputPrefix + charAtExpectedPosition + inputSuffix;

        assertThrowsRelevantException(input, exceptionType, e -> {
            assertTrue(e.getMessage().contains(messageSubstring));
            assertRelevant.accept(e);
        });
    }

    private void assertThrowsExceptionWithPositionInMessage(final String inputPrefix,
                                                            final char charAtExpectedPosition,
                                                            final String inputSuffix,
                                                            final Class<? extends Exception> exceptionType) {
        assertThrowsExceptionWithPositionInMessage(inputPrefix, charAtExpectedPosition, inputSuffix, exceptionType, e -> {
        });
    }

    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER)
    @CsvSource({
            ",a,ilter{(0=1)}",
            ",a,ap{element}",
            "map{element}%>%,a,ilter{(element>0)}",
            "map{element}%>%map{element}%>%,%,>%map{element}",
            "map{(1,/,1)}",
            "map{(element,/,1)}",
            "map{(element,t,)}",
            "map{(element,),}",
            "filter{(0,!,=1)}",
            "filter{((element>1),^,(element<1))}",
    })
    public void unexpectedCharacter_throwsUnexpectedCharacterException(final String prefix,
                                                                       final char unexpectedChar,
                                                                       final String suffix) {
        assertThrowsExceptionWithPositionInMessage(prefix, unexpectedChar, suffix, UnexpectedCharacterException.class);
    }

    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER)
    @CsvSource(value = {
            "fil,l,er{(0=0)}",
            "m,m,p{element}",
            "filter,(,0=0)",
            "map,(,element)",
            "map{123,%,>%map{0}",
            "map{1,.,1}",
            "map{1,e,8}",
            "map{,+,1}",
            "map{1,+,-1}"
    })
    public void notExpectedCharacter_throwsSyntaxErrorException(final String prefix,
                                                                final char notExpectedChar,
                                                                final String suffix) {
        assertThrowsExceptionWithPositionInMessage(prefix, notExpectedChar, suffix, SyntaxErrorException.class,
                e -> assertTrue(e.getMessage().contains("but found " + notExpectedChar)));
    }

    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER)
    @ValueSource(strings = {
            "",
            "map{123",
            "filter{(0=0)}%>%map{(element+2)}%>%",
            "filter{(0=0)}%>%map"
    })
    public void unexpectedEnd_throwsSyntaxErrorException(final String input) {
        assertThrowsRelevantException(input, SyntaxErrorException.class,
                e -> assertEquals("Unexpected end of the input string", e.getMessage()));
    }

    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER)
    @ValueSource(strings = {
            "map{-}",
            "map{--}",
            "map{---}",
            "map{--1}",
            "map{---1}",
            "map{-element}",
            "map{(-element)}",
    })
    public void incorrectIntegerFormat_throwsSyntaxErrorException(final String input) {
        assertThrowsRelevantException(input, SyntaxErrorException.class, e -> {
            assertEquals("Error parsing integer", e.getMessage());
            assertEquals(NumberFormatException.class, e.getCause().getClass());
        });
    }

    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER)
    @CsvSource({
            ",f,ilter{element}",
            ",m,ap{(0=0)}",
            "filter{((0=0),=,(0=0))}",
            "filter{((0=0),>,(0=1))}",
            "filter{((0=0),<,(0=1))}",
            "map{((0=0),+,(0=0))}",
            "map{((0=0),-,(0=0))}",
            ",m,ap{((0=0)&(0=0))}",
            ",m,ap{((0=0)|(0=0))}"
    })
    public void typeError_throwsTypeErrorException(final String prefix,
                                                   final char charAtExpectedPosition,
                                                   final String suffix) {
        assertThrowsExceptionWithPositionInMessage(prefix, charAtExpectedPosition, suffix, TypeErrorException.class);
    }

    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER)
    @CsvSource({
            "filter{(element>10)}%>%filter{(element<20)}," +
                    "(((-10+element)>0)&((20-element)>0))," +
                    "element",
            "map{(element+10)}%>%filter{(element>10)}%>%map{(element*element)}," +
                    "(element>0)," +
                    "((100+(20*element))+(element*element))",
            "filter{(element>0)}%>%filter{(element<0)}%>%map{(element*element)}," +
                    "((element>0)&((-1*element)>0))," +
                    "(element*element)"
    })
    public void samples(final String input,
                        final String expectedFilterClause,
                        final String expectedMapClause) throws Exception {
        test(input, expectedFilterClause, expectedMapClause);
    }

    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER)
    @CsvSource({
            "map{element}," +
                    "(0=0)," +
                    "element",
            "map{11}," +
                    "(0=0)," +
                    "11",
            "filter{(123=321)}," +
                    "(-198=0)," +
                    "element",
            "map{0001}," +
                    "(0=0)," +
                    "1",
            "map{(1+-1)}," +
                    "(0=0)," +
                    "0",
            "map{(1*-1)}," +
                    "(0=0)," +
                    "-1",
            "filter{(((element+1)+element)=0)}," +
                    "((1+(2*element))=0)," +
                    "element",
            "map{(1-element)}," +
                    "(0=0)," +
                    "(1-element)",
            "map{(-1*element)}," +
                    "(0=0)," +
                    "(-1*element)",
            "map{((element-1)-(element*element))}," +
                    "(0=0)," +
                    "((-1+element)-(element*element))",
            "filter{(1234567890123456789012345678901234567890=1234567890123456789012345678901234567890)}," +
                    "(0=0)," +
                    "element",
            "map{(element*element)}%>%filter{((element+1)>(element*element))}," +
                    "(((1+(element*element))-(((element*element)*element)*element))>0)," +
                    "(element*element)",
            "map{(element+2)}%>%filter{((((element*element)*(element+1))+(element+20))=0)}," +
                    "((((34+(17*element))+(7*(element*element)))+((element*element)*element))=0)," +
                    "(2+element)",
            "map{(((2+((element+5)*(element*element)))*0)*2)}," +
                    "(0=0)," +
                    "0",
            "filter{((((element+5)+(2*element))=0)&((element+element)>25))}," +
                    "(((5+(3*element))=0)&((-25+(2*element))>0))," +
                    "element",
            "filter{((((element+5)+(2*element))=0)|((element+element)>25))}," +
                    "(((5+(3*element))=0)|((-25+(2*element))>0))," +
                    "element",
            "filter{(((4*(element*element))+(2*element))=((4*(element*element))+(2*element)))}," +
                    "(0=0)," +
                    "element",
            "filter{((((element+(1+element))>0)|((element+(2+element))<-2))&(((element+(3+element))<100)&((element+(4+element))>-0100)))}," +
                    "((((1+(2*element))>0)|((-4-(2*element))>0))&(((97-(2*element))>0)&((104+(2*element))>0)))," +
                    "element",
            "filter{((((element+(1+element))<100)&((element+(2+element))>-100))|(((element+(3+element))>0)|((element+(4+element))<-2)))}," +
                    "((((99-(2*element))>0)&((102+(2*element))>0))|(((3+(2*element))>0)|((-6-(2*element))>0)))," +
                    "element",
            "filter{((((element+(element+-1))<100)&((element+(element+-2))>-100))|(((element+(element+-3))>0)|((element+(element+-4))<-2)))}," +
                    "((((101-(2*element))>0)&((98+(2*element))>0))|(((-3+(2*element))>0)|((2-(2*element))>0)))," +
                    "element",
            "map{(((element*element)*element)*element)}%>%map{(((element*element)*element)*element)}," +
                    "(0=0)," +
                    "(((((((((((((((element*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)",
            "map{(((element*element)*element)*element)}%>%map{(((element*element)*element)*element)}%>%map{0}," +
                    "(0=0)," +
                    "0",
            "map{(((element*element)*element)*element)}%>%map{(((element*element)*element)*element)}%>%filter{(element>0)}%>%map{0}," +
                    "((((((((((((((((element*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)*element)>0)," +
                    "0",
            "map{1}%>%filter{(element>1)}%>%map{((element*element)+element)}," +
                    "(0>0)," +
                    "2",
            "filter{((((((3*(element*(element*(element*element))))-(4*element))-5)>(((2+(element*(1+1)))+((element*element)*element))+((element*element)*element)))" +
                    "|((((3*(element*(element*(element*element))))-(4*element))-5)<(((2+(element*(1+1)))+((element*element)*element))+((element*element)*element))))" +
                    "|((((3*(element*(element*(element*element))))-(4*element))-5)=(((2+(element*(1+1)))+((element*element)*element))+((element*element)*element))))}," +

                    "((((((-7-(6*element))-(2*((element*element)*element)))+(3*(((element*element)*element)*element)))>0)" +
                    "|((((7+(6*element))+(2*((element*element)*element)))-(3*(((element*element)*element)*element)))>0))" +
                    "|((((-7-(6*element))-(2*((element*element)*element)))+(3*(((element*element)*element)*element)))=0))," +

                    "element"
    })
    public void correctInput_returnsEquivalentCode(final String input,
                                                   final String expectedFilterClause,
                                                   final String expectedMapClause) throws Exception {
        test(input, expectedFilterClause, expectedMapClause);
    }
}
