package com.giuliolongfils.spectrum.browsers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Browser")
class BrowserTest {

    private MockedStatic<WebDriverManager> webDriverManagerMockedStatic;
    private MockedStatic<RemoteWebDriver> remoteWebDriverMockedStatic;
    private MockedConstruction<ChromeOptions> chromeOptionsMockedConstruction;
    private MockedConstruction<LoggingPreferences> loggingPreferencesMockedConstruction;

    private final String driversPath = "driversPath";

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
    private Configuration.WebDriver.Grid grid;

    @Mock
    private WebDriverManager webDriverManager;

    @Mock
    private WebDriver webDriver;

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

    @Captor
    private ArgumentCaptor<ChromeOptions> chromeOptionsArgumentCaptor;

    @InjectMocks
    private Chrome browser;

    @BeforeEach
    public void beforeEach() {
        webDriverManagerMockedStatic = mockStatic(WebDriverManager.class);
        remoteWebDriverMockedStatic = mockStatic(RemoteWebDriver.class);
        chromeOptionsMockedConstruction = mockConstruction(ChromeOptions.class);
        loggingPreferencesMockedConstruction = mockConstruction(LoggingPreferences.class);
    }

    @AfterEach
    public void afterEach() {
        webDriverManagerMockedStatic.close();
        remoteWebDriverMockedStatic.close();
        chromeOptionsMockedConstruction.close();
        loggingPreferencesMockedConstruction.close();
    }

    @Test
    @DisplayName("build should return the instance of the requested webdriver")
    public void build() {
        // buildCapabilitiesFrom stubs
        final List<String> arguments = List.of("args");
        when(configuration.getWebDriver()).thenReturn(webDriverConfig);
        when(webDriverConfig.getChrome()).thenReturn(chromeConfig);
        when(chromeConfig.getArguments()).thenReturn(arguments);
        when(configuration.getSeleniumLogs()).thenReturn(seleniumLogs);
        when(seleniumLogs.getBrowser()).thenReturn(browserLevel);
        when(seleniumLogs.getDriver()).thenReturn(driverLevel);
        when(seleniumLogs.getPerformance()).thenReturn(performanceLevel);
        when(chromeConfig.getExperimentalOptions()).thenReturn(Map.of("one", "value"));

        when(waits.getImplicit()).thenReturn(implicitDuration);
        when(waits.getPageLoadTimeout()).thenReturn(pageLoadDuration);
        when(waits.getScriptTimeout()).thenReturn(scriptDuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.isGrid()).thenReturn(false);
        when(runtime.isDownloadWebDriver()).thenReturn(false);
        when(runtime.getDriversPath()).thenReturn(driversPath);
        when(options.timeouts()).thenReturn(timeouts);
        when(webDriverConfig.getWaits()).thenReturn(waits);
        when(timeouts.implicitlyWait(implicitDuration)).thenReturn(timeouts);
        when(timeouts.pageLoadTimeout(pageLoadDuration)).thenReturn(timeouts);
        when(timeouts.scriptTimeout(scriptDuration)).thenReturn(timeouts);

        try (MockedConstruction<ChromeDriver> chromeDriverMockedConstruction = mockConstruction(ChromeDriver.class, (mock, context) -> {
            when(mock.manage()).thenReturn(options);
        })) {
            final WebDriver actual = browser.build(configuration);

            // buildCapabilitiesFrom verifications
            final ChromeOptions chromeOptions = chromeOptionsMockedConstruction.constructed().get(0);
            verify(chromeOptions).addArguments(arguments);
            verify(chromeOptions).setAcceptInsecureCerts(true);
            final LoggingPreferences loggingPreferences = loggingPreferencesMockedConstruction.constructed().get(0);
            verify(loggingPreferences).enable(BROWSER, browserLevel);
            verify(loggingPreferences).enable(DRIVER, driverLevel);
            verify(loggingPreferences).enable(PERFORMANCE, performanceLevel);
            verify(chromeOptions).setCapability(LOGGING_PREFS, loggingPreferences);
            verify(chromeOptions).setExperimentalOption("one", "value");

            assertEquals(chromeDriverMockedConstruction.constructed().get(0), actual);
            assertEquals(Paths.get(driversPath, "chromedriver.exe").toString(), System.getProperty("webDriver.chrome.driver"));
        }
    }

