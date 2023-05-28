package com.giuliolongfils.spectrum.utils.testbook.reporters;

import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public class HtmlTestBookReporter extends TestBookReporter {

    @SuppressWarnings("FieldMayBeFinal")
    private String template = "/testbook/template.html";

    @SuppressWarnings("FieldMayBeFinal")
    private Path output = Paths.get("target/spectrum/testbook/testbook.html");

    @Override
    @SneakyThrows
    public void doOutputFrom(final String interpolatedTemplate) {
        Files.createDirectories(output.getParent());
        Files.write(output, interpolatedTemplate.getBytes());
    }
}
