package io.github.giulong.spectrum.utils;

import static com.aventstack.extentreports.Status.INFO;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.List;

import com.aventstack.extentreports.Status;

import io.github.giulong.spectrum.exceptions.VisualRegressionException;
import io.github.giulong.spectrum.utils.TestData.Screenshot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class TestDataTest {

    private MockedStatic<Screenshot> screenshotMockedStatic;

    @Spy
    private TestData.VisualRegression visualRegression = TestData.VisualRegression.builder().build();

    @Mock
    private Screenshot.ScreenshotBuilder screenshotBuilder;

    @Mock
    private Screenshot screenshot;

    @InjectMocks
    private TestData testData = TestData.builder().build();

    @BeforeEach
    void beforeEach() {
        screenshotMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        screenshotMockedStatic.close();
    }

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
    @DisplayName("registerFailedVisualRegression should set the testFailedException with the updated regressions count")
    void registerFailedVisualRegression() {
        try (MockedConstruction<VisualRegressionException> mockedConstruction = mockConstruction()) {
            assertNull(testData.getTestFailedException());

            testData.registerFailedVisualRegression();  // first regression
            assertEquals(1, visualRegression.getCount());

            testData.registerFailedVisualRegression();  // second regression
            assertEquals(2, visualRegression.getCount());

            final List<VisualRegressionException> constructed = mockedConstruction.constructed();
            assertEquals(2, constructed.size());
            assertEquals(constructed.getLast(), testData.getTestFailedException());
        }
    }

    @Test
    @DisplayName("buildScreenshotFor should build a screenshot with the provided params")
    void buildScreenshotFor() {
        final String message = "message";
        final Status status = INFO;

        when(Screenshot.builder()).thenReturn(screenshotBuilder);
        when(screenshotBuilder.frame(MANUAL)).thenReturn(screenshotBuilder);
        when(screenshotBuilder.message(message)).thenReturn(screenshotBuilder);
        when(screenshotBuilder.status(status)).thenReturn(screenshotBuilder);
        when(screenshotBuilder.build()).thenReturn(screenshot);

        testData.buildScreenshotFor(MANUAL, message, status);
    }
}
