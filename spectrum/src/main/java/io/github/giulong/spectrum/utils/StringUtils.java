package io.github.giulong.spectrum.utils;

import lombok.Getter;

import java.util.Objects;

@Getter
public final class StringUtils {

    private static final StringUtils INSTANCE = new StringUtils();

    public static StringUtils getInstance() {
        return INSTANCE;
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
}
