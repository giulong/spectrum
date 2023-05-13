package com.giuliolongfils.spectrum.utils.testbook.parsers;

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
}
