package io.github.giulong.spectrum.utils.web_driver_events;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.event.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.slf4j.event.Level.WARN;

class HtmlReportConsumerTest {

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private ExtentTest currentNode;

    @Mock
    private WebDriverEvent webDriverEvent;

    @Captor
    private ArgumentCaptor<Markup> markupArgumentCaptor;

    @InjectMocks
    private HtmlReportConsumer htmlReportConsumer = new HtmlReportConsumer(HtmlReportConsumer.builder());

    @DisplayName("accept should log the message at info level")
    @ParameterizedTest
    @CsvSource({"INFO", "ERROR", "DEBUG", "TRACE"})
    void accept(final Level level) {
        final String message = "message";

        when(statefulExtentTest.getCurrentNode()).thenReturn(currentNode);
        when(webDriverEvent.getMessage()).thenReturn(message);
        when(webDriverEvent.getLevel()).thenReturn(level);

        htmlReportConsumer.accept(webDriverEvent);

        verify(currentNode).info(message);
        verifyNoMoreInteractions(currentNode);
    }

    @Test
    @DisplayName("accept should log the message at warn level")
    void acceptWarn() {
        final String message = "message";

        when(statefulExtentTest.getCurrentNode()).thenReturn(currentNode);
        when(webDriverEvent.getMessage()).thenReturn(message);
        when(webDriverEvent.getLevel()).thenReturn(WARN);

        htmlReportConsumer.accept(webDriverEvent);

        verify(currentNode).warning(markupArgumentCaptor.capture());
        verifyNoMoreInteractions(currentNode);

        assertEquals("<span class='badge white-text yellow'>message</span>", markupArgumentCaptor.getValue().getMarkup());
    }
}
