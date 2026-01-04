package io.github.giulong.spectrum.utils.visual_regression;

import java.awt.image.BufferedImage;

public class OutlineDiff extends ColorDiff {

    @Override
    protected void apply(final BufferedImage diff, final int i, final int j, final int rgb, final int[][] referencePixels, final int[][] regressionPixels) {
        if (j > 0 && referencePixels[j - 1][i] == regressionPixels[j - 1][i]) {
            diff.setRGB(i, j - 1, rgb);
        }

        if (j < referencePixels.length - 1 && referencePixels[j + 1][i] == regressionPixels[j + 1][i]) {
            diff.setRGB(i, j + 1, rgb);
        }

        if (i > 0 && referencePixels[j][i - 1] == regressionPixels[j][i - 1]) {
            diff.setRGB(i - 1, j, rgb);
        }

        if (i < referencePixels[j].length - 1 && referencePixels[j][i + 1] == regressionPixels[j][i + 1]) {
            diff.setRGB(i + 1, j, rgb);
        }
    }
}
