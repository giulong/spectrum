package io.github.giulong.spectrum.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Frame {

    AUTO("auto"),
    AUTO_BEFORE("autoBefore"),
    AUTO_AFTER("autoAfter"),
    MANUAL("manual");

    private final String value;

    public static Frame from(final String value) {
        return Arrays
                .stream(values())
                .filter(f -> f.value.equals(value))
                .findFirst()
                .orElseThrow();
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
