package io.github.giulong.spectrum.browsers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.environments.Environment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ThreadGuard;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static io.github.giulong.spectrum.browsers.Browser.WEB_DRIVER_THREAD_LOCAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Browser")
class BrowserTest {

    private MockedStatic<ThreadGuard> threadGuardMockedStatic;

    private MockedConstruction<LoggingPreferences> loggingPreferencesMockedConstruction;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.WebDriver webDriverConfig;

    @Mock
    private Configuration.WebDriver.Chrome chromeConfig;

    @Mock
    private Level browserLevel;

    @Mock
    private Level driverLevel;

    @Mock
    private Level performanceLevel;

    @Mock
    private Configuration.WebDriver.Logs logs;

    @Mock
    private WebDriver webDriver;

    @Mock
    private WebDriver protectedWebDriver;

    @Mock
    private Configuration.WebDriver.Waits waits;

    @Mock
    private WebDriver.Options options;

    @Mock
    private WebDriver.Timeouts timeouts;

    @Mock
    private Duration implicitDuration;

    @Mock
    private Duration pageLoadDuration;

    @Mock
    private Duration scriptDuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Environment environment;

    @InjectMocks
    private Chrome browser;

    @BeforeEach
    public void beforeEach() {
        WEB_DRIVER_THREAD_LOCAL.remove();

        threadGuardMockedStatic = mockStatic(ThreadGuard.class);
        loggingPreferencesMockedConstruction = mockConstruction(LoggingPreferences.class);

        Reflections.setField("configuration", browser, configuration);
    }

    @AfterEach
    public void afterEach() {
        threadGuardMockedStatic.close();
        loggingPreferencesMockedConstruction.close();
    }

    @Test
    @DisplayName("build should return the instance of the requested webdriver")
    public void build() {
        // buildCapabilitiesFrom stubs
        final List<String> arguments = List.of("args");
        when(configuration.getWebDriver()).thenReturn(webDriverConfig);
        when(webDriverConfig.getChrome()).thenReturn(chromeConfig);
        when(chromeConfig.getArgs()).thenReturn(arguments);
        when(webDriverConfig.getLogs()).thenReturn(logs);
        when(logs.getBrowser()).thenReturn(browserLevel);
        when(logs.getDriver()).thenReturn(driverLevel);
        when(logs.getPerformance()).thenReturn(performanceLevel);
        when(chromeConfig.getCapabilities()).thenReturn(Map.of("one", "value"));

        MockedConstruction<ChromeOptions> chromeOptionsMockedConstruction = mockConstruction(ChromeOptions.class);

        when(waits.getImplicit()).thenReturn(implicitDuration);
        when(waits.getPageLoadTimeout()).thenReturn(pageLoadDuration);
        when(waits.getScriptTimeout()).thenReturn(scriptDuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getEnvironment()).thenReturn(environment);
        when(webDriver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);
        when(webDriverConfig.getWaits()).thenReturn(waits);
        when(timeouts.implicitlyWait(implicitDuration)).thenReturn(timeouts);
        when(timeouts.pageLoadTimeout(pageLoadDuration)).thenReturn(timeouts);
        when(timeouts.scriptTimeout(scriptDuration)).thenReturn(timeouts);
        when(environment.setupFor(browser)).thenReturn(webDriver);

        when(ThreadGuard.protect(webDriver)).thenReturn(protectedWebDriver);

        final WebDriver actual = browser.build();
        final WebDriver threadLocalWebDriver = WEB_DRIVER_THREAD_LOCAL.get();

        assertEquals(protectedWebDriver, threadLocalWebDriver);
        assertEquals(protectedWebDriver, actual);

        chromeOptionsMockedConstruction.close();
    }

    @Test
    @DisplayName("configureWaitsOf should configure all the waits by default")
    public void configureWaitsOf() {
        when(waits.getImplicit()).thenReturn(implicitDuration);
        when(waits.getPageLoadTimeout()).thenReturn(pageLoadDuration);
        when(waits.getScriptTimeout()).thenReturn(scriptDuration);

        when(webDriver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);
        when(timeouts.implicitlyWait(implicitDuration)).thenReturn(timeouts);
        when(timeouts.pageLoadTimeout(pageLoadDuration)).thenReturn(timeouts);

        browser.configureWaitsOf(webDriver, waits);

        verify(timeouts).scriptTimeout(scriptDuration);
    }

    @Test
    @DisplayName("shutdown should quit the webDriver")
    public void shutdown() {
        WEB_DRIVER_THREAD_LOCAL.set(webDriver);

        browser.shutdown();

        verify(webDriver).quit();
    }
}
