package com.giuliolongfils.spectrum.utils.testbook.reporters;

import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Getter
public class LogTestBookReporter extends TestBookReporter {

    @SuppressWarnings("FieldMayBeFinal")
    private String template = "/testbook/template.txt";

    @Override
    public Map<String, String> getSpecificReplacementsFor(final Map<String, TestBookResult> tests, final Map<String, TestBookResult> unmappedTests) {
        final int longestTestName = Stream.of(tests.keySet(), unmappedTests.keySet()).flatMap(Set::stream)
                .collect(toSet())
                .stream().max(comparingInt(String::length))
                .orElse("")
                .length();

        return Map.of(
                "{{tests}}", format(tests, longestTestName),
                "{{unmapped-tests}}", format(unmappedTests, longestTestName)
        );
    }

    @Override
    public void doOutputFrom(final String interpolatedTemplate) {
        log.info(interpolatedTemplate);
    }

    public String format(final Map<String, TestBookResult> tests, final int longestTestName) {
        return tests
                .entrySet()
                .stream()
                .map(e -> String.format("%-" + longestTestName + "s | %-8s", e.getKey(), e.getValue().getStatus().getValue()))
                .collect(joining("\n"));
    }
}
