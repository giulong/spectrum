package io.github.giulong.spectrum.utils.visual_regression;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class OutlineDiffTest {

    private final int[][] referencePixels = new int[3][3];
    private final int[][] regressionPixels = new int[3][3];
    private final int rgb = 123;

    @Mock
    private BufferedImage bufferedImage;

    @InjectMocks
    private OutlineDiff diff;

    @Test
    @DisplayName("apply should set the outline of each modified pixel")
    void apply() {
        final int i = 1;
        final int j = 1;

        diff.apply(bufferedImage, i, j, rgb, referencePixels, regressionPixels);

        verify(bufferedImage).setRGB(0, 1, rgb);
        verify(bufferedImage).setRGB(1, 0, rgb);
        verify(bufferedImage).setRGB(1, 2, rgb);
        verify(bufferedImage).setRGB(2, 1, rgb);

        verifyNoMoreInteractions(bufferedImage);
    }

    @Test
    @DisplayName("apply should not set the outline if pixels are outside left/upper bounds")
    void applyOutsideLeftUpperBounds() {
        final int i = 0;
        final int j = 0;

        diff.apply(bufferedImage, i, j, rgb, referencePixels, regressionPixels);

        verify(bufferedImage).setRGB(0, 1, rgb);
        verify(bufferedImage).setRGB(1, 0, rgb);

        verifyNoMoreInteractions(bufferedImage);
    }

    @Test
    @DisplayName("apply should not set the outline if pixels are outside right/lower bounds")
    void applyOutsideRightLowerBounds() {
        final int i = 2;
        final int j = 2;

        diff.apply(bufferedImage, i, j, rgb, referencePixels, regressionPixels);

        verify(bufferedImage).setRGB(1, 2, rgb);
        verify(bufferedImage).setRGB(2, 1, rgb);

        verifyNoMoreInteractions(bufferedImage);
    }

    @Test
    @DisplayName("apply should not set the outline if adjacent pixels are modified")
    void applyModified() {
        final int i = 1;
        final int j = 1;
        final int[][] modifiedPixels = new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};

        diff.apply(bufferedImage, i, j, rgb, referencePixels, modifiedPixels);

        verifyNoInteractions(bufferedImage);
    }
}
