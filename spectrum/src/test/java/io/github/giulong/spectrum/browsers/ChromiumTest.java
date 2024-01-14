package io.github.giulong.spectrum.browsers;

import io.github.giulong.spectrum.utils.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;

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

    @InjectMocks
    private Chrome chrome;

    @Test
    @DisplayName("setLoggingPreferencesFrom should set the LOGGING_PREFS in the capabilities")
    public void setLoggingPreferencesFrom() {
        when(logs.getBrowser()).thenReturn(browserLevel);
        when(logs.getDriver()).thenReturn(driverLevel);
        when(logs.getPerformance()).thenReturn(performanceLevel);

        MockedConstruction<LoggingPreferences> mockedConstruction = mockConstruction(LoggingPreferences.class);

        chrome.capabilities = chromeOptions;
        chrome.setLoggingPreferencesFrom(logs);
        verify(chromeOptions).setCapability(LOGGING_PREFS, mockedConstruction.constructed().get(0));

        mockedConstruction.close();
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities and return the capabilities")
    public void mergeGridCapabilitiesFrom() {
        final String key = "one";
        final String value = "value";
        chrome.capabilities = chromeOptions;

        final ChromeOptions actual = chrome.mergeGridCapabilitiesFrom(Map.of(key, value));
        verify(chromeOptions).setCapability(key, value);
        assertEquals(actual, chromeOptions);
    }
}
