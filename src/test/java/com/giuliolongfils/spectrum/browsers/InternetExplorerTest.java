package com.giuliolongfils.spectrum.browsers;

import com.giuliolongfils.spectrum.pojos.Configuration;
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
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InternetExplorer")
class InternetExplorerTest {

    private MockedStatic<WebDriverManager> webDriverManagerMockedStatic;

    @Mock
    private InternetExplorerOptions internetExplorerOptions;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.WebDriver webDriverConfig;

    @Mock
    private Configuration.WebDriver.InternetExplorer internetExplorerConfig;

    @Mock
    private Configuration.WebDriver.Grid grid;

    @Mock
    private WebDriverManager webDriverManager;

    @InjectMocks
    private InternetExplorer internetExplorer;

    @BeforeEach
    public void beforeEach() {
        webDriverManagerMockedStatic = mockStatic(WebDriverManager.class);
    }

    @AfterEach
    public void afterEach() {
        webDriverManagerMockedStatic.close();
    }

    @Test
    @DisplayName("exposesConsole should return false")
    public void exposesConsole() {
        assertFalse(internetExplorer.exposesConsole());
    }

    @Test
    @DisplayName("takesPartialScreenshots should return false")
    public void takesPartialScreenshots() {
        assertFalse(internetExplorer.takesPartialScreenshots());
    }

    @Test
    @DisplayName("getWebDriverManager should return call the iedriver method")
    public void getWebDriverManager() {
        when(WebDriverManager.iedriver()).thenReturn(webDriverManager);
        assertEquals(webDriverManager, internetExplorer.getWebDriverManager());
    }

    @Test
    @DisplayName("getSystemPropertyName should return webDriver.ie.driver")
    public void getSystemPropertyName() {
        assertEquals("webDriver.ie.driver", internetExplorer.getSystemPropertyName());
    }

    @Test
    @DisplayName("getDriverName should return the name of the executable")
    public void getDriverName() {
        assertEquals("IEDriverServer.exe", internetExplorer.getDriverName());
    }

    @Test
    @DisplayName("buildCapabilitiesFrom should build an instance of Chrome based on the provided configuration")
    public void buildCapabilitiesFrom() {
        when(configuration.getWebDriver()).thenReturn(webDriverConfig);
        when(webDriverConfig.getIe()).thenReturn(internetExplorerConfig);

        MockedConstruction<InternetExplorerOptions> internetExplorerOptionsMockedConstruction = mockConstruction(InternetExplorerOptions.class);
        MockedConstruction<LoggingPreferences> loggingPreferencesMockedConstruction = mockConstruction(LoggingPreferences.class);

        internetExplorer.buildCapabilitiesFrom(configuration);
        final InternetExplorerOptions internetExplorerOptions = internetExplorerOptionsMockedConstruction.constructed().get(0);
        verify(internetExplorerOptions).setAcceptInsecureCerts(true);

        internetExplorerOptionsMockedConstruction.close();
        loggingPreferencesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildWebDriver should return a ChromeDriver with the capabilities")
    public void buildWebDriver() {
        internetExplorer.capabilities = internetExplorerOptions;

        try (MockedConstruction<InternetExplorerDriver> internetExplorerDriverMockedConstruction = mockConstruction(InternetExplorerDriver.class, (mock, context) -> {
            assertEquals(internetExplorerOptions, context.arguments().get(0));
        })) {
            final WebDriver actual = internetExplorer.buildWebDriver();
            assertEquals(internetExplorerDriverMockedConstruction.constructed().get(0), actual);
        }
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities")
    public void mergeGridCapabilitiesFrom() {
        internetExplorer.capabilities = internetExplorerOptions;

        when(grid.getCapabilities()).thenReturn(Map.of("one", "value"));
        internetExplorer.mergeGridCapabilitiesFrom(grid);
        verify(internetExplorerOptions).setCapability("one", "value");
    }
}
