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
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(StringUtils.getInstance(), StringUtils.getInstance());
    }

    @Test
    @DisplayName("escapeString method should return the input string with escaped characters")
    public void testEscape() {
        assertThrows(NullPointerException.class, () -> stringUtils.escape(null), "The string to escape cannot be null");
        assertEquals("\\\\", stringUtils.escape("\\"), "Escapes backslashes");
        assertEquals("\\'", stringUtils.escape("'"), "Escapes single quotes");
        assertEquals("\\\"", stringUtils.escape("\""), "Escapes double quotes");
        assertEquals("\\n", stringUtils.escape("\n"), "Escapes newlines");
        assertEquals("\\r", stringUtils.escape("\r"), "Escapes carriage returns");
    }

    @Test
    @DisplayName("EscapeString should act correctly even with combined characters")
    void testEscapeWithCombinedCharacters() {
        assertEquals("\\\\\\'\\\"\\n\\r", stringUtils.escape("\\'\"\n\r"));
    }

    @Test
    @DisplayName("convertString should convert the input string into the value combined with the key in the map")
    void testConvertCssProperty() {
        assertEquals("className", stringUtils.convertCssProperty("class"));
        assertEquals("readOnly", stringUtils.convertCssProperty("readonly"));
        assertEquals("notInMap", stringUtils.convertCssProperty("notInMap"));
    }
}