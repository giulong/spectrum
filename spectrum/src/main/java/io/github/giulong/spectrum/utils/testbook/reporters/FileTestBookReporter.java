package io.github.giulong.spectrum.utils.testbook.reporters;

import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public abstract class FileTestBookReporter extends TestBookReporter {

    public abstract String getOutput();

    @Override
    @SneakyThrows
    public void doOutputFrom(final String interpolatedTemplate) {
        final Path outputPath = Path.of(FILE_UTILS.interpolateTimestampFrom(getOutput()));
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, interpolatedTemplate.getBytes());
    }
}
