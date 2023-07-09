package io.github.giulong.spectrum.utils.testbook.reporters;

import lombok.Getter;

@Getter
public class HtmlTestBookReporter extends FileTestBookReporter {

    @SuppressWarnings("FieldMayBeFinal")
    private String template = "/testbook/template.html";

    @SuppressWarnings("FieldMayBeFinal")
    private String output = "target/spectrum/testbook/testbook-{timestamp}.html";
}
