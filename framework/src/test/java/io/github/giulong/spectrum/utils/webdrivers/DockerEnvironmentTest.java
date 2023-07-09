package io.github.giulong.spectrum.utils.webdrivers;

import io.github.giulong.spectrum.browsers.Browser;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DockerEnvironment")
class DockerEnvironmentTest {

    @Mock
    private Browser<?, ?, ?> browser;

    @Mock
    private WebDriverManager webDriverManager;

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private DockerEnvironment dockerEnvironment;

    @Test
    @DisplayName("buildFrom should configure webDriverManager with docker")
    public void buildFrom() {
        when(browser.getWebDriverManager()).thenReturn(webDriverManager);
        when(webDriverManager.browserInDocker()).thenReturn(webDriverManager);

        dockerEnvironment.setupFrom(browser, null);

        verify(webDriverManager).create();
    }

    @Test
    @DisplayName("finalizeSetupOf should do nothing")
    public void finalizeSetupOf() {
        dockerEnvironment.finalizeSetupOf(webDriver);
    }
}
