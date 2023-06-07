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
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DockerEnvironment")
class DockerEnvironmentTest {

    @Mock
    private Configuration configuration;

    @Mock
    private Browser<?> browser;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private WebDriverManager webDriverManager;

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private DockerEnvironment dockerEnvironment;

    @Test
    @DisplayName("buildFrom should configure webDriverManager with docker")
    public void buildFrom() {
        final String driversPath = "driversPath";

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getDriversPath()).thenReturn(driversPath);
        when(browser.getWebDriverManager()).thenReturn(webDriverManager);
        when(webDriverManager.avoidOutputTree()).thenReturn(webDriverManager);
        when(webDriverManager.cachePath(driversPath)).thenReturn(webDriverManager);
        when(webDriverManager.browserInDocker()).thenReturn(webDriverManager);
        when(browser.buildWebDriver()).thenReturn(webDriver);

        assertEquals(webDriver, dockerEnvironment.buildFrom(configuration, browser));

        verify(webDriverManager).setup();
    }
}