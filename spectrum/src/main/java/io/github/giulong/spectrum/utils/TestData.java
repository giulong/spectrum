package io.github.giulong.spectrum.utils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.giulong.spectrum.exceptions.TestFailedException;
import io.github.giulong.spectrum.exceptions.VisualRegressionException;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.jcodec.api.awt.AWTSequenceEncoder;

@Slf4j
@Getter
@Builder
public class TestData {

    private String className;
    private String methodName;
    private String classDisplayName;
    private String testId;
    private Path videoPath;
    private int screenshotNumber;
    private VisualRegression visualRegression;

    @Builder.Default
    private Supplier<TestFailedException> testFailedException = () -> {
        log.debug("Test successful");
        return null;
    };

    @Builder.Default
    private Map<Path, AWTSequenceEncoder> encoders = new HashMap<>();

    @Setter
    private int frameNumber;

    @Setter
    private boolean dynamic;

    @Setter
    private byte[] lastFrameDigest;

    @Setter
    private String lastFrameDisplayName;

    @Setter
    private Path dynamicVideoPath;

    @Setter
    private String displayName;

    public void incrementScreenshotNumber() {
        screenshotNumber++;
    }

    public void incrementFrameNumber() {
        frameNumber++;
    }

    public void registerFailedVisualRegression() {
        if (visualRegression.count++ == 0) {
            testFailedException = () -> {
                throw new VisualRegressionException(String.format("There were %d visual regressions", visualRegression.count));
            };
        }
    }

    @Getter
    @Builder
    public static class VisualRegression {
        private Path path;
        private int count;
    }
}
