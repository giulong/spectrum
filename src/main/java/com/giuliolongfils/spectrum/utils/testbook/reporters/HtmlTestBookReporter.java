package com.giuliolongfils.spectrum.utils.testbook.reporters;

import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Getter
public class HtmlTestBookReporter extends TestBookReporter {

    @SuppressWarnings("FieldMayBeFinal")
    private String template = "/testbook/template.html";

    @SuppressWarnings("FieldMayBeFinal")
    private Path output = Paths.get("target/spectrum/testbook/testbook.html");

    @Override
    public Map<String, String> getSpecificReplacementsFor(final Map<String, TestBookResult> tests, final Map<String, TestBookResult> unmappedTests) {
        return Map.of(
                "{{tests}}", format(tests),
                "{{unmapped-tests}}", format(unmappedTests)
        );
    }

    @Override
    @SneakyThrows
    public void doOutputFrom(final String interpolatedTemplate) {
        Files.createDirectories(output.getParent());
        Files.write(output, interpolatedTemplate.getBytes());
    }

    public String format(final Map<String, TestBookResult> tests) {
        return tests
                .entrySet()
                .stream()
                .map(e -> {
                    final String statusValue = e.getValue().getStatus().getValue();
                    final String statusClass = statusValue.toLowerCase().replace(" ", "-");
                    return String.format("<div class=\"test-row %s\"><div class=\"inline test-name\">%s</div>" +
                            "<div class=\"inline status\">%s</div></div>", statusClass, e.getKey(), statusValue);
                })
                .sorted()
                .collect(joining());
    }
}
