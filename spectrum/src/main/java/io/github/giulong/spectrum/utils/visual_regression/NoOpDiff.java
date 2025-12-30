package io.github.giulong.spectrum.utils.visual_regression;

import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpDiff extends ImageDiff {

    @Override
    public Path buildBetween(final Path reference, final Path regression, final Path destination, final String diffName) {
        log.debug("NoOp image diff");

        return null;
    }
}
