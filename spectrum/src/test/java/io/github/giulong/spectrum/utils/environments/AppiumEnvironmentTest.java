package io.github.giulong.spectrum.utils.environments;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.drivers.Appium;
import io.github.giulong.spectrum.internals.AppiumLog;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;

import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.slf4j.event.Level.INFO;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppiumEnvironment")
class AppiumEnvironmentTest {

    private MockedStatic<AppiumDriverLocalService> appiumDriverLocalServiceMockedStatic;
    private MockedStatic<AppiumLog> appiumLogMockedStatic;

    @Mock
    private AppiumLog.AppiumLogBuilder appiumLogBuilder;

    @Mock
    private AppiumLog appiumLog;

    @Mock
    private AppiumDriverLocalService driverService;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Environments environments;

    @Mock
    private Configuration.Environments.Appium appium;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Configuration.Drivers.Logs logs;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Appium<?, ?> driver;

    @Mock
    private AppiumServiceBuilder builder;

    @Mock
    private URL url;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private AppiumDriver appiumDriver;

    @Captor
    private ArgumentCaptor<DesiredCapabilities> desiredCapabilitiesArgumentCaptor;

    @InjectMocks
    private AppiumEnvironment appiumEnvironment;

    @BeforeEach
    public void beforeEach() {
        appiumDriverLocalServiceMockedStatic = mockStatic(AppiumDriverLocalService.class);
        appiumLogMockedStatic = mockStatic(AppiumLog.class);

        Reflections.setField("configuration", appiumEnvironment, configuration);
    }

    @AfterEach
    public void afterEach() {
        appiumDriverLocalServiceMockedStatic.close();
        appiumLogMockedStatic.close();
    }

    @Test
    @DisplayName("sessionOpened should initialise the AppiumDriverLocalService redirecting the logs to slf4j")
    public void sessionOpenedLogs() {
        when(configuration.getEnvironments()).thenReturn(environments);
        when(environments.getAppium()).thenReturn(appium);
        when(appium.getCapabilities()).thenReturn(capabilities);
        when(appium.isCollectServerLogs()).thenReturn(true);

        when(configuration.getRuntime()).thenReturn(runtime);
        doReturn(driver).when(runtime).getDriver();
        doReturn(builder).when(driver).getDriverServiceBuilder();
        when(builder.withCapabilities(desiredCapabilitiesArgumentCaptor.capture())).thenReturn(builder);
        when(AppiumDriverLocalService.buildService(builder)).thenReturn(driverService);

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getLogs()).thenReturn(logs);
        when(logs.getLevel()).thenReturn(INFO);
        when(AppiumLog.builder()).thenReturn(appiumLogBuilder);
        when(appiumLogBuilder.level(INFO)).thenReturn(appiumLogBuilder);
        when(appiumLogBuilder.build()).thenReturn(appiumLog);

        MockedConstruction<DesiredCapabilities> desiredCapabilitiesMockedConstruction = mockConstruction(DesiredCapabilities.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        appiumEnvironment.sessionOpened();

        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), desiredCapabilitiesArgumentCaptor.getValue());
        assertEquals(driverService, Reflections.getFieldValue("driverService", appiumEnvironment));

        verify(driverService).clearOutPutStreams();
        verify(driverService).addOutPutStream(appiumLog);
        verify(driverService).start();

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("sessionOpened should initialise the AppiumDriverLocalService without collecting server logs")
    public void sessionOpened() {
        when(configuration.getEnvironments()).thenReturn(environments);
        when(environments.getAppium()).thenReturn(appium);
        when(appium.getCapabilities()).thenReturn(capabilities);
        when(appium.isCollectServerLogs()).thenReturn(false);

        when(configuration.getRuntime()).thenReturn(runtime);
        doReturn(driver).when(runtime).getDriver();
        doReturn(builder).when(driver).getDriverServiceBuilder();
        when(builder.withCapabilities(desiredCapabilitiesArgumentCaptor.capture())).thenReturn(builder);
        when(AppiumDriverLocalService.buildService(builder)).thenReturn(driverService);

        MockedConstruction<DesiredCapabilities> desiredCapabilitiesMockedConstruction = mockConstruction(DesiredCapabilities.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        appiumEnvironment.sessionOpened();

        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), desiredCapabilitiesArgumentCaptor.getValue());
        assertEquals(driverService, Reflections.getFieldValue("driverService", appiumEnvironment));

        verify(driverService, never()).clearOutPutStreams();
        verify(driverService, never()).addOutPutStream(any());
        verify(driverService).start();

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("sessionClosed should just stop the driverService")
    public void sessionClosed() {
        appiumEnvironment.sessionClosed();

        verify(driverService).stop();
    }

    @Test
    @DisplayName("setupFor should delegate the webDriver construction to the actual subclass, calling the super setFileDetectorFor")
    public void setupFor() {
        when(configuration.getEnvironments()).thenReturn(environments);
        when(environments.getAppium()).thenReturn(appium);
        when(appium.isLocalFileDetector()).thenReturn(true);
        when(appium.getUrl()).thenReturn(url);

        doReturn(appiumDriver).when(driver).buildDriverFor(url);

        assertEquals(appiumDriver, appiumEnvironment.setupFor(driver));
        verify(appiumDriver).setFileDetector(any(LocalFileDetector.class));
    }

    @Test
    @DisplayName("shutdown should just close the driverService")
    public void shutdown() {
        appiumEnvironment.shutdown();

        verify(driverService).close();
    }
}