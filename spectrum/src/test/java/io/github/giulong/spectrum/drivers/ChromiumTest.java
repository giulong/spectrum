package io.github.giulong.spectrum.drivers;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;

import java.util.logging.Level;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;

class ChromiumTest {

    @MockFinal
    @SuppressWarnings("unused")
    private ChromeOptions capabilities;

    @Mock
    private Configuration.Drivers.Logs logs;

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
    void setLoggingPreferencesFrom() {
        when(logs.getBrowser()).thenReturn(browserLevel);
        when(logs.getDriver()).thenReturn(driverLevel);
        when(logs.getPerformance()).thenReturn(performanceLevel);

        MockedConstruction<LoggingPreferences> mockedConstruction = mockConstruction();

        chrome.capabilities = capabilities;
        chrome.setLoggingPreferencesFrom(logs);
        verify(capabilities).setCapability(LOGGING_PREFS, mockedConstruction.constructed().getFirst());

        mockedConstruction.close();
    }
}
