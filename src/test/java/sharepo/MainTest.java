package sharepo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {
    private ByteArrayOutputStream output;

    private static Object[][] mainMethod() {
        final String sample = "filter{(0=1)}%>%map{element}";
        return new Object[][]{
                {null, ""},
                {new String[]{}, ""},
                {new String[]{null}, ""},
                {new String[]{sample, sample}, ""},
                {new String[]{sample}, "filter{(-1=0)}%>%map{element}\n"},
                {new String[]{"hello"}, "SYNTAX ERROR\n"},
                {new String[]{"map{(1=0)}"}, "TYPE ERROR\n"}
        };
    }

    @BeforeEach
    public void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }


    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER)
    @MethodSource
    public void mainMethod(final String[] args, final String expected) {
        Main.main(args);

        assertEquals(expected, output.toString());
    }

    @AfterEach
    void tearDown() {
        System.setOut(null);
    }
}