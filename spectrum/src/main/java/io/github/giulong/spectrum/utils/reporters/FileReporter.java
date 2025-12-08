package io.github.giulong.spectrum.utils.reporters;

import static java.util.function.Predicate.not;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.github.giulong.spectrum.interfaces.reports.CanProduceMetadata;
import io.github.giulong.spectrum.interfaces.reports.CanReportSummary;
import io.github.giulong.spectrum.interfaces.reports.CanReportTestBook;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FixedSizeQueue;
import io.github.giulong.spectrum.utils.MetadataManager;
import io.github.giulong.spectrum.utils.Retention;

import lombok.Generated;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class FileReporter extends Reporter implements CanProduceMetadata {

    @JsonIgnore
    private final MetadataManager metadataManager = MetadataManager.getInstance();

    @JsonIgnore
    private final FileUtils fileUtils = FileUtils.getInstance();

    @JsonPropertyDescription("Path to the template to be used, relative to src/test/resources")
    @SuppressWarnings("unused")
    private String template;

    @JsonPropertyDescription("Where to produce the output, relative to the root of the project")
    @SuppressWarnings("unused")
    private String output;

    @JsonPropertyDescription("Retention rules configuration")
    private final Retention retention = new Retention();

    @JsonPropertyDescription("Set to true if you want the report to be automatically opened when the suite execution is finished")
    @SuppressWarnings("unused")
    private boolean openAtEnd;

    @Override
    public void cleanupOldReports() {
        final String extension = fileUtils.getExtensionOf(output);
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
                .filter(not(File::isDirectory)
                        .and(file -> file.getName().endsWith(extension)))
                .toList();

        retention.deleteArtifactsFrom(files, this);
    }

    @Override
    @SneakyThrows
    public void doOutputFrom(final String interpolatedTemplate) {
        fileUtils.write(output, interpolatedTemplate);
    }

    @Override
    public void produceMetadata() {
        final File file = Path.of(output).toAbsolutePath().toFile();
        final int maxSize = retention.getSuccessful();
        final FixedSizeQueue<File> queue = metadataManager.getSuccessfulQueueOf(this);

        log.debug("Adding metadata '{}'. Current size: {}, max capacity: {}", file, queue.size(), maxSize);
        queue
                .shrinkTo(maxSize - 1)
                .add(file);

        metadataManager.setSuccessfulQueueOf(this, queue);
    }

    @SneakyThrows
    @Override
    public void open() {
        if (openAtEnd) {
            Desktop.getDesktop().open(Path.of(output).toFile());
        }
    }

    @Generated
    public static class TxtTestBookReporter extends FileReporter implements CanReportTestBook {
    }

    @Generated
    public static class HtmlTestBookReporter extends FileReporter implements CanReportTestBook {
    }

    @Generated
    public static class TxtSummaryReporter extends FileReporter implements CanReportSummary {
    }

    @Generated
    public static class HtmlSummaryReporter extends FileReporter implements CanReportSummary {
    }
}
