package io.github.giulong.spectrum.utils.webdrivers;

import io.github.giulong.spectrum.browsers.Browser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocalEnvironment")
class LocalEnvironmentTest {

    @Mock
    private Browser<?, ?, ?> browser;

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private LocalEnvironment localEnvironment;

    @Test
    @DisplayName("buildFrom should do nothing")
    public void buildFromDownload() {
        localEnvironment.setupFrom(browser, null);
    }

    @Test
    @DisplayName("finalizeSetupOf should do nothing")
    public void finalizeSetupOf() {
        localEnvironment.finalizeSetupOf(webDriver);
    }
}
