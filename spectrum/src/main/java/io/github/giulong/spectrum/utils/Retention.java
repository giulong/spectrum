package io.github.giulong.spectrum.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.interfaces.reports.CanProduceMetadata;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

import static java.lang.Math.clamp;
import static java.lang.Math.max;
import static java.util.function.Predicate.not;

@Slf4j
@Getter
public class Retention {

    @JsonIgnore
    private final MetadataManager metadataManager = MetadataManager.getInstance();

    @SuppressWarnings("FieldMayBeFinal")
    @JsonPropertyDescription("Number of reports to retain. Older ones will be deleted")
    private int total = Integer.MAX_VALUE;

    @SuppressWarnings("unused")
    @JsonPropertyDescription("Number of successful reports to retain. Older ones will be deleted")
    private int successful;

    public int deleteOldArtifactsFrom(final List<File> files, final CanProduceMetadata metadataProducer) {
        final int currentCount = files.size();
        final int toKeep = clamp(total, 0, currentCount);
        final int toDelete = max(0, currentCount - toKeep);
        final FixedSizeQueue<File> successfulQueue = metadataManager.getSuccessfulQueueOf(metadataProducer);

        log.debug("Reports to keep: total {}, successful {}. Reports already present: {} -> {} will be kept, {} will be deleted",
                total, successful, currentCount, toKeep, toDelete);

        final List<File> successfulFilesToKeep = files
                .stream()
                .filter(file -> successfulQueue.contains(file.getAbsoluteFile()))
                .limit(successful)
                .toList();

        final List<File> deletableFiles = files
                .stream()
                .filter(not(successfulFilesToKeep::contains))
                .toList();

        for (int i = 0; i < toDelete; i++) {
            final File file = deletableFiles.get(i);
            log.trace("File '{}' deleted? {}", file, file.delete());
        }

        return toDelete;
    }
}
