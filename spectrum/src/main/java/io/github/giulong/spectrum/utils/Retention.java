package io.github.giulong.spectrum.utils;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
@Getter
@SuppressWarnings("unused")
public class Retention {

    @JsonPropertyDescription("Number of reports to retain. Older ones will be deleted")
    private int total;

    public int cleanupNeeded(final List<File> files) {
        final int currentCount = files.size();
        final int toKeep = Math.max(0, total - 1);
        final int toDelete = currentCount - toKeep;

        if (toDelete < 1) {
            log.debug("Reports to keep: {}. Reports already present: {}. None will be deleted, 1 is being generated", total, currentCount);
        }

        log.debug("Reports to keep: {}. Reports already present: {}: {} will be kept, {} will be deleted, 1 is being generated", total, currentCount, toKeep, toDelete);

        return toDelete;
    }
}
