package io.github.giulong.spectrum.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
public final class StringUtils {

    private static final StringUtils INSTANCE = new StringUtils();
    private static final Map<String, String> CONVERSIONMAP = new HashMap<>();

    public static StringUtils getInstance() {
        return INSTANCE;
    }

    public String escapeString(String stringToEscape) {
        Objects.requireNonNull(stringToEscape, "The string to escape cannot be null");

        return stringToEscape
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    static {
        CONVERSIONMAP.put("class", "className");
        CONVERSIONMAP.put("readonly", "readOnly");
    }

    public String convertString(String stringToConvert) {
        return CONVERSIONMAP.getOrDefault(stringToConvert, stringToConvert);
    }
}
