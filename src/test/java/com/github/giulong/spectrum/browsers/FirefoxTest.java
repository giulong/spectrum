package com.github.giulong.spectrum.browsers;

import com.github.giulong.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Firefox")
class FirefoxTest {

    private MockedStatic<WebDriverManager> webDriverManagerMockedStatic;

    @Mock
    private FirefoxOptions firefoxOptions;

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
    @DisplayName("getWebDriverManager should return call the firefoxdriver method")
    public void getWebDriverManager() {
        when(WebDriverManager.firefoxdriver()).thenReturn(webDriverManager);
        assertEquals(webDriverManager, firefox.getWebDriverManager());
    }

    @Test
    @DisplayName("buildCapabilitiesFrom should build an instance of Firefox based on the provided configuration")
    public void buildCapabilitiesFrom() {
        final List<String> arguments = List.of("args");

        when(webDriverConfig.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getArgs()).thenReturn(arguments);
        when(firefoxConfig.getLogLevel()).thenReturn(firefoxDriverLogLevel);
        when(firefoxConfig.getPreferences()).thenReturn(Map.of("one", "value"));

        MockedConstruction<FirefoxOptions> firefoxOptionsMockedConstruction = mockConstruction(FirefoxOptions.class);

        firefox.buildCapabilitiesFrom(webDriverConfig, null);
        final FirefoxOptions firefoxOptions = firefoxOptionsMockedConstruction.constructed().get(0);
        verify(firefoxOptions).addArguments(arguments);
        verify(firefoxOptions).setLogLevel(firefoxDriverLogLevel);
        verify(firefoxOptions).addPreference("one", "value");

        firefoxOptionsMockedConstruction.close();
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities")
    public void mergeGridCapabilitiesFrom() {
        firefox.capabilities = firefoxOptions;

        when(grid.getCapabilities()).thenReturn(Map.of("one", "value"));
        firefox.mergeGridCapabilitiesFrom(grid);
        verify(firefoxOptions).setCapability("one", "value");
    }

    @DisplayName("addPreference should add the correct preference based on the value type")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void addPreference(final Object value, final Object expected) {
        firefox.capabilities = firefoxOptions;

        firefox.addPreference("key", value);
        verify(firefoxOptions).addPreference("key", expected);
    }

    @Test
    @DisplayName("setCapability should add the correct capabilities for boolean values")
    public void setCapabilityBoolean() {
        firefox.capabilities = firefoxOptions;

        firefox.setCapability("key", true);
        verify(firefoxOptions).setCapability("key", true);
    }

    @Test
    @DisplayName("setCapability should add the correct capabilities for int values")
    public void setCapabilityInteger() {
        firefox.capabilities = firefoxOptions;

        firefox.setCapability("key", 123);
        verify(firefoxOptions).setCapability("key", 123);
    }

    @Test
    @DisplayName("setCapability should add the correct capabilities for object values")
    public void setCapabilityObject() {
        firefox.capabilities = firefoxOptions;
        final DummyObject dummyObject = new DummyObject();

        firefox.setCapability("key", dummyObject);
        verify(firefoxOptions).setCapability("key", dummyObject);
    }

    public static Stream<Arguments> valuesProvider() {
        final DummyObject dummyObject = new DummyObject();

        return Stream.of(
                arguments(true, true),
                arguments(123, 123),
                arguments("123", "123"),
                arguments("true", "true"),
                arguments(dummyObject, dummyObject.toString())
        );
    }

    private static class DummyObject {

        @Override
        public String toString() {
            return "toString";
        }
    }
}
