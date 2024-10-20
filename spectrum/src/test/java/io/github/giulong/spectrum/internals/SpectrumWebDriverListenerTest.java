package io.github.giulong.spectrum.internals;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Drivers.Event;
import io.github.giulong.spectrum.utils.TestContext;
import io.github.giulong.spectrum.utils.web_driver_events.WebDriverEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.openqa.selenium.WebElement;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static ch.qos.logback.classic.Level.ALL;
import static ch.qos.logback.classic.Level.OFF;
import static io.github.giulong.spectrum.enums.Frame.AUTO_AFTER;
import static io.github.giulong.spectrum.enums.Frame.AUTO_BEFORE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.slf4j.event.Level.*;

class SpectrumWebDriverListenerTest {

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
    private List<Consumer<WebDriverEvent>> consumers;

    @Mock
    private Consumer<WebDriverEvent> consumer1;

    @Mock
    private Consumer<WebDriverEvent> consumer2;

    @Mock
    private Iterator<Consumer<WebDriverEvent>> iterator;

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

    @Mock
    private TestContext testContext;

    @InjectMocks
    private SpectrumWebDriverListener spectrumWebDriverListener;

    @BeforeEach
    public void beforeEach() {
        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(ch.qos.logback.classic.Level.INFO);

        webDriverEventMockedStatic = mockStatic(WebDriverEvent.class);
    }

    @AfterEach
    public void afterEach() {
        webDriverEventMockedStatic.close();
    }

    @SuppressWarnings("unchecked")
    private void webDriverEventStubsAtLevel(final org.slf4j.event.Level level) {
        final String formattedMessage = "message <div>arg</div>";

        when(WebDriverEvent.builder()).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.frame(AUTO_BEFORE)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.level(level)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.message(formattedMessage)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.build()).thenReturn(webDriverEvent);

