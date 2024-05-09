package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.enums.LocatorType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
public final class JsMethodsUtils {

    private static final JsMethodsUtils INSTANCE = new JsMethodsUtils();
    private static final Map<String, String> CONVERSIONMAP = new HashMap<>();
    private static final Map<LocatorType, String> LOCATORTOFINDELEMENTSCRIPT = new EnumMap<>(LocatorType.class);
    private static final Map<LocatorType, String> LOCATORTOFINDELEMENTSSCRIPT = new EnumMap<>(LocatorType.class);

    private static final List<String> SHORTANDPROPERTIES = Arrays.asList(
            "background", "font", "border", "border-top", "margin", "margin-top", "padding",
            "padding-top", "list-style", "outline", "pause", "cue");

    public static JsMethodsUtils getInstance() {
        return INSTANCE;
    }

    static {
        CONVERSIONMAP.put("class", "className");
        CONVERSIONMAP.put("readonly", "readOnly");
        initializeFindElementScripts();
        initializeFindElementsScripts();
    }

    private static void initializeFindElementScripts() {
        LOCATORTOFINDELEMENTSCRIPT.put(LocatorType.Id, "return %s.getElementById('%s');");
        LOCATORTOFINDELEMENTSCRIPT.put(LocatorType.className, "return %s.getElementsByClassName('%s')[0];");
        LOCATORTOFINDELEMENTSCRIPT.put(LocatorType.tagName, "return %s.getElementsByTagName('%s')[0];");
        LOCATORTOFINDELEMENTSCRIPT.put(LocatorType.name, "return %s.querySelector('[name=\"%s\"]');");
        LOCATORTOFINDELEMENTSCRIPT.put(LocatorType.cssSelector, "return %s.querySelector('%s');");
        LOCATORTOFINDELEMENTSCRIPT.put(LocatorType.xpath, "return %s.evaluate('%s', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;");
        LOCATORTOFINDELEMENTSCRIPT.put(LocatorType.linkText, "return %s.evaluate('//a[text()=\"%s\"]', " +
                "document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;");
        LOCATORTOFINDELEMENTSCRIPT.put(LocatorType.partialLinkText, "return %s.evaluate('//a[contains(text(), \"%s\")]', " +
                "document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;");
    }

    private static void initializeFindElementsScripts() {
        LOCATORTOFINDELEMENTSSCRIPT.put(LocatorType.Id, "return %s.querySelectorAll('#%s');");
        LOCATORTOFINDELEMENTSSCRIPT.put(LocatorType.className, "return %s.getElementsByClassName('%s');");
        LOCATORTOFINDELEMENTSSCRIPT.put(LocatorType.tagName, "return %s.getElementsByTagName('%s');");
        LOCATORTOFINDELEMENTSSCRIPT.put(LocatorType.name, "return %s.querySelectorAll('[name=\"%s\"]');");
        LOCATORTOFINDELEMENTSSCRIPT.put(LocatorType.cssSelector, "return %s.querySelectorAll('%s');");
        LOCATORTOFINDELEMENTSSCRIPT.put(LocatorType.xpath, "return %s.evaluate('%s', document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);");
        LOCATORTOFINDELEMENTSSCRIPT.put(LocatorType.linkText, "return %s.evaluate('//a[text()=\"%s\"]', " +
                "document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);");
        LOCATORTOFINDELEMENTSSCRIPT.put(LocatorType.partialLinkText, "return %s.evaluate('//a[contains(text(), \"%s\")]', " +
                "document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null)");
    }

    public String convertCssProperty(String stringToConvert) {
        return CONVERSIONMAP.getOrDefault(stringToConvert, stringToConvert);
    }

    public String getFindElementScript(LocatorType locatorType) {
        return LOCATORTOFINDELEMENTSCRIPT.get(locatorType);
    }

    public String getFindElementsScript(LocatorType locatorType) {
        return LOCATORTOFINDELEMENTSSCRIPT.get(locatorType);
    }

    public boolean isShorthandProperty(String cssProperty) {
        return SHORTANDPROPERTIES.contains(cssProperty);
    }
}