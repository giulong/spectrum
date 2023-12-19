package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.pojos.Configuration;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Comparator.comparingLong;

@Slf4j
@Builder
public class ExtentReporter {

    private final FileUtils fileUtils = FileUtils.getInstance();

    private Configuration.Extent extent;

    @SneakyThrows
    public void cleanupOldReports() {
        final Retention retention = extent.getRetention();
        log.info("Extent reports to keep: {}", retention.getTotal());

        final File[] folderContent = Objects
                .requireNonNull(Path
                        .of(extent.getReportFolder())
                        .toFile()
                        .listFiles());

        final List<File> files = Arrays
                .stream(folderContent)
                .filter(file -> !file.isDirectory())
                .sorted(comparingLong(File::lastModified))
                .toList();

        final List<File> directories = Arrays
                .stream(folderContent)
                .filter(File::isDirectory)
                .toList();

        final int toDelete = retention.deleteOldArtifactsFrom(files);

        for (int i = 0; i < toDelete; i++) {
            final String directoryName = fileUtils.removeExtensionFrom(files.get(i).getName());

            directories
                    .stream()
                    .filter(directory -> directory.getName().equals(directoryName))
                    .findFirst()
                    .map(File::toPath)
                    .ifPresent(fileUtils::deleteDirectory);
        }
    }
}
