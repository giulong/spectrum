package io.github.giulong.spectrum.utils;

import com.aventstack.extentreports.ExtentReports;
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
public class ExtentReportsWrapper {

    private ExtentReports extentReports;
    private Configuration.Extent extent;

    @SneakyThrows
    public void cleanup() {
        final int retention = extent.getRetention();
        final File[] folderContent = Objects.requireNonNull(Path.of(extent.getReportFolder()).toFile().listFiles());
        final List<File> files = Arrays
                .stream(folderContent)
                .filter(file -> !file.isDirectory())
                .sorted(comparingLong(File::lastModified))
                .toList();

        final int total = files.size();
        final int toKeep = Math.max(0, retention - 1);
        final int toDelete = total - toKeep;

        if (toDelete < 1) {
            log.debug("Reports to keep: {}. Reports already present: {}. None will be deleted, 1 is being generated", retention, total);
            return;
        }

        log.debug("Reports to keep: {}. Reports already present: {}: {} will be kept, {} will be deleted, 1 is being generated", retention, total, toKeep, toDelete);

        final List<String> foldersToDelete = files
                .subList(0, toDelete)
                .stream()
                .peek(file -> log.trace("File '{}' deleted? {}", file, file.delete()))
                .map(File::getName)
                .map(FileUtils.getInstance()::removeExtensionFrom)
                .toList();

        Arrays
                .stream(folderContent)
                .filter(File::isDirectory)
                .filter(directory -> foldersToDelete.contains(directory.getName()))
                .peek(directory -> log.trace("Deleting orphan directory {}", directory.getName()))
                .map(File::toPath)
                .forEach(FileUtils.getInstance()::deleteDirectory);
    }
}
