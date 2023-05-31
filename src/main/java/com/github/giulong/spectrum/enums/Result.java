package com.github.giulong.spectrum.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TestBookResult {

    NOT_RUN("Not Run"),
    SUCCESSFUL("Successful"),
    FAILED("Failed"),
    ABORTED("Aborted"),
    DISABLED("Disabled");

    private final String value;
}
