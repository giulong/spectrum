package io.github.giulong.spectrum.utils.environments;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.internals.DriverLog;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Drivers.Logs;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;
import org.openqa.selenium.remote.service.DriverService;

import static io.github.giulong.spectrum.utils.environments.LocalEnvironment.DRIVER_SERVICE_THREAD_LOCAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.slf4j.event.Level.DEBUG;

class LocalEnvironmentTest {

    private MockedStatic<RemoteWebDriver> remoteWebDriverMockedStatic;
    private MockedStatic<DriverLog> driverLogMockedStatic;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Logs logs;

    @Mock
    private DriverLog.DriverLogBuilder driverLogBuilder;

    @Mock
    private DriverLog driverLog;

    @Mock
    private Driver<ChromeOptions, ?, ?> driver;

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
        driverLogMockedStatic = mockStatic(DriverLog.class);
        DRIVER_SERVICE_THREAD_LOCAL.remove();

        Reflections.setField("configuration", localEnvironment, configuration);
    }

    @AfterEach
    public void afterEach() {
        remoteWebDriverMockedStatic.close();
        driverLogMockedStatic.close();
    }

    @Test
    @DisplayName("setupFrom should set the driver service and return an instance of WebDriver")
    public void setupFromDownload() {
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getLogs()).thenReturn(logs);
        when(logs.getLevel()).thenReturn(DEBUG);
        when(DriverLog.builder()).thenReturn(driverLogBuilder);
        when(driverLogBuilder.level(DEBUG)).thenReturn(driverLogBuilder);
        when(driverLogBuilder.build()).thenReturn(driverLog);
        when(driver.getCapabilities()).thenReturn(chromeOptions);
        doReturn(chromeDriverServiceBuilder).when(driver).getDriverServiceBuilder();
        when(chromeDriverServiceBuilder.withLogOutput(driverLog)).thenReturn(chromeDriverServiceBuilder);
        when(RemoteWebDriver.builder()).thenReturn(webDriverBuilder);
        when(webDriverBuilder.withDriverService(driverServiceArgumentCaptor.capture())).thenReturn(webDriverBuilder);
        when(webDriverBuilder.oneOf(chromeOptions)).thenReturn(webDriverBuilder);
        when(webDriverBuilder.build()).thenReturn(webDriver);

        final DriverService threadLocalDriverService = DRIVER_SERVICE_THREAD_LOCAL.get();

        assertEquals(webDriver, localEnvironment.setupFor(driver));
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
