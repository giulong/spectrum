package io.github.giulong.spectrum.utils.visual_regression;

import java.awt.image.BufferedImage;

public class HighlightDiff extends ColorDiff {

    @Override
    protected void apply(final BufferedImage diff, final int i, final int j, final int rgb, final int[][] referencePixels, final int[][] regressionPixels) {
        diff.setRGB(i, j, rgb);
    }
}
