package io.github.giulong.spectrum.utils.visual_regression;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.github.giulong.spectrum.interfaces.JsonSchemaTypes;
import io.github.giulong.spectrum.utils.FileUtils;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class ColorDiff extends ImageDiff {

    @JsonIgnore
    private final FileUtils fileUtils = FileUtils.getInstance();

    @SuppressWarnings("unused")
    @JsonSchemaTypes(String.class)
    @JsonPropertyDescription("RGB color used to highlight changed pixels. Must be prefixed by a #, such as in '#ff0000'")
    private Color color;

    @SuppressWarnings("unused")
    @JsonPropertyDescription("Number of pixels under which differences are ignored")
    private int threshold;

    protected abstract void apply(BufferedImage diff, int i, int j, int rgb, int[][] referencePixels, int[][] regressionPixels);

    @Override
    @SneakyThrows
    public Result buildBetween(final Path reference, final Path regression, final Path destination, final String name) {
        log.debug("Building diff between {} and {}", reference, regression);

        final BufferedImage referenceImage = ImageIO.read(reference.toFile());
        final BufferedImage regressionImage = ImageIO.read(regression.toFile());
        final int width = referenceImage.getWidth();
        final int height = referenceImage.getHeight();
        final int regressionWidth = regressionImage.getWidth();
        final int regressionHeight = regressionImage.getHeight();

        if (width != regressionWidth || height != regressionHeight) {
            log.warn("Snapshot reference is {}x{}, while current screenshot is {}x{}. They have different sizes. Cannot compare them.",
                    width, height, regressionWidth, regressionHeight);
            return Result.builder().build();
        }

        final int[][] referencePixels = getPixelMatrixOf(referenceImage, width, height);
        final int[][] regressionPixels = getPixelMatrixOf(regressionImage, width, height);
        final int rgb = color.getRGB();
        int count = 0;

        for (int i = 0; i < referencePixels.length; i++) {
            for (int j = 0; j < referencePixels[i].length; j++) {
                if (referencePixels[i][j] != regressionPixels[i][j]) {
                    apply(referenceImage, j, i, rgb, referencePixels, regressionPixels);
                    count++;
                }
            }
        }

        log.debug("Images {} and {} differ by {} pixels", reference, regression, count);
        if (count <= threshold) {
            log.debug("Images {} and {} differ by {} pixels, below or equal to the threshold of {}", reference, regression, count, threshold);
            return Result
                    .builder()
                    .regressionConfirmed(false)
                    .build();
        }

        final Path fullDestination = destination.resolve(name);
        final File destinationFile = fullDestination.toFile();

        log.debug("Writing diff between {} and {} at {}", reference, regression, destinationFile);
        ImageIO.write(referenceImage, fileUtils.getExtensionOf(name), destinationFile);

        return Result
                .builder()
                .path(fullDestination)
                .build();
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