        when(consumers.iterator()).thenReturn(iterator);
        doCallRealMethod().when(consumers).forEach(any());
        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(consumer1, consumer2);
    }

    @DisplayName("extractSelectorFrom should extract just the relevant info from the webElement")
    @ParameterizedTest(name = "with WebElement {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void extractSelectorFrom(final String fullWebElement, final String expected) {
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(expected);
        assertEquals(expected, spectrumWebDriverListener.extractSelectorFrom(webElement1));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> id: message]", "id: message"),
                arguments("[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #gettotal]] -> tag name: button]", "css selector: #gettotal -> tag name: button"),
                arguments("[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #get1-.total]] -> tag name: button]", "css selector: #get1-.total -> tag name: button")
        );
    }

    @Test
    @DisplayName("extractSelectorFrom should extract just the relevant info from the webElement")
    public void extractSelectorFromNoMatch() {
        final String fullWebElement = "fullWebElement";

        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(false);
        assertEquals("", spectrumWebDriverListener.extractSelectorFrom(webElement1));
    }

    @Test
    @DisplayName("parse should return a list of strings calling the extractSelectorFrom for each WebElement in the provided list, and using String.valueOf to avoid NPEs")
    public void parse() {
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

        assertEquals(expected, spectrumWebDriverListener.parse(args));
    }

    @Test
    @DisplayName("parse should return a list of strings calling the extractSelectorFrom for each WebElement in the provided list, and using String.valueOf to avoid NPEs, applying no additional format by default")
    public void parseDefault() {
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

        assertEquals(expected, spectrumWebDriverListener.parse(args));
    }

    @Test
    @DisplayName("TRACE level: listen should log the provided event with its args")
    public void listenTrace() {
        webDriverEventStubsAtLevel(TRACE);

        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(Level.TRACE);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.TRACE);
        when(event.getWait()).thenReturn(0L);

        spectrumWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("TRACE level off: listen should not log the provided event")
    public void listenTraceOff() {
        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(OFF);
        when(event.getLevel()).thenReturn(Level.TRACE);

        spectrumWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(event, never()).getMessage();
        verifyNoInteractions(consumers);
    }

    @Test
    @DisplayName("DEBUG level: listen should log the provided event with its args")
    public void listenDebug() {
        webDriverEventStubsAtLevel(DEBUG);

        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(ch.qos.logback.classic.Level.DEBUG);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(ch.qos.logback.classic.Level.DEBUG);
        when(event.getWait()).thenReturn(wait);

        spectrumWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("DEBUG level off: listen should not log the provided event")
    public void listenDebugOff() {
        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(OFF);
        when(event.getLevel()).thenReturn(ch.qos.logback.classic.Level.DEBUG);

        spectrumWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(event, never()).getMessage();
        verifyNoInteractions(consumers);
    }

    @Test
    @DisplayName("INFO level: listen should log the provided event with its args")
    public void listenInfo() {
        webDriverEventStubsAtLevel(INFO);

        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(ch.qos.logback.classic.Level.INFO);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(ch.qos.logback.classic.Level.INFO);
        when(event.getWait()).thenReturn(wait);

        spectrumWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("INFO level off: listen should not log the provided event")
    public void listenInfoOff() {
        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(OFF);
        when(event.getLevel()).thenReturn(ch.qos.logback.classic.Level.INFO);

        spectrumWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(event, never()).getMessage();
        verifyNoInteractions(consumers);
    }

    @Test
    @DisplayName("WARN level: listen should log the provided event with its args")
    public void listenWarn() {
        webDriverEventStubsAtLevel(WARN);

        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(Level.WARN);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(Level.WARN);
        when(event.getWait()).thenReturn(wait);

        spectrumWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("WARN level off: listen should not log the provided event")
    public void listenWarnOff() {
        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(OFF);
        when(event.getLevel()).thenReturn(Level.WARN);

        spectrumWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(event, never()).getMessage();
        verifyNoInteractions(consumers);
    }

    @Test
    @DisplayName("Default level: listen should log at DEBUG level as per logback default")
    public void listenDefault() {
        webDriverEventStubsAtLevel(DEBUG);

        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(ALL);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(ALL);

        spectrumWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("Default level: listen should log at DEBUG level as per logback default")
    public void listenDefaultOFF() {
        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(OFF);
        when(event.getLevel()).thenReturn(ALL);

        spectrumWebDriverListener.listenTo(AUTO_BEFORE, event, arg);

        verify(event, never()).getMessage();
        verifyNoInteractions(consumers);
    }

    @Test
    @DisplayName("beforeSendKeys should call listenTo passing the keysToSend for regular webElements")
    public void beforeSendKeys() {
        final String keysToSend = "keysToSend";
        final String fullWebElement = "fullWebElement";
        final String message = "message %s %s";
        final String formattedMessage = "message " + fullWebElement + " [" + keysToSend + "]";

        when(testContext.isSecuredWebElement(webElement1)).thenReturn(false);

        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(ch.qos.logback.classic.Level.INFO);
        when(events.getBeforeSendKeys()).thenReturn(event);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(ch.qos.logback.classic.Level.INFO);
        when(event.getWait()).thenReturn(wait);

        when(WebDriverEvent.builder()).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.frame(AUTO_BEFORE)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.level(INFO)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.message(formattedMessage)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.build()).thenReturn(webDriverEvent);

        when(consumers.iterator()).thenReturn(iterator);
        doCallRealMethod().when(consumers).forEach(any());
        when(iterator.hasNext()).thenReturn(true, true, false);
        doReturn(consumer1, consumer2).when(iterator).next();

        // extractSelectorFrom
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(fullWebElement);

        spectrumWebDriverListener.beforeSendKeys(webElement1, keysToSend);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("beforeSendKeys should call listenTo masking the keysToSend for secured webElements")
    public void beforeSendKeysSecured() {
        final String keysToSend = "keysToSend";
        final String fullWebElement = "fullWebElement";
        final String message = "message %s %s";
        final String formattedMessage = "message " + fullWebElement + " [***]";

        when(testContext.isSecuredWebElement(webElement1)).thenReturn(true);

        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(ch.qos.logback.classic.Level.INFO);
        when(events.getBeforeSendKeys()).thenReturn(event);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(ch.qos.logback.classic.Level.INFO);
        when(event.getWait()).thenReturn(wait);

        when(WebDriverEvent.builder()).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.frame(AUTO_BEFORE)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.level(INFO)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.message(formattedMessage)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.build()).thenReturn(webDriverEvent);

        when(consumers.iterator()).thenReturn(iterator);
        doCallRealMethod().when(consumers).forEach(any());
        when(iterator.hasNext()).thenReturn(true, true, false);
        doReturn(consumer1, consumer2).when(iterator).next();

        // extractSelectorFrom
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(fullWebElement);

        spectrumWebDriverListener.beforeSendKeys(webElement1, keysToSend);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("afterSendKeys should call listenTo passing the keysToSend for regular webElements")
    public void afterSendKeys() {
        final String keysToSend = "keysToSend";
        final String fullWebElement = "fullWebElement";
        final String message = "message %s %s";
        final String formattedMessage = "message " + fullWebElement + " [" + keysToSend + "]";

        when(testContext.isSecuredWebElement(webElement1)).thenReturn(false);

        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(ch.qos.logback.classic.Level.INFO);
        when(events.getAfterSendKeys()).thenReturn(event);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(ch.qos.logback.classic.Level.INFO);
        when(event.getWait()).thenReturn(wait);

        when(WebDriverEvent.builder()).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.frame(AUTO_AFTER)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.level(INFO)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.message(formattedMessage)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.build()).thenReturn(webDriverEvent);

        when(consumers.iterator()).thenReturn(iterator);
        doCallRealMethod().when(consumers).forEach(any());
        when(iterator.hasNext()).thenReturn(true, true, false);
        doReturn(consumer1, consumer2).when(iterator).next();

        // extractSelectorFrom
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(fullWebElement);

        spectrumWebDriverListener.afterSendKeys(webElement1, keysToSend);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }

    @Test
    @DisplayName("afterSendKeys should call listenTo masking the keysToSend for secured webElements")
    public void afterSendKeysSecured() {
        final String keysToSend = "keysToSend";
        final String fullWebElement = "fullWebElement";
        final String message = "message %s %s";
        final String formattedMessage = "message " + fullWebElement + " [***]";

        when(testContext.isSecuredWebElement(webElement1)).thenReturn(true);

        ((Logger) LoggerFactory.getLogger(SpectrumWebDriverListener.class)).setLevel(ch.qos.logback.classic.Level.INFO);
        when(events.getAfterSendKeys()).thenReturn(event);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(ch.qos.logback.classic.Level.INFO);
        when(event.getWait()).thenReturn(wait);

        when(WebDriverEvent.builder()).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.frame(AUTO_AFTER)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.level(INFO)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.message(formattedMessage)).thenReturn(webDriverEventBuilder);
        when(webDriverEventBuilder.build()).thenReturn(webDriverEvent);

        when(consumers.iterator()).thenReturn(iterator);
        doCallRealMethod().when(consumers).forEach(any());
        when(iterator.hasNext()).thenReturn(true, true, false);
        doReturn(consumer1, consumer2).when(iterator).next();

        // extractSelectorFrom
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(fullWebElement);

        spectrumWebDriverListener.afterSendKeys(webElement1, keysToSend);

        verify(consumer1).accept(webDriverEvent);
        verify(consumer2).accept(webDriverEvent);
    }
}
