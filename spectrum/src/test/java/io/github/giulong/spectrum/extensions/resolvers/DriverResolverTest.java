package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.internals.web_driver_listeners.AutoWaitWebDriverListener;
import io.github.giulong.spectrum.internals.web_driver_listeners.EventsWebDriverListener;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.video.Video;
import io.github.giulong.spectrum.utils.web_driver_events.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.decorators.Decorated;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.*;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class DriverResolverTest {

    private static MockedStatic<EventsWebDriverListener> eventsListenerMockedStatic;
    private static MockedStatic<AutoWaitWebDriverListener> autoWaitWebDriverListenerMockedStatic;
    private static MockedStatic<Pattern> patternMockedStatic;
    private static MockedStatic<LogConsumer> logConsumerMockedStatic;
    private static MockedStatic<HtmlReportConsumer> htmlReportConsumerMockedStatic;
    private static MockedStatic<VideoAutoScreenshotProducer> videoAutoScreenshotConsumerMockedStatic;
    private static MockedStatic<TestStepBuilderConsumer> testStepBuilderConsumerMockedStatic;
    private static MockedStatic<HighlightElementConsumer> highlightElementConsumerMockedStatic;

    @MockSingleton
    @SuppressWarnings("unused")
    private ContextManager contextManager;

    @MockSingleton
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Driver<?, ?, ?> driver;

    @Mock(extraInterfaces = {TakesScreenshot.class, JavascriptExecutor.class})
    private WebDriver webDriver;

    @Mock(extraInterfaces = Decorated.class)
    private WebDriver decoratedWebDriver;

    @Mock
    private Configuration.Drivers driversConfiguration;

    @Mock
    private Configuration.Drivers.Events events;

    @SuppressWarnings("rawtypes")
    @Mock
    private EventsWebDriverListener.EventsWebDriverListenerBuilder eventsWebDriverListenerBuilder;

    @Mock
    private EventsWebDriverListener eventsWebDriverListener;

    @SuppressWarnings("rawtypes")
    @Mock
    private AutoWaitWebDriverListener.AutoWaitWebDriverListenerBuilder autoWaitWebDriverListenerBuilder;

    @Mock
    private AutoWaitWebDriverListener autoWaitWebDriverListener;

    @Mock
    private Configuration.Drivers.Waits waits;

    @Mock
    private Configuration.Drivers.Waits.AutoWait autoWait;

    @Mock
    private Duration autoWaitTimeout;

    @SuppressWarnings("rawtypes")
    @Mock
    private LogConsumer.LogConsumerBuilder logConsumerBuilder;

    @Mock
    private LogConsumer logConsumer;

    @SuppressWarnings("rawtypes")
    @Mock
    private HtmlReportConsumer.HtmlReportConsumerBuilder htmlReportConsumerBuilder;

    @Mock
    private HtmlReportConsumer htmlReportConsumer;

    @SuppressWarnings("rawtypes")
    @Mock
    private VideoAutoScreenshotProducer.VideoAutoScreenshotProducerBuilder videoAutoScreenshotProducerBuilder;

    @Mock
    private VideoAutoScreenshotProducer videoAutoScreenshotProducer;

    @SuppressWarnings("rawtypes")
    @Mock
    private TestStepBuilderConsumer.TestStepBuilderConsumerBuilder testStepBuilderConsumerBuilder;

    @Mock
    private TestStepBuilderConsumer testStepBuilderConsumer;

    @SuppressWarnings("rawtypes")
    @Mock
    private HighlightElementConsumer.HighlightElementConsumerBuilder highlightElementConsumerBuilder;

    @Mock
    private HighlightElementConsumer highlightElementConsumer;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private Configuration.Extent extentConfiguration;

    @Mock
    private Configuration.Application application;

    @Mock
    private Configuration.Application.Highlight highlight;

    @Mock
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private Pattern pattern;

    @Captor
    private ArgumentCaptor<List<WebDriverEventConsumer>> consumersArgumentCaptor;

    @InjectMocks
    private DriverResolver driverResolver;

    @BeforeEach
    void beforeEach() {
        eventsListenerMockedStatic = mockStatic(EventsWebDriverListener.class);
        autoWaitWebDriverListenerMockedStatic = mockStatic(AutoWaitWebDriverListener.class);
        patternMockedStatic = mockStatic(Pattern.class);
        logConsumerMockedStatic = mockStatic(LogConsumer.class);
        htmlReportConsumerMockedStatic = mockStatic(HtmlReportConsumer.class);
        videoAutoScreenshotConsumerMockedStatic = mockStatic(VideoAutoScreenshotProducer.class);
        testStepBuilderConsumerMockedStatic = mockStatic(TestStepBuilderConsumer.class);
        highlightElementConsumerMockedStatic = mockStatic(HighlightElementConsumer.class);
    }

    @AfterEach
    void afterEach() {
        eventsListenerMockedStatic.close();
        autoWaitWebDriverListenerMockedStatic.close();
        patternMockedStatic.close();
        logConsumerMockedStatic.close();
        htmlReportConsumerMockedStatic.close();
        videoAutoScreenshotConsumerMockedStatic.close();
        testStepBuilderConsumerMockedStatic.close();
        highlightElementConsumerMockedStatic.close();
    }

    @SuppressWarnings("unchecked")
    private void stubs() {
        final String readJs = "readJs";
        final String locatorRegex = "locatorRegex";
        final String js = "js";

        when(context.getStore(GLOBAL)).thenReturn(store);
        when(context.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        doReturn(driver).when(runtime).getDriver();
        when(driver.build()).thenReturn(webDriver);
        when(configuration.getDrivers()).thenReturn(driversConfiguration);
        when(driversConfiguration.getEvents()).thenReturn(events);
        when(configuration.getExtent()).thenReturn(extentConfiguration);
        when(extentConfiguration.getLocatorRegex()).thenReturn(locatorRegex);
        when(Pattern.compile(locatorRegex)).thenReturn(pattern);

        when(configuration.getApplication()).thenReturn(application);
        when(application.getHighlight()).thenReturn(highlight);
        when(highlight.isEnabled()).thenReturn(false);

        when(driversConfiguration.getWaits()).thenReturn(waits);
        when(waits.getAuto()).thenReturn(autoWait);

        when(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)).thenReturn(statefulExtentTest);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(configuration.getVideo()).thenReturn(video);

        when(LogConsumer.builder()).thenReturn(logConsumerBuilder);
        when(logConsumerBuilder.enabled(true)).thenReturn(logConsumerBuilder);
        when(logConsumerBuilder.build()).thenReturn(logConsumer);

        when(HtmlReportConsumer.builder()).thenReturn(htmlReportConsumerBuilder);
        when(htmlReportConsumerBuilder.enabled(true)).thenReturn(htmlReportConsumerBuilder);
        when(htmlReportConsumerBuilder.statefulExtentTest(statefulExtentTest)).thenReturn(htmlReportConsumerBuilder);
        when(htmlReportConsumerBuilder.testData(testData)).thenReturn(htmlReportConsumerBuilder);
        when(htmlReportConsumerBuilder.video(video)).thenReturn(htmlReportConsumerBuilder);
        when(htmlReportConsumerBuilder.build()).thenReturn(htmlReportConsumer);

        when(VideoAutoScreenshotProducer.builder()).thenReturn(videoAutoScreenshotProducerBuilder);
        when(videoAutoScreenshotProducerBuilder.enabled(true)).thenReturn(videoAutoScreenshotProducerBuilder);
        when(videoAutoScreenshotProducerBuilder.video(video)).thenReturn(videoAutoScreenshotProducerBuilder);
        when(videoAutoScreenshotProducerBuilder.driver((TakesScreenshot) webDriver)).thenReturn(videoAutoScreenshotProducerBuilder);
        when(videoAutoScreenshotProducerBuilder.context(context)).thenReturn(videoAutoScreenshotProducerBuilder);
        when(videoAutoScreenshotProducerBuilder.build()).thenReturn(videoAutoScreenshotProducer);

        when(TestStepBuilderConsumer.builder()).thenReturn(testStepBuilderConsumerBuilder);
        when(testStepBuilderConsumerBuilder.enabled(true)).thenReturn(testStepBuilderConsumerBuilder);
        when(testStepBuilderConsumerBuilder.build()).thenReturn(testStepBuilderConsumer);

        when(highlight.isEnabled()).thenReturn(true);
        when(highlight.getJs()).thenReturn(js);
        when(fileUtils.read(js)).thenReturn(readJs);
        when(HighlightElementConsumer.builder()).thenReturn(highlightElementConsumerBuilder);
        when(highlightElementConsumerBuilder.enabled(true)).thenReturn(highlightElementConsumerBuilder);
        when(highlightElementConsumerBuilder.driver((JavascriptExecutor) webDriver)).thenReturn(highlightElementConsumerBuilder);
        when(highlightElementConsumerBuilder.js(readJs)).thenReturn(highlightElementConsumerBuilder);
        when(highlightElementConsumerBuilder.build()).thenReturn(highlightElementConsumer);

        when(EventsWebDriverListener.builder()).thenReturn(eventsWebDriverListenerBuilder);
        when(eventsWebDriverListenerBuilder.locatorPattern(pattern)).thenReturn(eventsWebDriverListenerBuilder);
        when(eventsWebDriverListenerBuilder.events(events)).thenReturn(eventsWebDriverListenerBuilder);
        when(eventsWebDriverListenerBuilder.consumers(consumersArgumentCaptor.capture())).thenReturn(eventsWebDriverListenerBuilder);
        when(eventsWebDriverListenerBuilder.build()).thenReturn(eventsWebDriverListener);
    }

    private void verificationsFor(final WebDriver actual) {
        verify(store).put(TEST_STEP_BUILDER_CONSUMER, testStepBuilderConsumer);
        verify(store).put(DRIVER, actual);
        verify(store).put(ORIGINAL_DRIVER, webDriver);

        verify(contextManager).put(context, TEST_STEP_BUILDER_CONSUMER, testStepBuilderConsumer);
        verify(contextManager).put(context, DRIVER, actual);
        verify(contextManager).put(context, ORIGINAL_DRIVER, webDriver);

        assertEquals(decoratedWebDriver, actual);
        assertEquals(List.of(logConsumer, htmlReportConsumer, videoAutoScreenshotProducer, testStepBuilderConsumer, highlightElementConsumer), consumersArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("resolveParameter should return the instance of the webDriver decorated with the default event listener")
    @SuppressWarnings("unchecked")
    void resolveParameter() {
        stubs();
        when(autoWait.isEnabled()).thenReturn(false);

        //noinspection rawtypes
        final MockedConstruction<EventFiringDecorator> mockedConstruction = mockConstruction(EventFiringDecorator.class, (mock, executionContext) -> {
            assertArrayEquals(List.of(eventsWebDriverListener).toArray(), (WebDriverListener[]) executionContext.arguments().getFirst());

            when(mock.decorate(webDriver)).thenReturn(decoratedWebDriver);
            when(((Decorated<WebDriver>) decoratedWebDriver).getOriginal()).thenReturn(webDriver);
        });

        final WebDriver actual = driverResolver.resolveParameter(parameterContext, context);
        verificationsFor(actual);
        verify(autoWait, never()).getTimeout();

        mockedConstruction.close();
    }

    @Test
    @DisplayName("resolveParameter should return the instance of the webDriver decorated with the default event listener")
    @SuppressWarnings("unchecked")
    void resolveParameterAutoWait() {
        stubs();
        when(autoWait.isEnabled()).thenReturn(true);
        when(autoWait.getTimeout()).thenReturn(autoWaitTimeout);
        when(AutoWaitWebDriverListener.builder()).thenReturn(autoWaitWebDriverListenerBuilder);
        when(autoWaitWebDriverListenerBuilder.locatorPattern(pattern)).thenReturn(autoWaitWebDriverListenerBuilder);
        when(autoWaitWebDriverListenerBuilder.build()).thenReturn(autoWaitWebDriverListener);

        final MockedConstruction<Actions> actionsMockedConstruction = mockConstruction(Actions.class, (mock, executionContext) -> {
            assertEquals(webDriver, executionContext.arguments().getFirst());

            when(autoWaitWebDriverListenerBuilder.actions(mock)).thenReturn(autoWaitWebDriverListenerBuilder);
        });

        final MockedConstruction<WebDriverWait> webDriverWaitMockedConstruction = mockConstruction(WebDriverWait.class, (mock, executionContext) -> {
            assertEquals(webDriver, executionContext.arguments().getFirst());
            assertEquals(autoWaitTimeout, executionContext.arguments().get(1));

            when(autoWaitWebDriverListenerBuilder.webDriverWait(mock)).thenReturn(autoWaitWebDriverListenerBuilder);
        });

        //noinspection rawtypes
        final MockedConstruction<EventFiringDecorator> mockedConstruction = mockConstruction(EventFiringDecorator.class, (mock, executionContext) -> {
            assertArrayEquals(List.of(autoWaitWebDriverListener, eventsWebDriverListener).toArray(), (WebDriverListener[]) executionContext.arguments().getFirst());

            when(mock.decorate(webDriver)).thenReturn(decoratedWebDriver);
            when(((Decorated<WebDriver>) decoratedWebDriver).getOriginal()).thenReturn(webDriver);
        });

        final WebDriver actual = driverResolver.resolveParameter(parameterContext, context);
        verificationsFor(actual);

        actionsMockedConstruction.close();
        mockedConstruction.close();
        webDriverWaitMockedConstruction.close();
    }
}
