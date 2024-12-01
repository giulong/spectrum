package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.internals.SpectrumWebDriverListener;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import io.github.giulong.spectrum.utils.web_driver_events.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.TEST_STEP_BUILDER_CONSUMER;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class DriverResolverTest {

    private static MockedStatic<SpectrumWebDriverListener> eventsListenerMockedStatic;
    private static MockedStatic<Pattern> patternMockedStatic;
    private static MockedStatic<LogConsumer> logConsumerMockedStatic;
    private static MockedStatic<HtmlReportConsumer> htmlReportConsumerMockedStatic;
    private static MockedStatic<ScreenshotConsumer> screenshotConsumerMockedStatic;
    private static MockedStatic<TestStepBuilderConsumer> testStepBuilderConsumerMockedStatic;

    @Mock
    private ContextManager contextManager;

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

    @Mock(extraInterfaces = TakesScreenshot.class)
    private WebDriver webDriver;

    @Mock
    private WebDriver decoratedWebDriver;

    @Mock
    private Configuration.Drivers driversConfiguration;

    @Mock
    private Configuration.Drivers.Events events;

    @Mock
    private SpectrumWebDriverListener.SpectrumWebDriverListenerBuilder spectrumWebDriverListenerBuilder;

    @Mock
    private SpectrumWebDriverListener spectrumWebDriverListener;

    @Mock
    private LogConsumer.LogConsumerBuilder logConsumerBuilder;

    @Mock
    private LogConsumer logConsumer;

    @Mock
    private HtmlReportConsumer.HtmlReportConsumerBuilder htmlReportConsumerBuilder;

    @Mock
    private HtmlReportConsumer htmlReportConsumer;

    @Mock
    private ScreenshotConsumer.ScreenshotConsumerBuilder screenshotConsumerBuilder;

    @Mock
    private ScreenshotConsumer screenshotConsumer;

    @Mock
    private TestStepBuilderConsumer.TestStepBuilderConsumerBuilder testStepBuilderConsumerBuilder;

    @Mock
    private TestStepBuilderConsumer testStepBuilderConsumer;

    @Mock
    private TestContext testContext;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private Configuration.Extent extentConfiguration;

    @Mock
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private Pattern pattern;

    @Captor
    private ArgumentCaptor<List<Consumer<WebDriverEvent>>> consumersArgumentCaptor;

    @InjectMocks
    private DriverResolver driverResolver;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("contextManager", driverResolver, contextManager);

        eventsListenerMockedStatic = mockStatic(SpectrumWebDriverListener.class);
        patternMockedStatic = mockStatic(Pattern.class);
        logConsumerMockedStatic = mockStatic(LogConsumer.class);
        htmlReportConsumerMockedStatic = mockStatic(HtmlReportConsumer.class);
        screenshotConsumerMockedStatic = mockStatic(ScreenshotConsumer.class);
        testStepBuilderConsumerMockedStatic = mockStatic(TestStepBuilderConsumer.class);
    }

    @AfterEach
    void afterEach() {
        eventsListenerMockedStatic.close();
        patternMockedStatic.close();
        logConsumerMockedStatic.close();
        htmlReportConsumerMockedStatic.close();
        screenshotConsumerMockedStatic.close();
        testStepBuilderConsumerMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return the instance of the webDriver decorated with the default event listener")
    @SuppressWarnings("unchecked")
    void resolveParameter() {
        final String locatorRegex = "locatorRegex";

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

        when(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)).thenReturn(statefulExtentTest);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(configuration.getVideo()).thenReturn(video);

        when(contextManager.get(context)).thenReturn(testContext);

        when(LogConsumer.builder()).thenReturn(logConsumerBuilder);
        when(logConsumerBuilder.build()).thenReturn(logConsumer);

        when(HtmlReportConsumer.builder()).thenReturn(htmlReportConsumerBuilder);
        when(htmlReportConsumerBuilder.statefulExtentTest(statefulExtentTest)).thenReturn(htmlReportConsumerBuilder);
        when(htmlReportConsumerBuilder.build()).thenReturn(htmlReportConsumer);

        when(ScreenshotConsumer.builder()).thenReturn(screenshotConsumerBuilder);
        when(screenshotConsumerBuilder.driver((TakesScreenshot) webDriver)).thenReturn(screenshotConsumerBuilder);
        when(screenshotConsumerBuilder.testData(testData)).thenReturn(screenshotConsumerBuilder);
        when(screenshotConsumerBuilder.video(video)).thenReturn(screenshotConsumerBuilder);
        when(screenshotConsumerBuilder.build()).thenReturn(screenshotConsumer);

        when(TestStepBuilderConsumer.builder()).thenReturn(testStepBuilderConsumerBuilder);
        when(testStepBuilderConsumerBuilder.build()).thenReturn(testStepBuilderConsumer);

        when(SpectrumWebDriverListener.builder()).thenReturn(spectrumWebDriverListenerBuilder);
        when(spectrumWebDriverListenerBuilder.locatorPattern(pattern)).thenReturn(spectrumWebDriverListenerBuilder);
        when(spectrumWebDriverListenerBuilder.events(events)).thenReturn(spectrumWebDriverListenerBuilder);
        when(spectrumWebDriverListenerBuilder.consumers(consumersArgumentCaptor.capture())).thenReturn(spectrumWebDriverListenerBuilder);
        when(spectrumWebDriverListenerBuilder.testContext(testContext)).thenReturn(spectrumWebDriverListenerBuilder);
        when(spectrumWebDriverListenerBuilder.build()).thenReturn(spectrumWebDriverListener);

        //noinspection rawtypes
        final MockedConstruction<EventFiringDecorator> mockedConstruction = mockConstruction(EventFiringDecorator.class, (mock, executionContext) -> {
            assertEquals(spectrumWebDriverListener, ((WebDriverListener[]) executionContext.arguments().getFirst())[0]);

            when(mock.decorate(webDriver)).thenReturn(decoratedWebDriver);
        });

        final WebDriver actual = driverResolver.resolveParameter(parameterContext, context);
        verify(store).put(TEST_STEP_BUILDER_CONSUMER, testStepBuilderConsumer);
        verify(store).put(DRIVER, actual);

        verify(contextManager).put(context, TEST_STEP_BUILDER_CONSUMER, testStepBuilderConsumer);
        verify(contextManager).put(context, DRIVER, actual);

        assertEquals(decoratedWebDriver, actual);
        assertEquals(List.of(logConsumer, htmlReportConsumer, screenshotConsumer, testStepBuilderConsumer), consumersArgumentCaptor.getValue());

        mockedConstruction.close();
    }
}
