package com.github.giulong.spectrum.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum QualityGateStatus {

    OK(true),
    KO(false);

    private final boolean value;

    public static QualityGateStatus fromValue(final boolean value) {
        return Arrays
                .stream(values())
                .filter(v -> v.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not a valid QualityGateStatus"));
    }
}
