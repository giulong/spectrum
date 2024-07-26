package io.github.giulong.spectrum.internals;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import io.github.giulong.spectrum.utils.Configuration.Drivers.Event;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.SneakyThrows;
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
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static ch.qos.logback.classic.Level.*;
import static io.github.giulong.spectrum.enums.Frame.AUTO_AFTER;
import static io.github.giulong.spectrum.enums.Frame.AUTO_BEFORE;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.OutputType.BYTES;

@ExtendWith(MockitoExtension.class)
class EventsListenerTest {

    private static final String UUID_REGEX = AUTO_AFTER.getValue() + "-([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\\.png";

    private final String arg = "arg";
    private final String message = "message <div>%s</div>";
    private final String tagsMessage = "message <div><code>" + arg + "</code></div>";
    private final long wait = 1L;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private Event event;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private WebElement webElement1;

    @Mock
    private WebElement webElement2;

    @Mock
    private WebElement webElement3;

    @Mock(extraInterfaces = TakesScreenshot.class)
    private WebDriver webDriver;

    @Mock
    private TestData testData;

    @Mock
    private Video video;

    @Mock
    private Pattern locatorPattern;

    @Mock
    private Matcher matcher;

    @Captor
    private ArgumentCaptor<Markup> markupArgumentCaptor;

    @InjectMocks
    private EventsListener eventsListener;

