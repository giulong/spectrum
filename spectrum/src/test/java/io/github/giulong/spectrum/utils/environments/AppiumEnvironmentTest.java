package io.github.giulong.spectrum.utils.environments;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.browsers.Browser;
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
    private Configuration.WebDriver webDriver;

    @Mock
    private Configuration.WebDriver.Logs logs;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Browser<?, ?, ?> browser;

    @Mock
    private AppiumServiceBuilder builder;

    @Mock
    private URL url;

    @Mock
    private Map<String, String> capabilities;

    @Mock
    private UiAutomator2Options uiAutomator2Options;

    @Captor
    private ArgumentCaptor<DesiredCapabilities> desiredCapabilitiesArgumentCaptor;

    @InjectMocks
    private AppiumEnvironment appiumEnvironment;

    @BeforeEach
    public void beforeEach() {
        appiumDriverLocalServiceMockedStatic = mockStatic(AppiumDriverLocalService.class);
        appiumLogMockedStatic = mockStatic(AppiumLog.class);

        Reflections.setField("configuration", appiumEnvironment, configuration);
        Reflections.setField("capabilities", appiumEnvironment, capabilities);
    }

    @AfterEach
    public void afterEach() {
        appiumDriverLocalServiceMockedStatic.close();
        appiumLogMockedStatic.close();
    }

    @Test
    @DisplayName("sessionOpened should initialise the AppiumDriverLocalService redirecting the logs to slf4j")
    public void sessionOpened() {
        final String ipAddress = "ipAddress";
        final int port = 123;

        Reflections.setField("ipAddress", appiumEnvironment, ipAddress);
        Reflections.setField("port", appiumEnvironment, port);

        when(configuration.getRuntime()).thenReturn(runtime);
        doReturn(browser).when(runtime).getBrowser();
        doReturn(builder).when(browser).getDriverServiceBuilder();
        when(builder.withCapabilities(desiredCapabilitiesArgumentCaptor.capture())).thenReturn(builder);
        when(builder.withIPAddress(ipAddress)).thenReturn(builder);
        when(builder.usingPort(port)).thenReturn(builder);
        when(AppiumDriverLocalService.buildService(builder)).thenReturn(driverService);

        when(configuration.getWebDriver()).thenReturn(webDriver);
        when(webDriver.getLogs()).thenReturn(logs);
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
    @DisplayName("sessionClosed should just stop the driverService")
    public void sessionClosed() {
        appiumEnvironment.sessionClosed();

        verify(driverService).stop();
    }

    @Test
    @DisplayName("setupFor should return the AndroidDriver with the capabilities of the provided browser")
    public void setupFor() {
        doReturn(uiAutomator2Options).when(browser).getCapabilities();

        MockedConstruction<AndroidDriver> androidDriverMockedConstruction = mockConstruction(AndroidDriver.class, (mock, context) -> {
            assertEquals(url, context.arguments().getFirst());
            assertEquals(uiAutomator2Options, context.arguments().get(1));
        });

        assertEquals(appiumEnvironment.setupFor(browser), androidDriverMockedConstruction.constructed().getFirst());

        androidDriverMockedConstruction.close();
    }

    @Test
    @DisplayName("shutdown should just close the driverService")
    public void shutdown() {
        appiumEnvironment.shutdown();

        verify(driverService).close();
    }
}