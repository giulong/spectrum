package io.github.giulong.spectrum.internals.web_driver_listeners;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Drivers.Event;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.web_driver_events.WebDriverEvent;
import io.github.giulong.spectrum.utils.web_driver_events.WebDriverEventConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.giulong.spectrum.enums.Frame.AUTO_AFTER;
import static io.github.giulong.spectrum.enums.Frame.AUTO_BEFORE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.slf4j.event.Level.*;

class EventsWebDriverListenerTest {

    private static MockedStatic<WebDriverEvent> webDriverEventMockedStatic;

    private final String arg = "arg";
    private final String message = "message <div>%s</div>";
    private final long wait = 1L;

    @Mock
    private Event event;

    @Mock
    private WebElement webElement1;

    @Mock
    private WebElement webElement2;

    @Mock
    private WebElement webElement3;

    @Mock
    private List<WebDriverEventConsumer> consumers;

    @Mock
    private WebDriverEventConsumer consumer1;

    @Mock
    private WebDriverEventConsumer consumer2;

    @Mock
    private WebDriverEventConsumer consumer3;

    @Mock
    private WebDriverEvent.WebDriverEventBuilder webDriverEventBuilder;

    @Mock
    private WebDriverEvent webDriverEvent;

    @Mock
    private Pattern locatorPattern;

    @Mock
    private Matcher matcher;

    @Mock
    private Configuration.Drivers.Events events;

    @InjectMocks
    private EventsWebDriverListener eventsWebDriverListener = new EventsWebDriverListener(EventsWebDriverListener.builder());

    @BeforeEach
    void beforeEach() {
        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(ch.qos.logback.classic.Level.INFO);

        webDriverEventMockedStatic = mockStatic(WebDriverEvent.class);
    }

    @AfterEach
    void afterEach() {
        webDriverEventMockedStatic.close();
    }

