package com.giuliolongfils.spectrum.utils.testbook.reporters;

import com.giuliolongfils.spectrum.pojos.testbook.TestBook;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class TxtTestBookReporter extends LogTestBookReporter {

    @SuppressWarnings("FieldMayBeFinal")
    private Path output = Paths.get("target/spectrum/testbook/testbook.txt");

    @Override
    @SneakyThrows
    public void updateWith(final TestBook testBook) {
        findLongestNameFrom(testBook);
        Files.write(output, parse(template, testBook).getBytes());
    }
}
