package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StringUtils")
public class JsStringUtilsTest {

    @InjectMocks
    private JsStringUtils jsStringUtils;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstace() {
        assertSame(JsStringUtils.getInstance(), JsStringUtils.getInstance());
    }


    @Test
    @DisplayName("escapeString method should return the input string with escaped characters")
    public void testEscapeString() {
        assertThrows(NullPointerException.class, () -> jsStringUtils.escapeString(null), "The string to escape cannot be null");
        assertEquals("\\\\", jsStringUtils.escapeString("\\"), "Escapes backslashes");
        assertEquals("\\'", jsStringUtils.escapeString("'"), "Escapes single quotes");
        assertEquals("\\\"", jsStringUtils.escapeString("\""), "Escapes double quotes");
        assertEquals("\\n", jsStringUtils.escapeString("\n"), "Escapes newlines");
        assertEquals("\\r", jsStringUtils.escapeString("\r"), "Escapes carriage returns");
    }

    @Test
    @DisplayName("EscapeString should act correctly even with combined characters")
    void testEscapeStringWithCombinedCharacters() {
        assertEquals("\\\\\\'\\\"\\n\\r", jsStringUtils.escapeString("\\'\"\n\r"));
    }


}