    private void webDriverEventStubsAtLevel(final org.slf4j.event.Level level) {
        final String formattedMessage = "message <div>arg</div>";

        Reflections.setField("consumers", eventsWebDriverListener, List.of(consumer1, consumer2));

        when(WebDriverEvent.builder()).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.frame(AUTO_BEFORE)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.level(level)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.args(List.of(arg))).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.message(formattedMessage)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.build()).thenReturn(webDriverEvent);

        when(consumer1.isEnabled()).thenReturn(true);
        when(consumer2.isEnabled()).thenReturn(true);
    }

    @Test
    @DisplayName("parse should return a list of strings calling the extractSelectorFrom for each WebElement in the provided list, and using String.valueOf to avoid NPEs")
    void parse() {
        final String webElement1ToString = "[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> id: message]";
        final String webElement2ToString = "[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #gettotal]] -> tag name: button]";
        final String webElement3ToString = "[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #get1-.total]] -> tag name: button]";
        final String expected1 = "id: message";
        final String expected2 = "css selector: #gettotal -> tag name: button";
        final String expected3 = "css selector: #get1-.total -> tag name: button";

        final String s = "string";
        final List<String> expected = Arrays.asList(
                "id: message",
                s,
                "null",
                "css selector: #gettotal -> tag name: button",
                "css selector: #get1-.total -> tag name: button"
        );

        when(webElement1.toString()).thenReturn(webElement1ToString);
        when(webElement2.toString()).thenReturn(webElement2ToString);
        when(webElement3.toString()).thenReturn(webElement3ToString);
        when(locatorPattern.matcher(webElement1ToString)).thenReturn(matcher);
        when(locatorPattern.matcher(webElement2ToString)).thenReturn(matcher);
        when(locatorPattern.matcher(webElement3ToString)).thenReturn(matcher);
        when(matcher.find())
                .thenReturn(true).thenReturn(false)
                .thenReturn(true).thenReturn(false)
                .thenReturn(true).thenReturn(false);
        when(matcher.group(1))
                .thenReturn(expected1)
                .thenReturn(expected2)
                .thenReturn(expected3);

        final Object[] args = new Object[]{webElement1, s, null, webElement2, webElement3};

        assertEquals(expected, eventsWebDriverListener.parse(args));
    }

    @Test
    @DisplayName("parse should return a list of strings calling the extractSelectorFrom for each WebElement in the provided list, " +
            "and using String.valueOf to avoid NPEs, applying no additional format by default")
    void parseDefault() {
        final String webElement1ToString = "[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> id: message]";
        final String webElement2ToString = "[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #gettotal]] -> tag name: button]";
        final String webElement3ToString = "[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #get1-.total]] -> tag name: button]";
        final String expected1 = "id: message";
        final String expected2 = "css selector: #gettotal -> tag name: button";
        final String expected3 = "css selector: #get1-.total -> tag name: button";

        final String s = "string";
        final List<String> expected = Arrays.asList(
                "id: message",
                s,
                "null",
                "css selector: #gettotal -> tag name: button",
                "css selector: #get1-.total -> tag name: button"
        );

        when(webElement1.toString()).thenReturn(webElement1ToString);
        when(webElement2.toString()).thenReturn(webElement2ToString);
        when(webElement3.toString()).thenReturn(webElement3ToString);
        when(locatorPattern.matcher(webElement1ToString)).thenReturn(matcher);
        when(locatorPattern.matcher(webElement2ToString)).thenReturn(matcher);
        when(locatorPattern.matcher(webElement3ToString)).thenReturn(matcher);
        when(matcher.find())
                .thenReturn(true).thenReturn(false)
                .thenReturn(true).thenReturn(false)
                .thenReturn(true).thenReturn(false);
        when(matcher.group(1))
                .thenReturn(expected1)
                .thenReturn(expected2)
                .thenReturn(expected3);

        final Object[] args = new Object[]{webElement1, s, null, webElement2, webElement3};

        assertEquals(expected, eventsWebDriverListener.parse(args));
    }

    @Test
    @DisplayName("TRACE level: listen should log the provided event with its args")
    void listenTrace() {
        webDriverEventStubsAtLevel(TRACE);

        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.TRACE);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(TRACE);
        when(event.getWait()).thenReturn(0L);

        eventsWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("TRACE level off: listen should not log the provided event")
    void listenTraceOff() {
        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.OFF);
        when(event.getLevel()).thenReturn(TRACE);

        eventsWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(event, never()).getMessage();
        verifyNoInteractions(consumers);
    }

    @Test
    @DisplayName("DEBUG level: listen should log the provided event with its args")
    void listenDebug() {
        webDriverEventStubsAtLevel(DEBUG);

        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.DEBUG);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(DEBUG);
        when(event.getWait()).thenReturn(wait);

        eventsWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("DEBUG level off: listen should not log the provided event")
    void listenDebugOff() {
        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.OFF);
        when(event.getLevel()).thenReturn(DEBUG);

        eventsWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(event, never()).getMessage();
        verifyNoInteractions(consumers);
    }

    @Test
    @DisplayName("INFO level: listen should log the provided event with its args")
    void listenInfo() {
        webDriverEventStubsAtLevel(INFO);

        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.INFO);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(INFO);
        when(event.getWait()).thenReturn(wait);

        eventsWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("INFO level off: listen should not log the provided event")
    void listenInfoOff() {
        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.OFF);
        when(event.getLevel()).thenReturn(INFO);

        eventsWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(event, never()).getMessage();
        verifyNoInteractions(consumers);
    }

    @Test
    @DisplayName("WARN level: listen should log the provided event with its args")
    void listenWarn() {
        webDriverEventStubsAtLevel(org.slf4j.event.Level.WARN);

        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.WARN);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(WARN);
        when(event.getWait()).thenReturn(wait);

        eventsWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("WARN level off: listen should not log the provided event")
    void listenWarnOff() {
        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.OFF);
        when(event.getLevel()).thenReturn(WARN);

        eventsWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(event, never()).getMessage();
        verifyNoInteractions(consumers);
    }

    @DisplayName("Default level (null): listen should not log regardless of the log's level")
    @ParameterizedTest(name = "with log level {0}")
    @ValueSource(strings = {"ERROR", "WARN", "INFO", "DEBUG", "TRACE"})
    void listenDefault(final String level) {
        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.toLevel(level));
        when(event.getLevel()).thenReturn(null);

        eventsWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(event, never()).getMessage();
        verifyNoInteractions(consumers);
    }

    @Test
    @DisplayName("beforeSendKeys should call listenTo passing the keysToSend for regular webElements")
    void beforeSendKeys() {
        final String keysToSend = "keysToSend";
        final String fullWebElement = "fullWebElement";
        final String localMessage = "message %s %s";
        final String formattedMessage = "message " + fullWebElement + " [" + keysToSend + "]";

        Reflections.setField("consumers", eventsWebDriverListener, List.of(consumer1, consumer2));

        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.INFO);
        when(events.getBeforeSendKeys()).thenReturn(event);
        when(event.getMessage()).thenReturn(localMessage);
        when(event.getLevel()).thenReturn(INFO);
        when(event.getWait()).thenReturn(wait);

        when(WebDriverEvent.builder()).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.frame(AUTO_BEFORE)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.level(INFO)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.args(List.of(webElement1, Arrays.toString(new CharSequence[]{keysToSend})))).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.message(formattedMessage)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.build()).thenReturn(webDriverEvent);

        // extractSelectorFrom
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(fullWebElement);

        when(consumer1.isEnabled()).thenReturn(true);
        when(consumer2.isEnabled()).thenReturn(true);

        eventsWebDriverListener.beforeSendKeys(webElement1, keysToSend);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("beforeSendKeys should call listenTo masking the keysToSend for secured webElements")
    void beforeSendKeysSecured() {
        final String key = "key";
        final String firstKeyToSend = "@Secured@" + key + "@Secured@";
        final CharSequence[] keysToSend = new CharSequence[]{firstKeyToSend, Keys.ADD, "ok"};
        final String fullWebElement = "fullWebElement";
        final String localMessage = "message %s %s";
        final String formattedMessage = "message " + fullWebElement + " [***]";

        Reflections.setField("consumers", eventsWebDriverListener, List.of(consumer1, consumer2));

        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.INFO);
        when(events.getBeforeSendKeys()).thenReturn(event);
        when(event.getMessage()).thenReturn(localMessage);
        when(event.getLevel()).thenReturn(INFO);
        when(event.getWait()).thenReturn(wait);

        when(WebDriverEvent.builder()).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.frame(AUTO_BEFORE)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.level(INFO)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.args(List.of(webElement1, Arrays.toString(new CharSequence[]{"***"})))).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.message(formattedMessage)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.build()).thenReturn(webDriverEvent);

        // extractSelectorFrom
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(fullWebElement);

        when(consumer1.isEnabled()).thenReturn(true);
        when(consumer2.isEnabled()).thenReturn(true);

        eventsWebDriverListener.beforeSendKeys(webElement1, keysToSend);

        assertArrayEquals(new CharSequence[]{key, Keys.ADD, "ok"}, keysToSend);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("afterSendKeys should call listenTo passing the keysToSend for regular webElements, calling enabled consumers")
    void afterSendKeys() {
        final String keysToSend = "keysToSend";
        final String fullWebElement = "fullWebElement";
        final String localMessage = "message %s %s";
        final String formattedMessage = "message " + fullWebElement + " [" + keysToSend + "]";

        Reflections.setField("consumers", eventsWebDriverListener, List.of(consumer1, consumer2, consumer3));

        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.INFO);
        when(events.getAfterSendKeys()).thenReturn(event);
        when(event.getMessage()).thenReturn(localMessage);
        when(event.getLevel()).thenReturn(INFO);
        when(event.getWait()).thenReturn(wait);

        when(WebDriverEvent.builder()).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.frame(AUTO_AFTER)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.level(INFO)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.args(List.of(webElement1, Arrays.toString(new CharSequence[]{keysToSend})))).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.message(formattedMessage)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.build()).thenReturn(webDriverEvent);

        // extractSelectorFrom
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(fullWebElement);

        when(consumer1.isEnabled()).thenReturn(true);
        when(consumer2.isEnabled()).thenReturn(true);
        when(consumer3.isEnabled()).thenReturn(false);

        eventsWebDriverListener.afterSendKeys(webElement1, keysToSend);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
        verify(consumer3, never()).accept(webDriverEvent);
    }

    @Test
    @DisplayName("afterSendKeys should call listenTo masking the keysToSend for secured webElements")
    void afterSendKeysSecured() {
        final String key = "key";
        final String firstKeyToSend = "@Secured@" + key + "@Secured@";
        final CharSequence[] keysToSend = new CharSequence[]{firstKeyToSend, Keys.ADD, "ok"};
        final String fullWebElement = "fullWebElement";
        final String localMessage = "message %s %s";
        final String formattedMessage = "message " + fullWebElement + " [***]";

        Reflections.setField("consumers", eventsWebDriverListener, List.of(consumer1, consumer2));

        ((Logger) LoggerFactory.getLogger(EventsWebDriverListener.class)).setLevel(Level.INFO);
        when(events.getAfterSendKeys()).thenReturn(event);
        when(event.getMessage()).thenReturn(localMessage);
        when(event.getLevel()).thenReturn(INFO);
        when(event.getWait()).thenReturn(wait);

        when(WebDriverEvent.builder()).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.frame(AUTO_AFTER)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.level(INFO)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.args(List.of(webElement1, Arrays.toString(new CharSequence[]{"***"})))).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.message(formattedMessage)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.build()).thenReturn(webDriverEvent);

        // extractSelectorFrom
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(fullWebElement);

        when(consumer1.isEnabled()).thenReturn(true);
        when(consumer2.isEnabled()).thenReturn(true);

        eventsWebDriverListener.afterSendKeys(webElement1, keysToSend);

        assertArrayEquals(new CharSequence[]{key, Keys.ADD, "ok"}, keysToSend);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("isSecured should unwrap the first keysToSend and return true check if it matches the secured pattern")
    void isSecuredTrue() {
        final String key = "key";
        final String firstKeyToSend = "@Secured@" + key + "@Secured@";
        final CharSequence[] keysToSend = new CharSequence[]{firstKeyToSend, Keys.ADD, "ok"};

        assertTrue(eventsWebDriverListener.isSecured(keysToSend));
        assertArrayEquals(new CharSequence[]{key, Keys.ADD, "ok"}, keysToSend);
    }

    @Test
    @DisplayName("isSecured should just return false check if the first keyToSend doesn't match the secured pattern")
    void isSecuredFalse() {
        final String key = "key";
        final CharSequence[] keysToSend = new CharSequence[]{key, Keys.ADD, "ok"};

        assertFalse(eventsWebDriverListener.isSecured(keysToSend));
        assertArrayEquals(new CharSequence[]{key, Keys.ADD, "ok"}, keysToSend);
    }
}
