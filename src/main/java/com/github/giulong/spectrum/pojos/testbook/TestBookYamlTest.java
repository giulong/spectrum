package com.github.giulong.spectrum.pojos.testbook;

import lombok.Getter;

@Getter
public class TestBookYamlTest {

    private String name;

    @SuppressWarnings("FieldMayBeFinal")
    private int weight = 1;
}
