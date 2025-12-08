package io.github.giulong.spectrum.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockConstruction;

import io.github.giulong.spectrum.exceptions.VisualRegressionException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.Spy;

class TestDataTest {

    @Spy
    private TestData.VisualRegression visualRegression = TestData.VisualRegression.builder().build();

    @InjectMocks
    private TestData testData = TestData.builder().build();

    @Test
    @DisplayName("incrementScreenshotNumber should return the incremented screenshotNumber")
    void incrementScreenshotNumber() {
        final int screenshotNumber = 123;

        Reflections.setField("screenshotNumber", testData, screenshotNumber);

        testData.incrementScreenshotNumber();

        assertEquals(screenshotNumber + 1, testData.getScreenshotNumber());
    }

    @Test
    @DisplayName("incrementFrameNumber should return the incremented frameNumber")
    void incrementFrameNumber() {
        final int frameNumber = 123;

        Reflections.setField("frameNumber", testData, frameNumber);

        testData.incrementFrameNumber();

        assertEquals(frameNumber + 1, testData.getFrameNumber());
    }

    @Test
    @DisplayName("registerFailedVisualRegression should set the testFailedException only on the first visual regression")
    void registerFailedVisualRegression() {
        try (MockedConstruction<VisualRegressionException> ignored = mockConstruction(VisualRegressionException.class,
                (mock, context) -> assertEquals(String.format("There were %d visual regressions", 2), context.arguments().getFirst()))) {
            assertDoesNotThrow(() -> testData.getTestFailedException().get());

            testData.registerFailedVisualRegression();  // first regression
            assertEquals(1, visualRegression.getCount());

            testData.registerFailedVisualRegression();  // second regression
            assertEquals(2, visualRegression.getCount());

            assertThrowsExactly(VisualRegressionException.class, () -> testData.getTestFailedException().get(), "There were " + 2 + " visual regressions");
        }
    }
}
