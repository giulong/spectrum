package com.giuliolongfils.spectrum.pojos.testbook;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class TestBookResult {

    @AllArgsConstructor
    @Getter
    public enum Status {
        NOT_RUN("Not Run"),
        SUCCESSFUL("Successful"),
        FAILED("Failed"),
        ABORTED("Aborted"),
        DISABLED("Disabled");

        private final String value;
    }

    private Status status;
}