    @Test
    @DisplayName("build should return a remote webdriver if grid parameter is set to true")
    public void buildGrid() throws MalformedURLException {
        // buildCapabilitiesFrom stubs
        final List<String> arguments = List.of("args");
        when(configuration.getWebDriver()).thenReturn(webDriverConfig);
        when(webDriverConfig.getChrome()).thenReturn(chromeConfig);
        when(chromeConfig.getArguments()).thenReturn(arguments);
        when(configuration.getSeleniumLogs()).thenReturn(seleniumLogs);
        when(seleniumLogs.getBrowser()).thenReturn(browserLevel);
        when(seleniumLogs.getDriver()).thenReturn(driverLevel);
        when(seleniumLogs.getPerformance()).thenReturn(performanceLevel);
        when(chromeConfig.getExperimentalOptions()).thenReturn(Map.of("one", "value"));

        // setTimeouts
        when(webDriver.manage()).thenReturn(options);
        when(waits.getImplicit()).thenReturn(implicitDuration);
        when(waits.getPageLoadTimeout()).thenReturn(pageLoadDuration);
        when(waits.getScriptTimeout()).thenReturn(scriptDuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.isGrid()).thenReturn(true);
        when(options.timeouts()).thenReturn(timeouts);
        when(webDriverConfig.getWaits()).thenReturn(waits);
        when(timeouts.implicitlyWait(implicitDuration)).thenReturn(timeouts);
        when(timeouts.pageLoadTimeout(pageLoadDuration)).thenReturn(timeouts);
        when(timeouts.scriptTimeout(scriptDuration)).thenReturn(timeouts);

        final URL url = URI.create("http://url").toURL();
        when(grid.getUrl()).thenReturn(url);
        RemoteWebDriverBuilder remoteWebDriverBuilder = mock(RemoteWebDriverBuilder.class);
        when(RemoteWebDriver.builder()).thenReturn(remoteWebDriverBuilder);
        when(remoteWebDriverBuilder.oneOf(chromeOptionsArgumentCaptor.capture())).thenReturn(remoteWebDriverBuilder);
        when(remoteWebDriverBuilder.address(url)).thenReturn(remoteWebDriverBuilder);
        when(remoteWebDriverBuilder.build()).thenReturn(webDriver);

        when(webDriverConfig.getGrid()).thenReturn(grid);

        // mergeGridCapabilities
        when(grid.getCapabilities()).thenReturn(Map.of("one", "value"));

        final WebDriver actual = browser.build(configuration);

        // buildCapabilitiesFrom verifications
        final ChromeOptions chromeOptions = chromeOptionsMockedConstruction.constructed().get(0);
        verify(chromeOptions).addArguments(arguments);
        verify(chromeOptions).setAcceptInsecureCerts(true);
        final LoggingPreferences loggingPreferences = loggingPreferencesMockedConstruction.constructed().get(0);
        verify(loggingPreferences).enable(BROWSER, browserLevel);
        verify(loggingPreferences).enable(DRIVER, driverLevel);
        verify(loggingPreferences).enable(PERFORMANCE, performanceLevel);
        verify(chromeOptions).setCapability(LOGGING_PREFS, loggingPreferences);
        verify(chromeOptions).setExperimentalOption("one", "value");

        // mergeGridCapabilities verification
        verify(chromeOptions).setCapability("one", "value");

        assertEquals(chromeOptions, chromeOptionsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("build should download the webdriver")
    public void buildDownloadWebDriver() {
        // buildCapabilitiesFrom stubs
        final List<String> arguments = List.of("args");
        when(configuration.getWebDriver()).thenReturn(webDriverConfig);
        when(webDriverConfig.getChrome()).thenReturn(chromeConfig);
        when(chromeConfig.getArguments()).thenReturn(arguments);
        when(configuration.getSeleniumLogs()).thenReturn(seleniumLogs);
        when(seleniumLogs.getBrowser()).thenReturn(browserLevel);
        when(seleniumLogs.getDriver()).thenReturn(driverLevel);
        when(seleniumLogs.getPerformance()).thenReturn(performanceLevel);
        when(chromeConfig.getExperimentalOptions()).thenReturn(Map.of("one", "value"));

        // setTimeouts
        when(waits.getImplicit()).thenReturn(implicitDuration);
        when(waits.getPageLoadTimeout()).thenReturn(pageLoadDuration);
        when(waits.getScriptTimeout()).thenReturn(scriptDuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.isGrid()).thenReturn(false);
        when(runtime.isDownloadWebDriver()).thenReturn(true);
        when(runtime.getDriversPath()).thenReturn(driversPath);
        when(options.timeouts()).thenReturn(timeouts);
        when(webDriverConfig.getWaits()).thenReturn(waits);
        when(timeouts.implicitlyWait(implicitDuration)).thenReturn(timeouts);
        when(timeouts.pageLoadTimeout(pageLoadDuration)).thenReturn(timeouts);
        when(timeouts.scriptTimeout(scriptDuration)).thenReturn(timeouts);

        when(WebDriverManager.chromedriver()).thenReturn(webDriverManager);
        when(webDriverManager.avoidOutputTree()).thenReturn(webDriverManager);
        when(webDriverManager.cachePath(driversPath)).thenReturn(webDriverManager);

        try (MockedConstruction<ChromeDriver> chromeDriverMockedConstruction = mockConstruction(ChromeDriver.class, (mock, context) -> {
            when(mock.manage()).thenReturn(options);
        })) {
            final WebDriver actual = browser.build(configuration);

            // buildCapabilitiesFrom verifications
            final ChromeOptions chromeOptions = chromeOptionsMockedConstruction.constructed().get(0);
            verify(chromeOptions).addArguments(arguments);
            verify(chromeOptions).setAcceptInsecureCerts(true);
            final LoggingPreferences loggingPreferences = loggingPreferencesMockedConstruction.constructed().get(0);
            verify(loggingPreferences).enable(BROWSER, browserLevel);
            verify(loggingPreferences).enable(DRIVER, driverLevel);
            verify(loggingPreferences).enable(PERFORMANCE, performanceLevel);
            verify(chromeOptions).setCapability(LOGGING_PREFS, loggingPreferences);
            verify(chromeOptions).setExperimentalOption("one", "value");

            assertEquals(chromeDriverMockedConstruction.constructed().get(0), actual);
            verify(webDriverManager).setup();
        }
    }

    @Test
    @DisplayName("build should download the webdriver and docker image")
    public void buildDownloadWebDriverDocker() {
        // buildCapabilitiesFrom stubs
        final List<String> arguments = List.of("args");
        when(configuration.getWebDriver()).thenReturn(webDriverConfig);
        when(webDriverConfig.getChrome()).thenReturn(chromeConfig);
        when(chromeConfig.getArguments()).thenReturn(arguments);
        when(configuration.getSeleniumLogs()).thenReturn(seleniumLogs);
        when(seleniumLogs.getBrowser()).thenReturn(browserLevel);
        when(seleniumLogs.getDriver()).thenReturn(driverLevel);
        when(seleniumLogs.getPerformance()).thenReturn(performanceLevel);
        when(chromeConfig.getExperimentalOptions()).thenReturn(Map.of("one", "value"));

        // setTimeouts
        when(waits.getImplicit()).thenReturn(implicitDuration);
        when(waits.getPageLoadTimeout()).thenReturn(pageLoadDuration);
        when(waits.getScriptTimeout()).thenReturn(scriptDuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.isGrid()).thenReturn(false);
        when(runtime.isDownloadWebDriver()).thenReturn(true);
        when(runtime.getDriversPath()).thenReturn(driversPath);
        when(options.timeouts()).thenReturn(timeouts);
        when(webDriverConfig.getWaits()).thenReturn(waits);
        when(timeouts.implicitlyWait(implicitDuration)).thenReturn(timeouts);
        when(timeouts.pageLoadTimeout(pageLoadDuration)).thenReturn(timeouts);
        when(timeouts.scriptTimeout(scriptDuration)).thenReturn(timeouts);

        when(WebDriverManager.chromedriver()).thenReturn(webDriverManager);
        when(webDriverManager.avoidOutputTree()).thenReturn(webDriverManager);
        when(webDriverManager.cachePath(driversPath)).thenReturn(webDriverManager);

        when(runtime.isDocker()).thenReturn(true);
        when(webDriverManager.browserInDocker()).thenReturn(webDriverManager);

        try (MockedConstruction<ChromeDriver> chromeDriverMockedConstruction = mockConstruction(ChromeDriver.class, (mock, context) -> {
            when(mock.manage()).thenReturn(options);
        })) {
            final WebDriver actual = browser.build(configuration);

            // buildCapabilitiesFrom verifications
            final ChromeOptions chromeOptions = chromeOptionsMockedConstruction.constructed().get(0);
            verify(chromeOptions).addArguments(arguments);
            verify(chromeOptions).setAcceptInsecureCerts(true);
            final LoggingPreferences loggingPreferences = loggingPreferencesMockedConstruction.constructed().get(0);
            verify(loggingPreferences).enable(BROWSER, browserLevel);
            verify(loggingPreferences).enable(DRIVER, driverLevel);
            verify(loggingPreferences).enable(PERFORMANCE, performanceLevel);
            verify(chromeOptions).setCapability(LOGGING_PREFS, loggingPreferences);
            verify(chromeOptions).setExperimentalOption("one", "value");

            assertEquals(chromeDriverMockedConstruction.constructed().get(0), actual);
            verify(webDriverManager).setup();
        }
    }

    @Test
    @DisplayName("setTimeouts should set the values provided in configuration")
    public void setTimeouts() {
        when(waits.getImplicit()).thenReturn(implicitDuration);
        when(waits.getPageLoadTimeout()).thenReturn(pageLoadDuration);
        when(waits.getScriptTimeout()).thenReturn(scriptDuration);

        when(webDriver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);
        when(timeouts.implicitlyWait(implicitDuration)).thenReturn(timeouts);
        when(timeouts.pageLoadTimeout(pageLoadDuration)).thenReturn(timeouts);
        when(timeouts.scriptTimeout(scriptDuration)).thenReturn(timeouts);

        assertEquals(webDriver, browser.setTimeouts(webDriver, waits));
    }
}