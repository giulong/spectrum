package io.github.giulong.spectrum.utils.webdrivers;

import io.github.giulong.spectrum.browsers.Browser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;
import org.openqa.selenium.remote.service.DriverService;

import static io.github.giulong.spectrum.utils.webdrivers.LocalEnvironment.DRIVER_SERVICE_THREAD_LOCAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocalEnvironment")
class LocalEnvironmentTest {

    @Mock
    private Browser<?, ?, ?> browser;

    @Mock
    private ChromeDriverService.Builder chromeDriverServiceBuilder;

    @Mock
    private ChromeDriverService chromeDriverService;

    @Mock
    private WebDriver webDriver;

    @Mock
    private RemoteWebDriverBuilder webDriverBuilder;

    @Captor
    private ArgumentCaptor<DriverService> driverServiceArgumentCaptor;

    @InjectMocks
    private LocalEnvironment localEnvironment;

    @BeforeEach
    public void beforeEach() {
        DRIVER_SERVICE_THREAD_LOCAL.remove();
    }

    @Test
    @DisplayName("setupFrom should set the driver service and return an instance of WebDriver")
    public void setupFromDownload() {
        doReturn(chromeDriverServiceBuilder).when(browser).getDriverServiceBuilder();
        when(chromeDriverServiceBuilder.withLogOutput(System.out)).thenReturn(chromeDriverServiceBuilder);
        when(webDriverBuilder.withDriverService(driverServiceArgumentCaptor.capture())).thenReturn(webDriverBuilder);
        when(webDriverBuilder.build()).thenReturn(webDriver);

        final DriverService threadLocalDriverService = DRIVER_SERVICE_THREAD_LOCAL.get();

        assertEquals(webDriver, localEnvironment.setupFrom(browser, webDriverBuilder));
        assertEquals(driverServiceArgumentCaptor.getValue(), threadLocalDriverService);
    }

    @Test
    @DisplayName("shutdown should do close the driver service")
    public void shutdown() {
        DRIVER_SERVICE_THREAD_LOCAL.set(chromeDriverService);

        localEnvironment.shutdown();

        verify(chromeDriverService).close();
    }
}
