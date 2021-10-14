import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MyVeryFirstTest {

    @Test
    public void toUpperCaseConvertsAllLettersToUppercase() {
        // given
        String teststring = "kleinGROSS";

        // when
        String result = teststring.toUpperCase();

        // then
        Assertions.assertEquals("KLEINGROSS", result);
    }
}
