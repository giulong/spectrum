package com.github.giulong.spectrum.utils.webdrivers;

import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.pojos.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GridEnvironment")
class GridEnvironmentTest {

    @Mock
    private RemoteWebDriverBuilder webDriverBuilder;

    @Mock
    private Browser<AbstractDriverOptions<?>> browser;

    @Mock
    private Configuration.WebDriver webDriverConfiguration;

    @Mock
    private RemoteWebDriver webDriver;

    @InjectMocks
    private GridEnvironment gridEnvironment;

    @Test
    @DisplayName("buildFrom should configure a remote webDriver")
    public void buildFrom() throws MalformedURLException {
        final URL url = URI.create("http://url").toURL();

        gridEnvironment.url = url;
        gridEnvironment.capabilities.put("one", "value");
        gridEnvironment.buildFrom(browser, webDriverBuilder);

        verify(browser).mergeGridCapabilitiesFrom(Map.of("one", "value"));
        verify(webDriverBuilder).address(url);
    }

    @Test
    @DisplayName("finalizeSetupOf with localFileDetector should set the file detector on the webdriver")
    public void finalizeSetupOfTrue() {
        gridEnvironment.localFileDetector = true;

        MockedConstruction<LocalFileDetector> mockedConstruction = mockConstruction(LocalFileDetector.class);

        gridEnvironment.finalizeSetupOf(webDriver);
        verify(webDriver).setFileDetector(mockedConstruction.constructed().get(0));

        mockedConstruction.close();
    }

    @Test
    @DisplayName("finalizeSetupOf with no localFileDetector should do nothing")
    public void finalizeSetupOfFalse() {
        gridEnvironment.localFileDetector = false;

        gridEnvironment.finalizeSetupOf(webDriver);
        verify(webDriver, never()).setFileDetector(any());
    }
}
