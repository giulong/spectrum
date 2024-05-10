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
    @DisplayName("getFindElementScript should return the correct js script by the provided locator for findElement method")
    void testGetFindElementScript() {
        assertEquals("return %s.getElementById('%s');", jsMethodsUtils.getFindElementScript(LocatorType.Id));
        assertEquals("return %s.querySelector('%s');", jsMethodsUtils.getFindElementScript(LocatorType.cssSelector));
        assertEquals("return %s.evaluate('%s', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;", jsMethodsUtils.getFindElementScript(LocatorType.xpath));
        assertEquals("return %s.evaluate('//a[contains(text(), \"%s\")]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;", jsMethodsUtils.getFindElementScript(LocatorType.partialLinkText));
        assertEquals("return %s.getElementsByTagName('%s')[0];", jsMethodsUtils.getFindElementScript(LocatorType.tagName));
        assertEquals("return %s.getElementsByClassName('%s')[0];", jsMethodsUtils.getFindElementScript(LocatorType.className));
        assertEquals("return %s.evaluate('//a[text()=\"%s\"]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;", jsMethodsUtils.getFindElementScript(LocatorType.linkText));
        assertNotEquals("return %s.evaluate('//a[text()=\"%s\"]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;", jsMethodsUtils.getFindElementScript(LocatorType.name));
    }

    @Test
    @DisplayName("getFindElementsScript should return the correct js script by the provided locator for findElements method")
    void testGetFindElementsScript() {
        assertEquals("return %s.querySelectorAll('#%s');", jsMethodsUtils.getFindElementsScript(LocatorType.Id));
        assertEquals("return %s.getElementsByClassName('%s');", jsMethodsUtils.getFindElementsScript(LocatorType.className));
        assertEquals("return %s.getElementsByTagName('%s');", jsMethodsUtils.getFindElementsScript(LocatorType.tagName));
        assertNotEquals("jreturn %s.querySelectorAll('[name=\\\"%s\\\"]');", jsMethodsUtils.getFindElementsScript(LocatorType.name));
        assertEquals("return %s.querySelectorAll('%s');", jsMethodsUtils.getFindElementsScript(LocatorType.cssSelector));
        assertEquals("var webElements = %s.evaluate('%s', document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);return Array.from({length: webElements.snapshotLength}, (_, i) => webElements.snapshotItem(i));", jsMethodsUtils.getFindElementsScript(LocatorType.xpath));
        assertEquals("return Array.from(%s.querySelectorAll('a')).filter(link => link.textContent === '%s');", jsMethodsUtils.getFindElementsScript(LocatorType.linkText));
        assertEquals("return Array.from(%s.querySelectorAll('a')).filter(link => link.textContent.includes('%s'));", jsMethodsUtils.getFindElementsScript(LocatorType.partialLinkText));
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
