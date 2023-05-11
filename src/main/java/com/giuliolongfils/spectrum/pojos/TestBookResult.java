package com.giuliolongfils.spectrum.pojos;

import lombok.*;

import static com.giuliolongfils.spectrum.pojos.TestBookResult.Status.NOT_RUN;

@Getter
@Setter
@EqualsAndHashCode
@With
@NoArgsConstructor
@AllArgsConstructor
public class TestBookResult {

    @AllArgsConstructor
    @Getter
    public enum Status {
        NOT_RUN("Not Run"),
        PASSED("Passed"),
        FAILED("Failed"),
        SKIPPED("Skipped");

        private final String value;
    }

    private Status status = NOT_RUN;
}
