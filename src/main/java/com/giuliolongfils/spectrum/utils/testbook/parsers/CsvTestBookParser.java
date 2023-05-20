package com.giuliolongfils.spectrum.utils.testbook.parsers;

import com.giuliolongfils.spectrum.utils.FileReader;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.giuliolongfils.spectrum.extensions.watchers.TestBookWatcher.SEPARATOR;
import static java.lang.System.lineSeparator;

@Slf4j
public class CsvTestBookParser extends TestBookParser {

    @Override
    public List<String> parse() {
        log.debug("Reading lines of csv testbook");

        return Arrays.stream(FileReader.getInstance()
                        .read(String.format("/%s", path))
                        .split(lineSeparator()))
                .peek(this::validate)
                .map(line -> line.replace(",", SEPARATOR))
                .collect(Collectors.toList());
    }

    @Override
    protected void validate(String line) {
        log.trace("Validating line {}", line);

        if (line.split(",").length != 2) {
            throw new RuntimeException(String.format("Wrong number of columns in line '%s' in TestBook. Need two as in: ClassName,TestName", line));
        }
    }
}
