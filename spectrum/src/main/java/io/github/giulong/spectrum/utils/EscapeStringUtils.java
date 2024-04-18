package io.github.giulong.spectrum.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
public final class EscapeStringUtils {

    private static final EscapeStringUtils INSTANCE = new EscapeStringUtils();

    public static EscapeStringUtils getInstance() {
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
}
