package com.giuliolongfils.spectrum.utils.testbook.reporters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import com.giuliolongfils.spectrum.pojos.testbook.TestBook;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class LogTestBookReporter extends TestBookReporter {

    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    private String template = "/testbook/template.txt";

    @JsonIgnore
    private int longestName;

    @Override
    public void updateWith(final TestBook testBook) {
        longestName = Stream.of(testBook.getTests().keySet(), testBook.getUnmappedTests().keySet()).flatMap(Set::stream)
                .collect(Collectors.toSet())
                .stream().max(Comparator.comparingInt(String::length))
                .orElse("")
                .length();

        log.info(parse(template, testBook));
    }

    @Override
    public String getTestsReplacementFrom(TestBook testBook) {
        return format(testBook.getTests());
    }

    @Override
    public String getUnmappedTestsReplacementFrom(TestBook testBook) {
        return format(testBook.getUnmappedTests());
    }

    public String format(final Map<String, TestBookResult> tests) {
        return tests
                .entrySet()
                .stream()
                .map(e -> String.format("%" + longestName + "s -> %-8s", e.getKey(), e.getValue().getStatus().getValue()))
                .collect(Collectors.joining("\n\t\t"));
    }
}
