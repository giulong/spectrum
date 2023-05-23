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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Firefox")
class FirefoxTest {

    private MockedStatic<WebDriverManager> webDriverManagerMockedStatic;

    @Mock
    private FirefoxOptions firefoxOptions;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.WebDriver webDriverConfig;

    @Mock
    private Configuration.WebDriver.Firefox firefoxConfig;

    @Mock
    private FirefoxDriverLogLevel firefoxDriverLogLevel;

    @Mock
    private Configuration.WebDriver.Grid grid;

    @Mock
    private WebDriverManager webDriverManager;

    @InjectMocks
    private Firefox firefox;

    @BeforeEach
    public void beforeEach() {
        webDriverManagerMockedStatic = mockStatic(WebDriverManager.class);
    }

    @AfterEach
    public void afterEach() {
        webDriverManagerMockedStatic.close();
    }

    @Test
    @DisplayName("exposesConsole should return true")
    public void exposesConsole() {
        assertTrue(firefox.exposesConsole());
    }

    @Test
    @DisplayName("takesPartialScreenshots should return false")
    public void takesPartialScreenshots() {
        assertFalse(firefox.takesPartialScreenshots());
    }

    @Test
    @DisplayName("getWebDriverManager should return call the firefoxdriver method")
    public void getWebDriverManager() {
        when(WebDriverManager.firefoxdriver()).thenReturn(webDriverManager);
        assertEquals(webDriverManager, firefox.getWebDriverManager());
    }

    @Test
    @DisplayName("getSystemPropertyName should return webDriver.gecko.driver")
    public void getSystemPropertyName() {
        assertEquals("webDriver.gecko.driver", firefox.getSystemPropertyName());
    }

    @Test
    @DisplayName("getDriverName should return the name of the executable")
    public void getDriverName() {
        assertEquals("geckodriver.exe", firefox.getDriverName());
    }

    @Test
    @DisplayName("buildCapabilitiesFrom should build an instance of Chrome based on the provided configuration")
    public void buildCapabilitiesFrom() {
        final List<String> arguments = List.of("args");

        when(configuration.getWebDriver()).thenReturn(webDriverConfig);
        when(webDriverConfig.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getArgs()).thenReturn(arguments);
        when(firefoxConfig.getLogLevel()).thenReturn(firefoxDriverLogLevel);
        when(firefoxConfig.getPreferences()).thenReturn(Map.of("one", "value"));

        MockedConstruction<FirefoxOptions> firefoxOptionsMockedConstruction = mockConstruction(FirefoxOptions.class);

        firefox.buildCapabilitiesFrom(configuration);
        final FirefoxOptions firefoxOptions = firefoxOptionsMockedConstruction.constructed().get(0);
        verify(firefoxOptions).addArguments(arguments);
        verify(firefoxOptions).setLogLevel(firefoxDriverLogLevel);
        verify(firefoxOptions).setAcceptInsecureCerts(true);
        verify(firefoxOptions).addPreference("one", "value");

        firefoxOptionsMockedConstruction.close();
    }

    @Test
    @DisplayName("buildWebDriver should return a ChromeDriver with the capabilities")
    public void buildWebDriver() {
        firefox.capabilities = firefoxOptions;

        try (MockedConstruction<FirefoxDriver> firefoxDriverMockedConstruction = mockConstruction(FirefoxDriver.class, (mock, context) -> {
            assertEquals(firefoxOptions, context.arguments().get(0));
        })) {
            final WebDriver actual = firefox.buildWebDriver();
            assertEquals(firefoxDriverMockedConstruction.constructed().get(0), actual);
        }
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities")
    public void mergeGridCapabilitiesFrom() {
        firefox.capabilities = firefoxOptions;

        when(grid.getCapabilities()).thenReturn(Map.of("one", "value"));
        firefox.mergeGridCapabilitiesFrom(grid);
        verify(firefoxOptions).setCapability("one", "value");
    }
}
