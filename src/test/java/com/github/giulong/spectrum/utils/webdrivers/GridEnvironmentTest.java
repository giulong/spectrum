package com.github.giulong.spectrum.utils.webdrivers;

import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.pojos.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GridEnvironment")
class GridEnvironmentTest {

    private MockedStatic<RemoteWebDriver> remoteWebDriverMockedStatic;

    @Mock
    private Configuration configuration;

    @Mock
    private Browser<MutableCapabilities> browser;

    @Mock
    private Configuration.WebDriver webDriverConfiguration;

    @Mock
    private Configuration.WebDriver.Grid grid;

    @Mock
    private WebDriver webDriver;

    @Mock
    private MutableCapabilities capabilities;

    @InjectMocks
    private GridEnvironment gridEnvironment;

    @BeforeEach
    public void beforeEach() {
        remoteWebDriverMockedStatic = mockStatic(RemoteWebDriver.class);
    }

    @AfterEach
    public void afterEach() {
        remoteWebDriverMockedStatic.close();
    }

    @Test
    @DisplayName("buildFrom should configure a remote webDriver")
    public void buildFrom() throws MalformedURLException {
        final URL url = URI.create("http://url").toURL();

        when(browser.getCapabilities()).thenReturn(capabilities);
        when(configuration.getWebDriver()).thenReturn(webDriverConfiguration);
        when(webDriverConfiguration.getGrid()).thenReturn(grid);
        when(grid.getUrl()).thenReturn(url);

        RemoteWebDriverBuilder remoteWebDriverBuilder = mock(RemoteWebDriverBuilder.class);
        when(RemoteWebDriver.builder()).thenReturn(remoteWebDriverBuilder);
        when(remoteWebDriverBuilder.oneOf(capabilities)).thenReturn(remoteWebDriverBuilder);
        when(remoteWebDriverBuilder.address(url)).thenReturn(remoteWebDriverBuilder);
        when(remoteWebDriverBuilder.build()).thenReturn(webDriver);

        assertEquals(webDriver, gridEnvironment.buildFrom(configuration, browser));

        verify(browser).mergeGridCapabilitiesFrom(grid);
    }
}