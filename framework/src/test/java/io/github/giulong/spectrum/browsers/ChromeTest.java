package io.github.giulong.spectrum.browsers;

import io.github.giulong.spectrum.pojos.Configuration;
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
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.service.DriverService;

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
    @DisplayName("getDriverServiceBuilder should return a new instance of ChromeDriverService.Builder()")
    public void getDriverServiceBuilder() {
        MockedConstruction<ChromeDriverService.Builder> chromeDriverServiceMockedConstruction = mockConstruction(ChromeDriverService.Builder.class);

        final DriverService.Builder<ChromeDriverService, ChromeDriverService.Builder> driverServiceBuilder = chrome.getDriverServiceBuilder();
        assertEquals(chromeDriverServiceMockedConstruction.constructed().get(0), driverServiceBuilder);

        chromeDriverServiceMockedConstruction.close();
    }

    @Test
    @DisplayName("getWebDriverManager should return the chromedriver method")
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
}
