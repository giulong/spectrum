package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StringUtils")
public class StringUtilsTest {

    @InjectMocks
    private StringUtils stringUtils;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstace() {
        assertSame(StringUtils.getInstance(), StringUtils.getInstance());
    }


    @Test
    @DisplayName("escapeString method should return the input string with escaped characters")
    public void testEscapeString() {
        assertThrows(NullPointerException.class, () -> stringUtils.escapeString(null), "The string to escape cannot be null");
        assertEquals("\\\\", stringUtils.escapeString("\\"), "Escapes backslashes");
        assertEquals("\\'", stringUtils.escapeString("'"), "Escapes single quotes");
        assertEquals("\\\"", stringUtils.escapeString("\""), "Escapes double quotes");
        assertEquals("\\n", stringUtils.escapeString("\n"), "Escapes newlines");
        assertEquals("\\r", stringUtils.escapeString("\r"), "Escapes carriage returns");
    }

    @Test
    @DisplayName("EscapeString should act correctly even with combined characters")
    void testEscapeStringWithCombinedCharacters() {
        assertEquals("\\\\\\'\\\"\\n\\r", stringUtils.escapeString("\\'\"\n\r"));
    }


}
