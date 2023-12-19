package io.github.giulong.spectrum.utils.testbook.reporters;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.utils.Retention;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparingLong;

@Slf4j
@Getter
@SuppressWarnings("unused")
public abstract class FileTestBookReporter extends TestBookReporter {

    @SuppressWarnings("FieldMayBeFinal")
    @JsonPropertyDescription("Retention rules configuration")
    private Retention retention = new Retention();

    public abstract String getOutput();

    @Override
    public void cleanupOldReports() {
        final String output = getOutput();
        final String extension = FILE_UTILS.getExtensionOf(output);
        log.info("{} testBooks to keep: {}", extension, retention.getTotal());

        final Path folder = Path.of(FILE_UTILS.interpolateTimestampFrom(output)).getParent();
        final File[] folderContent = folder.toFile().listFiles();

        if (folderContent == null) {
            return;
        }

        final List<File> files = Arrays
                .stream(folderContent)
                .filter(file -> !file.isDirectory())
                .filter(file -> file.getName().endsWith(extension))
                .sorted(comparingLong(File::lastModified))
                .toList();

        retention.deleteOldArtifactsFrom(files);
    }

    @Override
    @SneakyThrows
    public void doOutputFrom(final String interpolatedTemplate) {
        final Path outputPath = Path.of(FILE_UTILS.interpolateTimestampFrom(getOutput()));
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, interpolatedTemplate.getBytes());
    }
}
