package com.giuliolongfils.spectrum.utils.testbook;

import com.giuliolongfils.spectrum.pojos.TestBook;
import com.giuliolongfils.spectrum.pojos.TestBookResult;
import com.giuliolongfils.spectrum.utils.FileReader;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class LogTestBookOutput extends TestBookOutput {

    private final String logTemplate = FileReader.getInstance().read("/testbook/log-template.txt");

    @Override
    public void updateWith(final TestBook testBook) {
        final Map<String, TestBookResult> tests = testBook.getTests();
        final Map<String, TestBookResult> unmappedTests = testBook.getUnmappedTests();
        final int longestName = Stream.of(tests.keySet(), unmappedTests.keySet()).flatMap(Set::stream)
                .collect(Collectors.toSet())
                .stream().max(Comparator.comparingInt(String::length))
                .orElse("")
                .length();

        log.info(String.format(logTemplate, format(tests, longestName), format(unmappedTests, longestName)));
    }

    public String format(final Map<String, TestBookResult> tests, final int longestName) {
        return tests
                .entrySet()
                .stream()
                .map(e -> String.format("%" + longestName + "s -> %-8s", e.getKey(), e.getValue().getStatus().getValue()))
                .collect(Collectors.joining("\n\t\t"));
    }
}
