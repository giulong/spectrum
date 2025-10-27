package io.github.giulong.spectrum.pojos.testbook;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Getter;

@SuppressWarnings("unused")
@Getter
public class QualityGate {

    @JsonPropertyDescription("Condition to be evaluated. If true, the execution is successful")
    private String condition;
}
