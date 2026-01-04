package io.github.giulong.spectrum.utils.visual_regression;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class HighlightDiffTest {

    @Mock
    private BufferedImage bufferedImage;

    @InjectMocks
    private HighlightDiff diff;

    @Test
    @DisplayName("apply should set the rgb of the provided image to the provided coordinates and rgb")
    void apply() {
        final int i = 1;
        final int j = 2;
        final int rgb = 123;

        diff.apply(bufferedImage, i, j, rgb, null, null);

        verify(bufferedImage).setRGB(i, j, rgb);

        verifyNoMoreInteractions(bufferedImage);
    }
}
