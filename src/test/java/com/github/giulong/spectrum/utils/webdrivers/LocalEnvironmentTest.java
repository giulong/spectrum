package com.github.giulong.spectrum.utils.webdrivers;

import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.MutableCapabilities;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocalEnvironment")
class LocalEnvironmentTest {

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Browser<MutableCapabilities> browser;

    @Mock
    private WebDriverManager webDriverManager;

    @InjectMocks
    private LocalEnvironment localEnvironment;

    @Test
    @DisplayName("buildFrom should configure a local webDriver without downloading it")
    public void buildFrom() {
        final String driversPath = "driversPath";
        final String systemPropertyName = "systemPropertyName";
        final String driverName = "driverName";

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getDriversPath()).thenReturn(driversPath);
        when(browser.getSystemPropertyName()).thenReturn(systemPropertyName);
        when(browser.getDriverName()).thenReturn(driverName);

        localEnvironment.buildFrom(configuration, browser);

        assertEquals(Path.of(driversPath, driverName).toString(), System.getProperty(systemPropertyName));
        System.clearProperty(systemPropertyName);
    }

    @Test
    @DisplayName("buildFrom should configure a local webDriver")
    public void buildFromDownload() {
        final String driversPath = "driversPath";
        final String systemPropertyName = "systemPropertyName";

        localEnvironment.setDownloadWebDriver(true);

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getDriversPath()).thenReturn(driversPath);
        when(browser.getWebDriverManager()).thenReturn(webDriverManager);
        when(webDriverManager.avoidOutputTree()).thenReturn(webDriverManager);
        when(webDriverManager.cachePath(driversPath)).thenReturn(webDriverManager);

        localEnvironment.buildFrom(configuration, browser);

        verify(webDriverManager).setup();
        assertNull(System.getProperty(systemPropertyName));
    }
}