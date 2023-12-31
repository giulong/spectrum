package io.github.giulong.spectrum.utils.reporters;

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
public class FileReporter extends Reporter {

    @JsonPropertyDescription("Path to the template to be used, relative to src/test/resources")
    private String template;

    @JsonPropertyDescription("Where to produce the output, relative to the root of the project")
    private String output;

    @JsonPropertyDescription("Retention rules configuration")
    private final Retention retention = new Retention();

    @Override
    public void cleanupOldReports() {
        final String extension = FILE_UTILS.getExtensionOf(output);
        log.info("{} testBooks to keep: {}", extension, retention.getTotal());

        final File[] folderContent = Path
                .of(output)
                .getParent()
                .toFile()
                .listFiles();

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
        final Path outputPath = Path.of(output);
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, interpolatedTemplate.getBytes());
    }
}
