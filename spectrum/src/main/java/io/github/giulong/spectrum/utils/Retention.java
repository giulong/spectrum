package io.github.giulong.spectrum.utils;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.clamp;
import static java.lang.Math.max;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Comparator.comparing;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.partitioningBy;

import java.io.File;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.github.giulong.spectrum.interfaces.reports.CanProduceMetadata;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Retention {

    @JsonIgnore
    private final MetadataManager metadataManager = MetadataManager.getInstance();

    @JsonIgnore
    private final FileUtils fileUtils = FileUtils.getInstance();

    @SuppressWarnings("FieldMayBeFinal")
    @JsonPropertyDescription("Number of reports to retain. Older ones will be deleted")
    private int total = MAX_VALUE;

    @SuppressWarnings("unused")
    @JsonPropertyDescription("Number of successful reports to retain. Older ones will be deleted")
    private int successful;

    @SuppressWarnings("FieldMayBeFinal")
    @JsonPropertyDescription("Number of days after which reports will be deleted")
    private int days = MAX_VALUE;

    public void deleteArtifactsFrom(final List<File> files, final CanProduceMetadata metadataProducer) {
        final FixedSizeQueue<File> successfulQueue = metadataManager.getSuccessfulQueueOf(metadataProducer);
        final int successfulLimit = max(0, successful);

        log.debug("Finding at most {} successful files among {}", successfulLimit, files.size());
        final List<File> successfulFilesToKeep = files
                .stream()
                .filter(file -> successfulQueue.contains(file.getAbsoluteFile()))
                .limit(successfulLimit)
                .toList();

        final int successfulToKeep = successfulFilesToKeep.size();
        final int remainingTotalToKeep = max(0, total - successfulToKeep);
        final Map<Boolean, List<File>> partitionedFiles = files
                .stream()
                .filter(not(successfulFilesToKeep::contains))
                .sorted(comparing(fileUtils::getCreationTimeOf))
                .collect(partitioningBy(this::isOld));

        final List<File> youngFiles = partitionedFiles.get(false);

        log.debug("Deleting young artifacts. Successful to keep: {}, Among {}, at most {} will be kept", successfulToKeep, youngFiles.size(), remainingTotalToKeep);
        deleteFrom(youngFiles, remainingTotalToKeep);

        log.debug("Deleting all remaining old artifacts");
        deleteFrom(partitionedFiles.get(true), 0);
    }

    void deleteFrom(final List<File> files, final int maxToKeep) {
        final int size = files.size();
        final int toKeep = clamp(maxToKeep, 0, size);
        final int toDelete = size - toKeep;

        log.debug("Files are {}. {} will be kept, {} will be deleted", size, toKeep, toDelete);
        files.stream().limit(toDelete).forEach(fileUtils::delete);
    }

    boolean isOld(final File file) {
        final FileTime creationTime = fileUtils.getCreationTimeOf(file);
        final LocalDateTime dateTime = LocalDateTime.ofInstant(creationTime.toInstant(), systemDefault());
        final LocalDateTime today = LocalDateTime.now();
        final long difference = DAYS.between(dateTime, today);
        final boolean old = difference >= days;

        log.debug("Report '{}' with date {} was generated {} days ago. Retention days are {}. Is it old? {}", file, dateTime, difference, days, old);
        return old;
    }
}
