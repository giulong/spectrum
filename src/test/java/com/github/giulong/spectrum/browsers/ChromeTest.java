package com.github.giulong.spectrum.browsers;

import com.github.giulong.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
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
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Chrome")
class ChromeTest {

    private MockedStatic<WebDriverManager> webDriverManagerMockedStatic;

    @Mock
    private ChromeOptions chromeOptions;

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

    @InjectMocks
    private Chrome chrome;

    @BeforeEach
    public void beforeEach() {
        webDriverManagerMockedStatic = mockStatic(WebDriverManager.class);
    }

    @AfterEach
    public void afterEach() {
        webDriverManagerMockedStatic.close();
    }

    @Test
    @DisplayName("getWebDriverManager should return call the chromedriver method")
    public void getWebDriverManager() {
        when(WebDriverManager.chromedriver()).thenReturn(webDriverManager);
        assertEquals(webDriverManager, chrome.getWebDriverManager());
    }

    @Test
    @DisplayName("buildCapabilitiesFrom should build an instance of Chrome based on the provided configuration")
    public void buildCapabilitiesFrom() {
        final List<String> arguments = List.of("args");

        when(webDriverConfig.getChrome()).thenReturn(chromeConfig);
        when(chromeConfig.getArgs()).thenReturn(arguments);
        when(seleniumLogs.getBrowser()).thenReturn(browserLevel);
        when(seleniumLogs.getDriver()).thenReturn(driverLevel);
        when(seleniumLogs.getPerformance()).thenReturn(performanceLevel);
        when(chromeConfig.getCapabilities()).thenReturn(Map.of("one", "value"));

        MockedConstruction<ChromeOptions> chromeOptionsMockedConstruction = mockConstruction(ChromeOptions.class);
        MockedConstruction<LoggingPreferences> loggingPreferencesMockedConstruction = mockConstruction(LoggingPreferences.class);

        chrome.buildCapabilitiesFrom(webDriverConfig, seleniumLogs);
        final ChromeOptions chromeOptions = chromeOptionsMockedConstruction.constructed().get(0);
        verify(chromeOptions).addArguments(arguments);

        final LoggingPreferences loggingPreferences = loggingPreferencesMockedConstruction.constructed().get(0);
        verify(loggingPreferences).enable(BROWSER, browserLevel);
        verify(loggingPreferences).enable(DRIVER, driverLevel);
        verify(loggingPreferences).enable(PERFORMANCE, performanceLevel);
        verify(chromeOptions).setCapability(LOGGING_PREFS, loggingPreferences);

        verify(chromeOptions).setExperimentalOption("one", "value");

        chromeOptionsMockedConstruction.close();
        loggingPreferencesMockedConstruction.close();
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities")
    public void mergeGridCapabilitiesFrom() {
        chrome.capabilities = chromeOptions;

        when(grid.getCapabilities()).thenReturn(Map.of("one", "value"));
        chrome.mergeGridCapabilitiesFrom(grid);
        verify(chromeOptions).setCapability("one", "value");
    }
}
