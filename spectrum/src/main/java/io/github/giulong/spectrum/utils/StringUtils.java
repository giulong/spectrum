package io.github.giulong.spectrum.utils;

import static lombok.AccessLevel.PRIVATE;

import java.util.Objects;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
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
