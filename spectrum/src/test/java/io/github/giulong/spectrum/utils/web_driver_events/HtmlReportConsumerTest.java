package io.github.giulong.spectrum.utils.web_driver_events;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.HtmlUtils;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.event.Level;

import static com.aventstack.extentreports.Status.INFO;
import static com.aventstack.extentreports.Status.WARNING;
import static io.github.giulong.spectrum.enums.Frame.AUTO_BEFORE;
import static org.mockito.Mockito.*;
import static org.slf4j.event.Level.WARN;

class HtmlReportConsumerTest {

    private final String message = "message";
    private final String details = "details";
    private final int frameNumber = 123;

    @Mock
    private TestData testData;

    @Mock
    private HtmlUtils htmlUtils;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private ExtentTest currentNode;

    @Mock
    private WebDriverEvent webDriverEvent;

    @Mock
    private Video video;

    @InjectMocks
    private HtmlReportConsumer htmlReportConsumer = new HtmlReportConsumer(HtmlReportConsumer.builder());

    @BeforeEach
    void beforeEach() {
        Reflections.setField("htmlUtils", htmlReportConsumer, htmlUtils);
    }

    private void stubsFor(final Level level) {
        when(statefulExtentTest.getCurrentNode()).thenReturn(currentNode);
        when(webDriverEvent.getMessage()).thenReturn(message);
        when(webDriverEvent.getLevel()).thenReturn(level);
        when(webDriverEvent.getFrame()).thenReturn(AUTO_BEFORE);

        when(video.getAndIncrementFrameNumberFor(testData, AUTO_BEFORE)).thenReturn(frameNumber);
    }

    @DisplayName("accept should log the video tag at info level")
    @ParameterizedTest
    @CsvSource({"INFO", "ERROR", "DEBUG", "TRACE"})
    void accept(final Level level) {
        stubsFor(level);

        when(video.shouldRecord(AUTO_BEFORE)).thenReturn(true);
        when(htmlUtils.buildFrameTagFor(frameNumber, message, testData)).thenReturn(details);

        htmlReportConsumer.accept(webDriverEvent);

        verify(currentNode).log(INFO, details);
        verifyNoMoreInteractions(currentNode);
    }

    @DisplayName("accept should log the plain message at info level")
    @ParameterizedTest
    @CsvSource({"INFO", "ERROR", "DEBUG", "TRACE"})
    void acceptPlainMessage(final Level level) {
        stubsFor(level);

        when(video.shouldRecord(AUTO_BEFORE)).thenReturn(false);

        htmlReportConsumer.accept(webDriverEvent);

        verify(currentNode).log(INFO, message);
        verifyNoMoreInteractions(currentNode);
        verifyNoInteractions(htmlUtils);
    }

    @Test
    @DisplayName("accept should log the video tag at warn level")
    void acceptWarn() {
        stubsFor(WARN);

        when(video.shouldRecord(AUTO_BEFORE)).thenReturn(true);
        when(htmlUtils.buildFrameTagFor(frameNumber, message, testData)).thenReturn(details);

        htmlReportConsumer.accept(webDriverEvent);

        verify(currentNode).log(WARNING, details);
        verifyNoMoreInteractions(currentNode);
    }

    @Test
    @DisplayName("accept should log the plain message at warn level")
    void acceptWarnPlainMessage() {
        stubsFor(WARN);

        when(video.shouldRecord(AUTO_BEFORE)).thenReturn(false);

        htmlReportConsumer.accept(webDriverEvent);

        verify(currentNode).log(WARNING, message);
        verifyNoMoreInteractions(currentNode);
        verifyNoInteractions(htmlUtils);
    }
}
