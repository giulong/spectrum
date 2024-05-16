package io.github.giulong.spectrum.utils;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public final class StringUtils {

    private static final Map<String, String> CONVERSIONMAP = new HashMap<>();
    private static final StringUtils INSTANCE = new StringUtils();

    public static StringUtils getInstance() {
        return INSTANCE;
    }

    private StringUtils() {
        CONVERSIONMAP.put("class", "className");
        CONVERSIONMAP.put("readonly", "readOnly");
    }

    public String escape(final String stringToEscape) {
        Objects.requireNonNull(stringToEscape, "The string to escape cannot be null");

        return stringToEscape
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    public String convert(String stringToConvert) {
        return CONVERSIONMAP.getOrDefault(stringToConvert, stringToConvert);
    }
}