package com.github.giulong.spectrum.pojos.testbook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.giulong.spectrum.enums.Result;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.github.giulong.spectrum.enums.Result.NOT_RUN;

@Getter
@Builder
public class Test {

    private String className;
    private String testName;

    @Builder.Default
    private int weight = 1;

    @JsonIgnore
    @Builder.Default
    @Setter
    private Result result = NOT_RUN;
}
