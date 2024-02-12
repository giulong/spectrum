package io.github.giulong.spectrum.browsers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;

@ExtendWith(MockitoExtension.class)
@DisplayName("Chromium")
class ChromiumTest {

    @Mock
    private ChromeOptions chromeOptions;

    @Mock
    private Configuration.WebDriver.Logs logs;

    @Mock
    private Level browserLevel;

    @Mock
    private Level driverLevel;

    @Mock
    private Level performanceLevel;

    @Mock
    private Map<String, Object> gridCapabilities;

    @Captor
    private ArgumentCaptor<DesiredCapabilities> desiredCapabilitiesArgumentCaptor;

    @InjectMocks
    private Chrome chrome;

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("capabilities", chrome, chromeOptions);
    }

    @Test
    @DisplayName("setLoggingPreferencesFrom should set the LOGGING_PREFS in the capabilities")
    public void setLoggingPreferencesFrom() {
        when(logs.getBrowser()).thenReturn(browserLevel);
        when(logs.getDriver()).thenReturn(driverLevel);
        when(logs.getPerformance()).thenReturn(performanceLevel);

        MockedConstruction<LoggingPreferences> mockedConstruction = mockConstruction(LoggingPreferences.class);

        chrome.capabilities = chromeOptions;
        chrome.setLoggingPreferencesFrom(logs);
        verify(chromeOptions).setCapability(LOGGING_PREFS, mockedConstruction.constructed().getFirst());

        mockedConstruction.close();
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities and return the capabilities")
    public void mergeGridCapabilitiesFrom() {
        when(chromeOptions.merge(desiredCapabilitiesArgumentCaptor.capture())).thenReturn(chromeOptions);

        MockedConstruction<DesiredCapabilities> desiredCapabilitiesMockedConstruction = mockConstruction(DesiredCapabilities.class, (mock, context) -> {
            assertEquals(gridCapabilities, context.arguments().getFirst());
        });

        final ChromeOptions actual = chrome.mergeGridCapabilitiesFrom(gridCapabilities);
        verify(chromeOptions).merge(desiredCapabilitiesMockedConstruction.constructed().getFirst());
        assertEquals(actual, chromeOptions);

        desiredCapabilitiesMockedConstruction.close();
    }
}
