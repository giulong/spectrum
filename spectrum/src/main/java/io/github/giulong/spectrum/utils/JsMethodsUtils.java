package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.enums.LocatorType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openqa.selenium.By;

import java.util.*;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
public final class JsMethodsUtils {

    private static final JsMethodsUtils INSTANCE = new JsMethodsUtils();
    private static final Map<String, String> CONVERSIONMAP = new HashMap<>();
    private static final Map<LocatorType, String> FINDELEMENTMAP = new HashMap<>();

    private static final List<String> SHORTANDPROPERTIES = Arrays.asList(
            "background", "font", "border", "border-top", "margin", "margin-top", "padding",
            "padding-top", "list-style", "outline", "pause", "cue");

    public static JsMethodsUtils getInstance() {
        return INSTANCE;
    }

    static {
        CONVERSIONMAP.put("class", "className");
        CONVERSIONMAP.put("readonly", "readOnly");
        FINDELEMENTMAP.put(LocatorType.ID, "return document.getElementById('%s');");
    }

    public String convertString(String stringToConvert) {
        return CONVERSIONMAP.getOrDefault(stringToConvert, stringToConvert);
    }

    public String getScript(LocatorType locatorType) {
        return FINDELEMENTMAP.get(locatorType);
    }

    public boolean isShorthandProperty(String cssProperty) {
        return SHORTANDPROPERTIES.contains(cssProperty);
    }
}