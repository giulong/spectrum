package com.github.giulong.spectrum.utils.webdrivers;

import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.pojos.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GridEnvironment")
class GridEnvironmentTest {

    @Mock
    private RemoteWebDriverBuilder webDriverBuilder;

    @Mock
    private Configuration configuration;

    @Mock
    private Browser<AbstractDriverOptions<?>> browser;

    @Mock
    private Configuration.WebDriver webDriverConfiguration;

    @Mock
    private Configuration.WebDriver.Grid grid;

    @InjectMocks
    private GridEnvironment gridEnvironment;

    @Test
    @DisplayName("buildFrom should configure a remote webDriver")
    public void buildFrom() throws MalformedURLException {
        final URL url = URI.create("http://url").toURL();

        when(configuration.getWebDriver()).thenReturn(webDriverConfiguration);
        when(webDriverConfiguration.getGrid()).thenReturn(grid);
        when(grid.getUrl()).thenReturn(url);
        when(webDriverBuilder.address(url)).thenReturn(webDriverBuilder);

        gridEnvironment.buildFrom(configuration, browser, webDriverBuilder);

        verify(browser).mergeGridCapabilitiesFrom(grid);
    }
}
