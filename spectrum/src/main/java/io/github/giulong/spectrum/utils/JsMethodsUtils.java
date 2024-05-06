package io.github.giulong.spectrum.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
public final class JsMethodsUtils {

    private static final JsMethodsUtils INSTANCE = new JsMethodsUtils();
    private static final Map<String, String> CONVERSIONMAP = new HashMap<>();
    private static final List<String> SHORTANDPROPERTIES = Arrays.asList(
            "background", "font", "border", "border-top", "margin", "margin-top", "padding",
            "padding-top", "list-style", "outline", "pause", "cue");

    public static JsMethodsUtils getInstance() {
        return INSTANCE;
    }

    static {
        CONVERSIONMAP.put("class", "className");
        CONVERSIONMAP.put("readonly", "readOnly");
    }

    public String convertString(String stringToConvert) {
        return CONVERSIONMAP.getOrDefault(stringToConvert, stringToConvert);
    }

    public boolean isShorthandProperty(String cssProperty) {
        return SHORTANDPROPERTIES.contains(cssProperty);
    }
}