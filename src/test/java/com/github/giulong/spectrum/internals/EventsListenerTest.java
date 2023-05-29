package com.github.giulong.spectrum.internals;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import com.github.giulong.spectrum.pojos.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import static ch.qos.logback.classic.Level.*;
import static com.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventsListener")
class EventsListenerTest {

    private final String arg = "arg";
    private final String message = "message <div>%s</div>";
    private final String tagsMessage = "message <div>" + arg + "</div>";

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private Configuration.Event event;

    @Mock
    private ExtentTest extentTest;

    @Captor
    private ArgumentCaptor<Markup> markupArgumentCaptor;

    @InjectMocks
    private EventsListener eventsListener;

    @BeforeEach
    public void beforeEach() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(INFO);
    }

    @Test
    @DisplayName("OFF level: log should not log the provided event")
    public void logOff() {
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.OFF);

        eventsListener.log(event, arg);
    }

    @Test
    @DisplayName("TRACE level: log should log the provided event with its args")
    public void logTrace() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(TRACE);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.TRACE);
        when(store.get(EXTENT_TEST, ExtentTest.class)).thenReturn(extentTest);

        eventsListener.log(event, arg);
        verify(extentTest).info(tagsMessage);
    }

    @Test
    @DisplayName("TRACE level off: log should not log the provided event")
    public void logTraceOff() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(OFF);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.TRACE);

        eventsListener.log(event, arg);
        verify(store, never()).get(EXTENT_TEST, ExtentTest.class);
        verify(extentTest, never()).info(tagsMessage);
    }

    @Test
    @DisplayName("DEBUG level: log should log the provided event with its args")
    public void logDebug() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(DEBUG);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.DEBUG);
        when(store.get(EXTENT_TEST, ExtentTest.class)).thenReturn(extentTest);

        eventsListener.log(event, arg);
        verify(extentTest).info(tagsMessage);
    }

    @Test
    @DisplayName("DEBUG level off: log should not log the provided event")
    public void logDebugOff() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(OFF);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.DEBUG);

        eventsListener.log(event, arg);
        verify(store, never()).get(EXTENT_TEST, ExtentTest.class);
        verify(extentTest, never()).info(tagsMessage);
    }

    @Test
    @DisplayName("INFO level: log should log the provided event with its args")
    public void logInfo() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(INFO);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.INFO);
        when(store.get(EXTENT_TEST, ExtentTest.class)).thenReturn(extentTest);

        eventsListener.log(event, arg);
        verify(extentTest).info(tagsMessage);
    }

    @Test
    @DisplayName("INFO level off: log should not log the provided event")
    public void logInfoOff() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(OFF);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.INFO);

        eventsListener.log(event, arg);
        verify(store, never()).get(EXTENT_TEST, ExtentTest.class);
        verify(extentTest, never()).info(tagsMessage);
    }

    @Test
    @DisplayName("WARN level: log should log the provided event with its args")
    public void logWarn() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(WARN);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.WARN);
        when(store.get(EXTENT_TEST, ExtentTest.class)).thenReturn(extentTest);

        eventsListener.log(event, arg);

        verify(extentTest).warning(markupArgumentCaptor.capture());
        final Markup markup = markupArgumentCaptor.getValue();
        assertEquals("<span class='badge white-text yellow'>" + tagsMessage + "</span>", markup.getMarkup());
    }

    @Test
    @DisplayName("WARN level off: log should not log the provided event")
    public void logWarnOff() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(OFF);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.WARN);

        eventsListener.log(event, arg);
        verify(store, never()).get(EXTENT_TEST, ExtentTest.class);
        verify(extentTest, never()).warning(markupArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Not matching level: log should not log the provided event")
    public void logDefault() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(ALL);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.ALL);

        eventsListener.log(event, arg);
        verify(store, never()).get(EXTENT_TEST, ExtentTest.class);
        verify(extentTest, never()).warning(markupArgumentCaptor.capture());
    }
}