package io.github.giulong.spectrum.browsers;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.utils.webdrivers.Environment;
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
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.support.ThreadGuard;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static io.github.giulong.spectrum.browsers.Browser.DRIVER_SERVICE_THREAD_LOCAL;
import static io.github.giulong.spectrum.browsers.Browser.WEB_DRIVER_THREAD_LOCAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Browser")
class BrowserTest {

    private MockedStatic<RemoteWebDriver> remoteWebDriverMockedStatic;
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
    private Configuration.SeleniumLogs seleniumLogs;

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

    @Mock
    private ChromeDriverService chromeDriverService;

    @Mock
    private ChromeDriverService.Builder chromeDriverServiceBuilder;

    @Mock
    private RemoteWebDriverBuilder webDriverBuilder;

    @InjectMocks
    private Chrome browser;

    @BeforeEach
    public void beforeEach() {
        WEB_DRIVER_THREAD_LOCAL.remove();
        DRIVER_SERVICE_THREAD_LOCAL.remove();

        remoteWebDriverMockedStatic = mockStatic(RemoteWebDriver.class);
        threadGuardMockedStatic = mockStatic(ThreadGuard.class);
        loggingPreferencesMockedConstruction = mockConstruction(LoggingPreferences.class);
    }

    @AfterEach
    public void afterEach() {
        remoteWebDriverMockedStatic.close();
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
        when(configuration.getSeleniumLogs()).thenReturn(seleniumLogs);
        when(seleniumLogs.getBrowser()).thenReturn(browserLevel);
        when(seleniumLogs.getDriver()).thenReturn(driverLevel);
        when(seleniumLogs.getPerformance()).thenReturn(performanceLevel);
        when(chromeConfig.getCapabilities()).thenReturn(Map.of("one", "value"));

        MockedConstruction<ChromeOptions> chromeOptionsMockedConstruction = mockConstruction(ChromeOptions.class, (mock, context) -> {
            when(RemoteWebDriver.builder()).thenReturn(webDriverBuilder);
            when(webDriverBuilder.oneOf(mock)).thenReturn(webDriverBuilder);
            when(webDriverBuilder.withDriverService(chromeDriverService)).thenReturn(webDriverBuilder);
            when(webDriverBuilder.build()).thenReturn(webDriver);
        });

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
        when(chromeDriverServiceBuilder.build()).thenReturn(chromeDriverService);

        when(ThreadGuard.protect(webDriver)).thenReturn(protectedWebDriver);

        MockedConstruction<ChromeDriverService.Builder> chromeDriverServiceMockedConstruction = mockConstruction(ChromeDriverService.Builder.class, (mock, context) -> {
            when(mock.withLogOutput(System.out)).thenReturn(chromeDriverServiceBuilder);
        });

        final WebDriver actual = browser.build(configuration);
        final WebDriver threadLocalWebDriver = WEB_DRIVER_THREAD_LOCAL.get();
        final DriverService threadLocalDriverService = DRIVER_SERVICE_THREAD_LOCAL.get();

        verify(chromeOptionsMockedConstruction.constructed().get(0)).setAcceptInsecureCerts(true);
        verify(environment).setupFrom(browser, webDriverBuilder);
        verify(environment).finalizeSetupOf(webDriver);
        assertEquals(protectedWebDriver, threadLocalWebDriver);
        assertEquals(protectedWebDriver, actual);
        assertEquals(chromeDriverService, threadLocalDriverService);

        chromeOptionsMockedConstruction.close();
        chromeDriverServiceMockedConstruction.close();
    }

    @Test
    @DisplayName("shutdown should quit the webDriver and close the driverService")
    public void shutdown() {
        WEB_DRIVER_THREAD_LOCAL.set(webDriver);
        DRIVER_SERVICE_THREAD_LOCAL.set(chromeDriverService);

        browser.shutdown();

        verify(webDriver).quit();
        verify(chromeDriverService).close();
    }
}