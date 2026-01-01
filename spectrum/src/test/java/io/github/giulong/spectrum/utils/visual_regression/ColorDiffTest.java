package io.github.giulong.spectrum.utils.visual_regression;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.visual_regression.ImageDiff.Result;

import lombok.Getter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class ColorDiffTest {

    private final int width = 3;
    private final int height = 5;
    private final int rgb = 123;
    private final byte[] pixelsRGB = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
    private final byte[] pixelsRGB2 = new byte[]{123, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private final byte[] pixelsARGB = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

    private MockedStatic<ImageIO> imageIOMockedStatic;

    @MockFinal
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @Mock
    private Color color;

    @Mock
    private Path reference;

    @Mock
    private Path regression;

    @Mock
    private Path destination;

    @Mock
    private File referenceFile;

    @Mock
    private File regressionFile;

    @Mock
    private File destinationFile;

    @Mock
    private BufferedImage referenceImage;

    @Mock
    private BufferedImage regressionImage;

    @Mock
    private WritableRaster raster;

    @Mock
    private DataBufferByte dataBuffer;

    @InjectMocks
    private DummyColorDiff diff;

    @BeforeEach
    void beforeEach() {
        imageIOMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        imageIOMockedStatic.close();
    }

    @Test
    @DisplayName("buildBetween should write the diff image between the two provided, at the provided destination and name")
    void buildBetween() throws IOException {
        final String name = "name";
        final String extension = "extension";

        when(color.getRGB()).thenReturn(rgb);

        when(reference.toFile()).thenReturn(referenceFile);
        when(ImageIO.read(referenceFile)).thenReturn(referenceImage);

        when(regression.toFile()).thenReturn(regressionFile);
        when(ImageIO.read(regressionFile)).thenReturn(regressionImage);

        // getPixelMatrixOf reference
        when(referenceImage.getWidth()).thenReturn(width);
        when(referenceImage.getHeight()).thenReturn(height);
        when(referenceImage.getAlphaRaster()).thenReturn(null);
        when(referenceImage.getRaster()).thenReturn(raster);
        when(raster.getDataBuffer()).thenReturn(dataBuffer);
        when(dataBuffer.getData())
                .thenReturn(pixelsRGB)
                .thenReturn(pixelsRGB2);

        // getPixelMatrixOf regression
        when(regressionImage.getWidth()).thenReturn(width);
        when(regressionImage.getHeight()).thenReturn(height);
        when(regressionImage.getAlphaRaster()).thenReturn(null);
        when(regressionImage.getRaster()).thenReturn(raster);

        when(destination.resolve(name)).thenReturn(destination);
        when(destination.toFile()).thenReturn(destinationFile);
        when(fileUtils.getExtensionOf(name)).thenReturn(extension);

        assertEquals(Result.builder().path(destination).build(), diff.buildBetween(reference, regression, destination, name));

        imageIOMockedStatic.verify(() -> ImageIO.write(referenceImage, extension, destinationFile));

        commonAssertions();
        verifyNoMoreInteractions(referenceImage);
    }

    @DisplayName("buildBetween should do nothing if reference and regression image have different sizes")
    @ParameterizedTest(name = "with regressionWidth {0} and regressionHeight {1}")
    @CsvSource({"3,100", "100,5", "100,100"})
    void buildBetween(final int regressionWidth, final int regressionHeight) throws IOException {
        final String name = "name";

        when(reference.toFile()).thenReturn(referenceFile);
        when(ImageIO.read(referenceFile)).thenReturn(referenceImage);

        when(regression.toFile()).thenReturn(regressionFile);
        when(ImageIO.read(regressionFile)).thenReturn(regressionImage);

        when(referenceImage.getWidth()).thenReturn(width);
        when(referenceImage.getHeight()).thenReturn(height);
        when(regressionImage.getWidth()).thenReturn(regressionWidth);
        when(regressionImage.getHeight()).thenReturn(regressionHeight);

        assertEquals(Result.builder().build(), diff.buildBetween(reference, regression, destination, name));

        verifyNoMoreInteractions(referenceImage);
        verifyNoMoreInteractions(regressionImage);
        verifyNoInteractions(destination);
        verifyNoInteractions(color);
        verifyNoInteractions(fileUtils);
    }

    @Test
    @DisplayName("buildBetween should return null and set regression not confirmed if the number of changed pixels is below the threshold")
    void buildBetweenBelowThreshold() throws IOException {
        final String name = "name";

        // just 2 pixels are actually different
        Reflections.setField("threshold", diff, 10);

        when(color.getRGB()).thenReturn(rgb);

        when(reference.toFile()).thenReturn(referenceFile);
        when(ImageIO.read(referenceFile)).thenReturn(referenceImage);

        when(regression.toFile()).thenReturn(regressionFile);
        when(ImageIO.read(regressionFile)).thenReturn(regressionImage);

        when(referenceImage.getWidth()).thenReturn(width);
        when(referenceImage.getHeight()).thenReturn(height);
        when(referenceImage.getAlphaRaster()).thenReturn(null);
        when(referenceImage.getRaster()).thenReturn(raster);
        when(raster.getDataBuffer()).thenReturn(dataBuffer);
        when(dataBuffer.getData())
                .thenReturn(pixelsRGB)
                .thenReturn(pixelsRGB2);

        // getPixelMatrixOf regression
        when(regressionImage.getWidth()).thenReturn(width);
        when(regressionImage.getHeight()).thenReturn(height);
        when(regressionImage.getAlphaRaster()).thenReturn(null);
        when(regressionImage.getRaster()).thenReturn(raster);

        assertEquals(Result.builder().regressionConfirmed(false).build(), diff.buildBetween(reference, regression, destination, name));

        verifyNoInteractions(destination);
        verifyNoInteractions(fileUtils);

        commonAssertions();
        verifyNoMoreInteractions(referenceImage);
    }

    @Test
    @DisplayName("getPixelMatrixOf should return the pixel matrix of the provided image with alpha channel")
    void getPixelMatrixOfARGB() {
        when(referenceImage.getAlphaRaster()).thenReturn(raster);
        when(referenceImage.getRaster()).thenReturn(raster);
        when(raster.getDataBuffer()).thenReturn(dataBuffer);
        when(dataBuffer.getData()).thenReturn(pixelsARGB);

        assertArrayEquals(
                new int[][]{{67305985, 134678021, 202050057}, {269422093, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
                diff.getPixelMatrixOf(referenceImage, width, height));
    }

    @Test
    @DisplayName("getPixelMatrixOf should return the pixel matrix of the provided image without alpha channel")
    void getPixelMatrixOfRGB() {
        when(referenceImage.getAlphaRaster()).thenReturn(null);
        when(referenceImage.getRaster()).thenReturn(raster);
        when(raster.getDataBuffer()).thenReturn(dataBuffer);
        when(dataBuffer.getData()).thenReturn(pixelsRGB);

        assertArrayEquals(
                new int[][]{{-16580095, -16382716, -16185337}, {-15987958, -15790579, -15593200}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, diff.getPixelMatrixOf(
                        referenceImage, width, height));
    }

    private void commonAssertions() {
        assertEquals(List.of(referenceImage, referenceImage), diff.getDiffs());
        assertEquals(List.of(0, 2), diff.getIList());
        assertEquals(List.of(0, 1), diff.getJList());
        assertEquals(List.of(rgb, rgb), diff.getRgbList());

        final List<int[][]> referencePixelsList = diff.getReferencePixelsList();
        assertEquals(2, referencePixelsList.size());
        for (int i = 0; i < 2; i++) {
            assertArrayEquals(
                    new int[][]{{-16580095, -16382716, -16185337}, {-15987958, -15790579, -15593200}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
                    referencePixelsList.get(i));
        }

        final List<int[][]> regressionPixelsList = diff.getRegressionPixelsList();
        assertEquals(2, regressionPixelsList.size());
        for (int i = 0; i < 2; i++) {
            assertArrayEquals(
                    new int[][]{{-16579973, -16382716, -16185337}, {-15987958, -15790579, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
                    regressionPixelsList.get(i));
        }
    }

    @Getter
    private static final class DummyColorDiff extends ColorDiff {

        private final List<BufferedImage> diffs = new ArrayList<>();
        private final List<Integer> iList = new ArrayList<>();
        private final List<Integer> jList = new ArrayList<>();
        private final List<Integer> rgbList = new ArrayList<>();
        private final List<int[][]> referencePixelsList = new ArrayList<>();
        private final List<int[][]> regressionPixelsList = new ArrayList<>();

        @Override
        protected void apply(final BufferedImage diff, final int i, final int j, final int rgb, final int[][] referencePixels, final int[][] regressionPixels) {
            diffs.add(diff);
            iList.add(i);
            jList.add(j);
            rgbList.add(rgb);
            referencePixelsList.add(referencePixels);
            regressionPixelsList.add(regressionPixels);
        }
    }
}
