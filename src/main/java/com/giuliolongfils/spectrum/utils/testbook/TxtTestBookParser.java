package com.giuliolongfils.spectrum.utils.testbook;

import com.giuliolongfils.spectrum.pojos.TestBookResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class TxtTestBookParser extends TestBookParser {

    @Override
    @SneakyThrows
    public List<String> parse() {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            return lines.toList();
        }
    }

    @Override
    public void setStatus(String className, String testName, TestBookResult.Status status) {
        final String fullName = String.format("%s.%s", className, testName);
        if (testBook.getTests().containsKey(fullName)) {
            testBook.getTests().get(fullName).setStatus(status);
        } else {
            testBook.getUnmappedTests().put(fullName, new TestBookResult().withStatus(status));
        }
    }
}
