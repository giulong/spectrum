package io.github.giulong.spectrum.utils.visual_regression;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.github.giulong.spectrum.interfaces.JsonSchemaTypes;
import io.github.giulong.spectrum.utils.FileUtils;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class HighlightDiff extends ImageDiff {

    private final FileUtils fileUtils = FileUtils.getInstance();

    @SuppressWarnings("unused")
    @JsonSchemaTypes(String.class)
    @JsonPropertyDescription("RGB color used to highlight changed pixels. Must be prefixed by a #, such as in '#ff0000'")
    private Color color;

    @SneakyThrows
    public Path buildBetween(final Path reference, final Path regression, final Path destination, final String name) {
        log.debug("Building diff between {} and {}", reference, regression);

        final BufferedImage referenceImage = ImageIO.read(reference.toFile());
        final BufferedImage regressionImage = ImageIO.read(regression.toFile());
        final int referenceWidth = referenceImage.getWidth();
        final int referenceHeight = referenceImage.getHeight();
        final int regressionWidth = regressionImage.getWidth();
        final int regressionHeight = regressionImage.getHeight();

        if (referenceWidth != regressionWidth || referenceHeight != regressionHeight) {
            log.warn("Snapshot reference is {}x{}, while current screenshot is {}x{}. They have different sizes. Cannot compare them.",
                    referenceWidth, referenceHeight, regressionWidth, regressionHeight);
            return null;
        }

        final int[][] referencePixels = getPixelMatrixOf(referenceImage, referenceWidth, referenceHeight);
        final int[][] regressionPixels = getPixelMatrixOf(regressionImage, regressionWidth, regressionHeight);
        final int rgb = color.getRGB();

        for (int i = 0; i < referencePixels.length; i++) {
            for (int j = 0; j < referencePixels[i].length; j++) {
                if (referencePixels[i][j] != regressionPixels[i][j]) {
                    referenceImage.setRGB(j, i, rgb);
                }
            }
        }

        final Path fullDestination = destination.resolve(name);
        final File destinationFile = fullDestination.toFile();
        log.debug("Writing diff between {} and {} at {}", reference, regression, destinationFile);
        ImageIO.write(referenceImage, fileUtils.getExtensionOf(name), destinationFile);

        return fullDestination;
    }

    int[][] getPixelMatrixOf(final BufferedImage image, final int width, final int height) {
        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int[][] result = new int[height][width];

        if (image.getAlphaRaster() != null) {
            for (int i = 0, j = 0, k = 0; k < pixels.length;) {
                result[i][j] = pixels[k++] & 0xff; // blue
                result[i][j] += (pixels[k++] & 0xff) << 8; // green
                result[i][j] += (pixels[k++] & 0xff) << 16; // red
                result[i][j] += (pixels[k++] & 0xff) << 24; // alpha

                if (++j == width) {
                    j = 0;
                    i++;
                }
            }
        } else {
            for (int i = 0, j = 0, k = 0; k < pixels.length;) {
                result[i][j] = pixels[k++] & 0xff; // blue
                result[i][j] += (pixels[k++] & 0xff) << 8; // green
                result[i][j] += (pixels[k++] & 0xff) << 16; // red
                result[i][j] -= 16777216; // 255 alpha

                if (++j == width) {
                    j = 0;
                    i++;
                }
            }
        }

        return result;
    }
}
