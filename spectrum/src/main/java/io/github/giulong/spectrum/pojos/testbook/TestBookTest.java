package io.github.giulong.spectrum.pojos.testbook;

import static io.github.giulong.spectrum.enums.Result.NOT_RUN;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.giulong.spectrum.enums.Result;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@EqualsAndHashCode(exclude = {"weight", "result"})
public class TestBookTest {

    private String className;
    private String testName;

    @Builder.Default
    private int weight = 1;

    @JsonIgnore
    @Builder.Default
    @Setter
    private Result result = NOT_RUN;
}
