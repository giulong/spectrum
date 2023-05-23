package com.giuliolongfils.spectrum.pojos.testbook;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode(exclude = "weight")
@ToString
public class TestBookTest {
    private String className;
    private String testName;
    private int weight;
}
