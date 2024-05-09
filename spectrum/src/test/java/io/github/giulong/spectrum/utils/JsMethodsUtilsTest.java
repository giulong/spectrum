package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.enums.LocatorType;
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
    void testConvertCssProperty() {
        assertEquals("className", jsMethodsUtils.convertCssProperty("class"));
        assertEquals("readOnly", jsMethodsUtils.convertCssProperty("readonly"));
        assertEquals("notInMap", jsMethodsUtils.convertCssProperty("notInMap"));
    }

    @Test
    @DisplayName("getScript should return the correct js script by the provided locator")
    void testGetFindElementScript() {
        assertEquals("return %s.getElementsByTagName('%s')[0];", jsMethodsUtils.getFindElementScript(LocatorType.tagName));
        assertEquals("return %s.getElementsByClassName('%s')[0];", jsMethodsUtils.getFindElementScript(LocatorType.className));
        assertEquals("return %s.evaluate('//a[text()=\"%s\"]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;", jsMethodsUtils.getFindElementScript(LocatorType.linkText));
        assertNotEquals("return %s.evaluate('//a[text()=\"%s\"]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;", jsMethodsUtils.getFindElementScript(LocatorType.name));
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
