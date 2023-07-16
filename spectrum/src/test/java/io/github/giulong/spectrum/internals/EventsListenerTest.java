package io.github.giulong.spectrum.internals;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import io.github.giulong.spectrum.pojos.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebElement;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static ch.qos.logback.classic.Level.*;
import static io.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
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
    private Configuration.WebDriver.Event event;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private WebElement webElement1;

    @Mock
    private WebElement webElement2;

    @Mock
    private WebElement webElement3;

    @Captor
    private ArgumentCaptor<Markup> markupArgumentCaptor;

    @InjectMocks
    private EventsListener eventsListener;

    @BeforeEach
    public void beforeEach() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(INFO);
    }

    @DisplayName("extractSelectorFrom should extract just the relevant info from the webElement")
    @ParameterizedTest(name = "with WebElement {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void extractSelectorFrom(final String fullWebElement, final String expected) {
        when(webElement1.toString()).thenReturn(fullWebElement);
        assertEquals(expected, eventsListener.extractSelectorFrom(webElement1));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> id: message]", "id: message"),
                arguments("[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #gettotal]] -> tag name: button]", "css selector: #gettotal -> tag name: button"),
                arguments("[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #get1-.total]] -> tag name: button]", "css selector: #get1-.total -> tag name: button")
        );
    }

    @Test
    @DisplayName("parse should return a list of strings calling the extractSelectorFrom for each WebElement in the provided list, and using String.valueOf to avoid NPEs")
    public void parse() {
        final String s = "string";
        final List<String> expected = Arrays.asList(
                "id: message",
                s,
                "null",
                "css selector: #gettotal -> tag name: button",
                "css selector: #get1-.total -> tag name: button"
        );

        when(webElement1.toString()).thenReturn("[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> id: message]");
        when(webElement2.toString()).thenReturn("[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #gettotal]] -> tag name: button]");
        when(webElement3.toString()).thenReturn("[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #get1-.total]] -> tag name: button]");

        final Object[] args = new Object[] { webElement1, s, null, webElement2, webElement3 };

        assertEquals(expected, eventsListener.parse(args));
    }

    @Test
    @DisplayName("OFF level: log should not log the provided event")
    public void logOff() {
        when(event.getLevel()).thenReturn(Level.OFF);

        eventsListener.log(event, arg);
        verify(event, never()).getMessage();
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
        when(event.getLevel()).thenReturn(Level.TRACE);

        eventsListener.log(event, arg);
        verify(event, never()).getMessage();
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
        when(event.getLevel()).thenReturn(Level.DEBUG);

        eventsListener.log(event, arg);
        verify(event, never()).getMessage();
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
        when(event.getLevel()).thenReturn(Level.INFO);

        eventsListener.log(event, arg);
        verify(event, never()).getMessage();
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
        when(event.getLevel()).thenReturn(Level.WARN);

        eventsListener.log(event, arg);
        verify(event, never()).getMessage();
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
