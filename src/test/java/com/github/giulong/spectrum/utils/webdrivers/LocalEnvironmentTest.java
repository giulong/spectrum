package com.github.giulong.spectrum.utils.webdrivers;

import com.github.giulong.spectrum.browsers.Browser;
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
@DisplayName("LocalEnvironment")
class LocalEnvironmentTest {

    @Mock
    private Browser<?, ?, ?> browser;

    @Mock
    private WebDriverManager webDriverManager;

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private LocalEnvironment localEnvironment;

    @Test
    @DisplayName("buildFrom should configure a local webDriver")
    public void buildFromDownload() {
        when(browser.getWebDriverManager()).thenReturn(webDriverManager);

        localEnvironment.setupFrom(browser, null);

        verify(webDriverManager).setup();
    }

    @Test
    @DisplayName("finalizeSetupOf should do nothing")
    public void finalizeSetupOf() {
        localEnvironment.finalizeSetupOf(webDriver);
    }
}
