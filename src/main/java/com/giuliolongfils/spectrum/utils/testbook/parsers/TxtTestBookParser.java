package com.giuliolongfils.spectrum.utils.testbook.parsers;

import com.giuliolongfils.spectrum.utils.FileReader;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

@Slf4j
public class TxtTestBookParser extends TestBookParser {

    @Override
    public List<String> parse() {
        return Arrays
                .stream(FileReader.getInstance()
                        .read(String.format("/%s", path))
                        .split(lineSeparator()))
                .peek(this::validate)
                .collect(Collectors.toList());
    }

    protected void validate(final String line) {
        if (!line.matches(".+::.+")) {
            throw new RuntimeException(String.format("Line '%s' in TestBook doesn't match pattern ClassName::TestName", line));
        }
    }
}
