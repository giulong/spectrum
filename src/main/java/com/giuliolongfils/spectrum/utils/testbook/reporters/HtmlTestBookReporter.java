package com.giuliolongfils.spectrum.utils.testbook.reporters;

import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import com.giuliolongfils.spectrum.pojos.testbook.TestBook;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class HtmlTestBookReporter extends TestBookReporter {

    @SuppressWarnings("FieldMayBeFinal")
    private String template = "/testbook/template.html";

    @SuppressWarnings("FieldMayBeFinal")
    private Path output = Paths.get("target/spectrum/testbook/testbook.html");

    @Override
    @SneakyThrows
    public void updateWith(final TestBook testBook) {
        Files.createDirectories(output.getParent());
        Files.write(output, parse(template, testBook).getBytes());
    }

    @Override
    public String getTestsReplacementFrom(final TestBook testBook) {
        return format(testBook.getTests());
    }

    @Override
    public String getUnmappedTestsReplacementFrom(final TestBook testBook) {
        return format(testBook.getUnmappedTests());
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
                .collect(Collectors.joining());
    }
}
