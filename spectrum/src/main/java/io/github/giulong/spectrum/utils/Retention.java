package io.github.giulong.spectrum.utils;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Slf4j
@Getter
@SuppressWarnings("unused")
public class Retention {

    @SuppressWarnings("FieldMayBeFinal")
    @JsonPropertyDescription("Number of reports to retain. Older ones will be deleted")
    private int total = Integer.MAX_VALUE;

    public int deleteOldArtifactsFrom(final List<File> files) {
        final int currentCount = files.size();
        final int toKeep = clamp(total, 0, currentCount);
        final int toDelete = max(0, currentCount - toKeep);

        log.debug("Reports to keep: {}. Reports already present: {} -> {} will be kept, {} will be deleted", total, currentCount, toKeep, toDelete);

        for (int i = 0; i < toDelete; i++) {
            final File file = files.get(i);
            log.trace("File '{}' deleted? {}", file, file.delete());
        }

        return toDelete;
    }

    protected int clamp(final int value, final int min, final int max) {
        return max(min, min(max, value));
    }
}
