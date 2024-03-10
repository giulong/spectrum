package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.internals.EventsListener;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.util.regex.Pattern;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DriverResolver")
class DriverResolverTest {

    private static MockedStatic<EventsListener> eventsListenerMockedStatic;
    private static MockedStatic<Pattern> patternMockedStatic;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

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

    @Mock
    private WebDriver webDriver;

    @Mock
    private WebDriver decoratedWebDriver;

    @Mock
    private Configuration.Drivers driversConfiguration;

    @Mock
    private Configuration.Drivers.Events events;

    @Mock
    private EventsListener.EventsListenerBuilder eventsListenerBuilder;

    @Mock
    private EventsListener eventsListener;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private Configuration.Extent extentConfiguration;

    @Mock
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private Pattern pattern;

    @InjectMocks
    private DriverResolver driverResolver;

    @BeforeEach
    public void beforeEach() {
        eventsListenerMockedStatic = mockStatic(EventsListener.class);
        patternMockedStatic = mockStatic(Pattern.class);
    }

    @AfterEach
    public void afterEach() {
        eventsListenerMockedStatic.close();
        patternMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return the instance of the webdriver decorated with the default event listener")
    @SuppressWarnings("unchecked")
    public void resolveParameter() {
        final String locatorRegex = "locatorRegex";

        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(extensionContext.getRoot()).thenReturn(rootContext);
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

        when(store.get(EXTENT_TEST, ExtentTest.class)).thenReturn(extentTest);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(configuration.getVideo()).thenReturn(video);

        when(EventsListener.builder()).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.locatorPattern(pattern)).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.extentTest(extentTest)).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.video(video)).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.testData(testData)).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.driver(webDriver)).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.events(events)).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.build()).thenReturn(eventsListener);

        //noinspection rawtypes
        MockedConstruction<EventFiringDecorator> mockedConstruction = mockConstruction(EventFiringDecorator.class, (mock, context) -> {
            assertEquals(eventsListener, ((WebDriverListener[]) context.arguments().getFirst())[0]);

            when(mock.decorate(webDriver)).thenReturn(decoratedWebDriver);
        });
        WebDriver actual = driverResolver.resolveParameter(parameterContext, extensionContext);
        verify(store).put(DRIVER, decoratedWebDriver);

        assertEquals(decoratedWebDriver, actual);

        mockedConstruction.close();
    }
}
