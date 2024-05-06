package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JsMethodsUtils")
public class JsMethodsUtilsTest {

    @InjectMocks
    private JsMethodsUtils jsMethodsUtils;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstace() {
        assertSame(JsMethodsUtils.getInstance(), JsMethodsUtils.getInstance());
    }

    @Test
    @DisplayName("convertString should convert the input string into the value combined with the key in the map")
    void testConvertString() {
        assertEquals("className", jsMethodsUtils.convertString("class"));
        assertEquals("readOnly", jsMethodsUtils.convertString("readonly"));
        assertEquals("notInMap", jsMethodsUtils.convertString("notInMap"));
    }

    @Test
    @DisplayName("IsShortandProperty should check if the provided cssProperty is shorthand or longhand")
    void testIsShortandProperty() {
        assertTrue(jsMethodsUtils.isShorthandProperty("background"));
        assertTrue(jsMethodsUtils.isShorthandProperty("font"));
        assertTrue(jsMethodsUtils.isShorthandProperty("border"));
        assertFalse(jsMethodsUtils.isShorthandProperty("background-color"));
        assertFalse(jsMethodsUtils.isShorthandProperty("border-color"));
    }
}
