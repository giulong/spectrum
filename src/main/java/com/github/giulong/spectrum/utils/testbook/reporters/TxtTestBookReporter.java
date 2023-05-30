package com.github.giulong.spectrum.utils.testbook.reporters;

import lombok.Getter;

@Getter
public class TxtTestBookReporter extends FileTestBookReporter {

    @SuppressWarnings("FieldMayBeFinal")
    private String template = "/testbook/template.txt";

    @SuppressWarnings("FieldMayBeFinal")
    private String output = "target/spectrum/testbook/testbook-{timestamp}.txt";
}
