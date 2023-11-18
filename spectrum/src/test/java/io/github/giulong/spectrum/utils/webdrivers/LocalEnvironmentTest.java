package io.github.giulong.spectrum.utils.webdrivers;

import io.github.giulong.spectrum.browsers.Browser;
import io.github.giulong.spectrum.internals.BrowserLog;
import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.pojos.Configuration.WebDriver.Logs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;
import org.openqa.selenium.remote.service.DriverService;

import static io.github.giulong.spectrum.utils.webdrivers.LocalEnvironment.DRIVER_SERVICE_THREAD_LOCAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.slf4j.event.Level.DEBUG;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocalEnvironment")
class LocalEnvironmentTest {

    private MockedStatic<RemoteWebDriver> remoteWebDriverMockedStatic;
    private MockedStatic<BrowserLog> browserLogMockedStatic;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.WebDriver webDriverConf;

    @Mock
    private Logs logs;

    @Mock
    private BrowserLog.BrowserLogBuilder browserLogBuilder;

    @Mock
    private BrowserLog browserLog;

    @Mock
    private Browser<ChromeOptions, ?, ?> browser;

    @Mock
    private ChromeOptions chromeOptions;

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
        remoteWebDriverMockedStatic = mockStatic(RemoteWebDriver.class);
        browserLogMockedStatic = mockStatic(BrowserLog.class);
        DRIVER_SERVICE_THREAD_LOCAL.remove();
    }

    @AfterEach
    public void afterEach() {
        remoteWebDriverMockedStatic.close();
        browserLogMockedStatic.close();
    }

    @Test
    @DisplayName("setupFrom should set the driver service and return an instance of WebDriver")
    public void setupFromDownload() {
        when(configuration.getWebDriver()).thenReturn(webDriverConf);
        when(webDriverConf.getLogs()).thenReturn(logs);
        when(logs.getLevel()).thenReturn(DEBUG);
        when(BrowserLog.builder()).thenReturn(browserLogBuilder);
        when(browserLogBuilder.level(DEBUG)).thenReturn(browserLogBuilder);
        when(browserLogBuilder.build()).thenReturn(browserLog);
        when(browser.getCapabilities()).thenReturn(chromeOptions);
        doReturn(chromeDriverServiceBuilder).when(browser).getDriverServiceBuilder();
        when(chromeDriverServiceBuilder.withLogOutput(browserLog)).thenReturn(chromeDriverServiceBuilder);
        when(RemoteWebDriver.builder()).thenReturn(webDriverBuilder);
        when(webDriverBuilder.withDriverService(driverServiceArgumentCaptor.capture())).thenReturn(webDriverBuilder);
        when(webDriverBuilder.oneOf(chromeOptions)).thenReturn(webDriverBuilder);
        when(webDriverBuilder.build()).thenReturn(webDriver);

        final DriverService threadLocalDriverService = DRIVER_SERVICE_THREAD_LOCAL.get();

        assertEquals(webDriver, localEnvironment.setupFrom(configuration, browser));
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
