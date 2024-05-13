package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.enums.LocatorType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
public final class JsMethodsUtils {

    private static final Map<String, String> CONVERSIONMAP = new HashMap<>();
    private static final Map<LocatorType, String> LOCATOR_TO_FIND_ELEMENT_SCRIPT = new EnumMap<>(LocatorType.class);
    private static final Map<LocatorType, String> LOCATOR_TO_FIND_ELEMENTS_SCRIPT = new EnumMap<>(LocatorType.class);
    private static final List<String> SHORTHAND_PROPERTIES = Arrays.asList(
            "background", "font", "border", "border-top", "margin", "margin-top", "padding",
            "padding-top", "list-style", "outline", "pause", "cue");
    private static final JsMethodsUtils INSTANCE = new JsMethodsUtils();

    public static JsMethodsUtils getInstance() {
        return INSTANCE;
    }

    private JsMethodsUtils() {
        CONVERSIONMAP.put("class", "className");
        CONVERSIONMAP.put("readonly", "readOnly");
        initializeFindElementScripts();
        initializeFindElementsScripts();
    }

    private static void initializeFindElementScripts() {
        LOCATOR_TO_FIND_ELEMENT_SCRIPT.put(LocatorType.ID, "return %s.getElementById('%s');");
        LOCATOR_TO_FIND_ELEMENT_SCRIPT.put(LocatorType.CLASS_NAME, "return %s.getElementsByClassName('%s')[0];");
        LOCATOR_TO_FIND_ELEMENT_SCRIPT.put(LocatorType.TAG_NAME, "return %s.getElementsByTagName('%s')[0];");
        LOCATOR_TO_FIND_ELEMENT_SCRIPT.put(LocatorType.NAME, "return %s.querySelector('[name=\"%s\"]');");
        LOCATOR_TO_FIND_ELEMENT_SCRIPT.put(LocatorType.CSS_SELECTOR, "return %s.querySelector('%s');");
        LOCATOR_TO_FIND_ELEMENT_SCRIPT.put(LocatorType.XPATH, "return %s.evaluate('%s', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;");
        LOCATOR_TO_FIND_ELEMENT_SCRIPT.put(LocatorType.LINK_TEXT, "return %s.evaluate('//a[text()=\"%s\"]', " +
                "document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;");
        LOCATOR_TO_FIND_ELEMENT_SCRIPT.put(LocatorType.PARTIAL_LINK_TEXT, "return %s.evaluate('//a[contains(text(), \"%s\")]', " +
                "document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;");
    }

    private static void initializeFindElementsScripts() {
        LOCATOR_TO_FIND_ELEMENTS_SCRIPT.put(LocatorType.ID, "return %s.querySelectorAll('#%s');");
        LOCATOR_TO_FIND_ELEMENTS_SCRIPT.put(LocatorType.CLASS_NAME, "return %s.getElementsByClassName('%s');");
        LOCATOR_TO_FIND_ELEMENTS_SCRIPT.put(LocatorType.TAG_NAME, "return %s.getElementsByTagName('%s');");
        LOCATOR_TO_FIND_ELEMENTS_SCRIPT.put(LocatorType.NAME, "return %s.querySelectorAll('[name=\"%s\"]');");
        LOCATOR_TO_FIND_ELEMENTS_SCRIPT.put(LocatorType.CSS_SELECTOR, "return %s.querySelectorAll('%s');");
        LOCATOR_TO_FIND_ELEMENTS_SCRIPT.put(LocatorType.XPATH, "var webElements = %s.evaluate('%s', document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);" +
                "return Array.from({length: webElements.snapshotLength}, (_, i) => webElements.snapshotItem(i));");
        LOCATOR_TO_FIND_ELEMENTS_SCRIPT.put(LocatorType.LINK_TEXT, "return Array.from(%s.querySelectorAll('a')).filter(link => link.textContent === '%s');");
        LOCATOR_TO_FIND_ELEMENTS_SCRIPT.put(LocatorType.PARTIAL_LINK_TEXT, "return Array.from(%s.querySelectorAll('a')).filter(link => link.textContent.includes('%s'));");
    }

    public String convertCssProperty(String stringToConvert) {
        return CONVERSIONMAP.getOrDefault(stringToConvert, stringToConvert);
    }

    public String getFindElementScript(LocatorType locatorType) {
        return LOCATOR_TO_FIND_ELEMENT_SCRIPT.get(locatorType);
    }

    public String getFindElementsScript(LocatorType locatorType) {
        return LOCATOR_TO_FIND_ELEMENTS_SCRIPT.get(locatorType);
    }

    public boolean isShorthandProperty(String cssProperty) {
        return SHORTHAND_PROPERTIES.contains(cssProperty);
    }
}