    @BeforeEach
    public void beforeEach() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(INFO);
    }

    @SneakyThrows
    private Path recordViewFrameForStubs() {
        final Path path = Files.createTempDirectory("reportsFolder");
        path.toFile().deleteOnExit();

        when(testData.getScreenshotFolderPath()).thenReturn(path);
        when(video.shouldRecord(path.resolve(anyString()).getFileName().toString())).thenReturn(true);
        when(((TakesScreenshot) webDriver).getScreenshotAs(BYTES)).thenReturn(new byte[]{1, 2, 3});

        return path;
    }

    @DisplayName("extractSelectorFrom should extract just the relevant info from the webElement")
    @ParameterizedTest(name = "with WebElement {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void extractSelectorFrom(final String fullWebElement, final String expected) {
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(expected);
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
    @DisplayName("extractSelectorFrom should extract just the relevant info from the webElement")
    public void extractSelectorFromNoMatch() {
        final String fullWebElement = "fullWebElement";

        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement1.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(false);
        assertEquals("", eventsListener.extractSelectorFrom(webElement1));
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
                "<code>id: message</code>",
                "<code>" + s + "</code>",
                "<code>null</code>",
                "<code>css selector: #gettotal -> tag name: button</code>",
                "<code>css selector: #get1-.total -> tag name: button</code>"
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

        assertEquals(expected, eventsListener.parse(args, "<code>%s</code>"));
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

        assertEquals(expected, eventsListener.parse(args));
    }

    @Test
    @DisplayName("recordVideoFrameFor should take a webdriver screenshot")
    public void recordVideoFrameFor() {
        final Path path = recordViewFrameForStubs();
        final Path screenshotPath = eventsListener.record(AUTO_AFTER);

        assertEquals(path, screenshotPath.getParent());
        assertThat(screenshotPath.getFileName().toString(), matchesPattern(UUID_REGEX));
    }

    @Test
    @DisplayName("recordVideoFrameFor should take a webdriver screenshot")
    public void recordVideoFrameForDisabled() throws IOException {
        final Path path = Files.createTempDirectory("reportsFolder");
        path.toFile().deleteOnExit();

        when(testData.getScreenshotFolderPath()).thenReturn(path);
        when(video.shouldRecord(path.resolve(anyString()).getFileName().toString())).thenReturn(false);

        assertNull(eventsListener.record(AUTO_AFTER));
    }

    @Test
    @DisplayName("OFF level: listen should not log the provided event")
    public void listenOff() {
        when(event.getLevel()).thenReturn(Level.OFF);

        eventsListener.listenTo(AUTO_BEFORE, event, arg);
        verify(event, never()).getMessage();
    }

    @Test
    @DisplayName("TRACE level: listen should log the provided event with its args")
    public void listenTrace() {
        recordViewFrameForStubs();

        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(TRACE);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(TRACE);
        when(event.getWait()).thenReturn(0L);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        eventsListener.listenTo(AUTO_BEFORE, event, arg);
        verify(extentTest).info(tagsMessage);
    }

    @Test
    @DisplayName("TRACE level off: listen should not log the provided event")
    public void listenTraceOff() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(OFF);
        when(event.getLevel()).thenReturn(TRACE);
        when(event.getWait()).thenReturn(wait);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        eventsListener.listenTo(AUTO_BEFORE, event, arg);
        verify(event, never()).getMessage();
        verify(extentTest, never()).info(tagsMessage);
    }

    @Test
    @DisplayName("DEBUG level: listen should log the provided event with its args")
    public void listenDebug() {
        recordViewFrameForStubs();

        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(DEBUG);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(DEBUG);
        when(event.getWait()).thenReturn(wait);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        eventsListener.listenTo(AUTO_BEFORE, event, arg);
        verify(extentTest).info(tagsMessage);
    }

    @Test
    @DisplayName("DEBUG level off: listen should not log the provided event")
    public void listenDebugOff() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(OFF);
        when(event.getLevel()).thenReturn(DEBUG);
        when(event.getWait()).thenReturn(wait);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        eventsListener.listenTo(AUTO_BEFORE, event, arg);
        verify(event, never()).getMessage();
        verify(extentTest, never()).info(tagsMessage);
    }

    @Test
    @DisplayName("INFO level: listen should log the provided event with its args")
    public void listenInfo() {
        recordViewFrameForStubs();

        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(INFO);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(INFO);
        when(event.getWait()).thenReturn(wait);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        eventsListener.listenTo(AUTO_BEFORE, event, arg);
        verify(extentTest).info(tagsMessage);
    }

    @Test
    @DisplayName("INFO level off: listen should not log the provided event")
    public void listenInfoOff() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(OFF);
        when(event.getLevel()).thenReturn(INFO);
        when(event.getWait()).thenReturn(wait);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        eventsListener.listenTo(AUTO_BEFORE, event, arg);
        verify(event, never()).getMessage();
        verify(extentTest, never()).info(tagsMessage);
    }

    @Test
    @DisplayName("WARN level: listen should log the provided event with its args")
    public void listenWarn() {
        recordViewFrameForStubs();

        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(WARN);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(WARN);
        when(event.getWait()).thenReturn(wait);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        eventsListener.listenTo(AUTO_BEFORE, event, arg);

        verify(extentTest).warning(markupArgumentCaptor.capture());
        final Markup markup = markupArgumentCaptor.getValue();
        assertEquals("<span class='badge white-text yellow'>" + tagsMessage + "</span>", markup.getMarkup());
    }

    @Test
    @DisplayName("WARN level off: listen should not log the provided event")
    public void listenWarnOff() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(OFF);
        when(event.getLevel()).thenReturn(WARN);
        when(event.getWait()).thenReturn(wait);

        eventsListener.listenTo(AUTO_BEFORE, event, arg);
        verify(event, never()).getMessage();
        verify(extentTest, never()).warning(markupArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Not matching level: listen should not log the provided event")
    public void listenDefault() {
        ((Logger) LoggerFactory.getLogger(EventsListener.class)).setLevel(ALL);
        when(event.getMessage()).thenReturn(message);
        when(event.getLevel()).thenReturn(ALL);

        eventsListener.listenTo(AUTO_BEFORE, event, arg);
        verify(store, never()).get(STATEFUL_EXTENT_TEST, ExtentTest.class);
        verify(extentTest, never()).warning(markupArgumentCaptor.capture());
    }
}
