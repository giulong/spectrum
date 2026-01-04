package io.github.giulong.spectrum.utils.visual_regression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;

import java.nio.file.Path;

import io.github.giulong.spectrum.utils.visual_regression.ImageDiff.Result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class NoOpDiffTest {

    @Mock
    private Path reference;

    @Mock
    private Path regression;

    @Mock
    private Path destination;

    @InjectMocks
    private NoOpDiff diff;

    @Test
    @DisplayName("buildBetween should do nothing and confirm the regression")
    void buildBetween() {
        assertEquals(Result.builder().shown(false).build(), diff.buildBetween(reference, regression, destination, "whatever"));

        verifyNoInteractions(reference);
        verifyNoInteractions(regression);
        verifyNoInteractions(destination);
    }
}
