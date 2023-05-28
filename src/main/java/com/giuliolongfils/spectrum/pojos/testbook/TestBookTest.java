package com.giuliolongfils.spectrum.pojos.testbook;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(exclude = "weight")
public class TestBookTest {

    private String className;
    private String testName;

    @Builder.Default
    private int weight = 1;
}
