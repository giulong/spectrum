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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.Map;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Edge")
class EdgeTest {

    private MockedStatic<WebDriverManager> webDriverManagerMockedStatic;

    @Mock
    private EdgeOptions edgeOptions;

    @Mock
    private Configuration.WebDriver webDriverConfig;

    @Mock
    private Configuration.WebDriver.Edge edgeConfig;

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
    private Edge edge;

    @BeforeEach
    public void beforeEach() {
        webDriverManagerMockedStatic = mockStatic(WebDriverManager.class);
    }

    @AfterEach
    public void afterEach() {
        webDriverManagerMockedStatic.close();
    }

    @Test
    @DisplayName("takesPartialScreenshots should return true")
    public void takesPartialScreenshots() {
        assertTrue(edge.takesPartialScreenshots());
    }

    @Test
    @DisplayName("getWebDriverManager should return call the edgedriver method")
    public void getWebDriverManager() {
        when(WebDriverManager.edgedriver()).thenReturn(webDriverManager);
        assertEquals(webDriverManager, edge.getWebDriverManager());
    }

    @Test
    @DisplayName("buildCapabilitiesFrom should build an instance of Chrome based on the provided configuration")
    public void buildCapabilitiesFrom() {
        when(webDriverConfig.getEdge()).thenReturn(edgeConfig);
        when(seleniumLogs.getBrowser()).thenReturn(browserLevel);
        when(seleniumLogs.getDriver()).thenReturn(driverLevel);
        when(seleniumLogs.getPerformance()).thenReturn(performanceLevel);

        MockedConstruction<EdgeOptions> edgeOptionsMockedConstruction = mockConstruction(EdgeOptions.class);
        MockedConstruction<LoggingPreferences> loggingPreferencesMockedConstruction = mockConstruction(LoggingPreferences.class);

        edge.buildCapabilitiesFrom(webDriverConfig, seleniumLogs);
        final EdgeOptions edgeOptions = edgeOptionsMockedConstruction.constructed().get(0);
        verify(edgeOptions).setAcceptInsecureCerts(true);

        final LoggingPreferences loggingPreferences = loggingPreferencesMockedConstruction.constructed().get(0);
        verify(loggingPreferences).enable(BROWSER, browserLevel);
        verify(loggingPreferences).enable(DRIVER, driverLevel);
        verify(loggingPreferences).enable(PERFORMANCE, performanceLevel);
        verify(edgeOptions).setCapability(LOGGING_PREFS, loggingPreferences);

        edgeOptionsMockedConstruction.close();
        loggingPreferencesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildWebDriver should return a ChromeDriver with the capabilities")
    public void buildWebDriver() {
        edge.capabilities = edgeOptions;

        MockedConstruction<EdgeDriver> edgeDriverMockedConstruction = mockConstruction(EdgeDriver.class,
                (mock, context) -> assertEquals(edgeOptions, context.arguments().get(0)));
        final WebDriver actual = edge.buildWebDriver();
        assertEquals(edgeDriverMockedConstruction.constructed().get(0), actual);

        edgeDriverMockedConstruction.close();
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities")
    public void mergeGridCapabilitiesFrom() {
        edge.capabilities = edgeOptions;

        when(grid.getCapabilities()).thenReturn(Map.of("one", "value"));
        edge.mergeGridCapabilitiesFrom(grid);
        verify(edgeOptions).setCapability("one", "value");
    }
}
