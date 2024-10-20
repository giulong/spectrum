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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;

import java.io.IOException;
import java.net.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.slf4j.event.Level.INFO;

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
    private Configuration.Environments.Appium.Service service;

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
    void beforeEach() {
        appiumDriverLocalServiceMockedStatic = mockStatic(AppiumDriverLocalService.class);
        appiumLogMockedStatic = mockStatic(AppiumLog.class);

        Reflections.setField("configuration", appiumEnvironment, configuration);
        Reflections.setField("external", appiumEnvironment, false);
    }

    @AfterEach
    void afterEach() {
        appiumDriverLocalServiceMockedStatic.close();
        appiumLogMockedStatic.close();
    }

    @Test
    @DisplayName("sessionOpened should initialise the AppiumDriverLocalService redirecting the logs to slf4j")
    void sessionOpenedLogs() {
        final String ipAddress = "localhost";
        final int port = 123;

        // isRunningOn
        MockedConstruction<ServerSocket> serverSocketMockedConstruction = mockConstruction(ServerSocket.class);
        MockedConstruction<InetSocketAddress> inetSocketAddressMockedConstruction = mockConstruction(InetSocketAddress.class, (mock, context) -> {
            assertEquals(InetAddress.getByName(ipAddress), context.arguments().getFirst());
            assertEquals(port, context.arguments().get(1));
        });

        when(configuration.getEnvironments()).thenReturn(environments);
        when(environments.getAppium()).thenReturn(appium);
        when(appium.getCapabilities()).thenReturn(capabilities);
        when(appium.isCollectServerLogs()).thenReturn(true);
        when(appium.getService()).thenReturn(service);
        when(service.getIpAddress()).thenReturn(ipAddress);
        when(service.getPort()).thenReturn(port);

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
        serverSocketMockedConstruction.close();
        inetSocketAddressMockedConstruction.close();
    }

    @Test
    @DisplayName("sessionOpened should initialise the AppiumDriverLocalService without collecting server logs")
    void sessionOpened() {
        final String ipAddress = "localhost";
        final int port = 123;

        // isRunningOn
        MockedConstruction<ServerSocket> serverSocketMockedConstruction = mockConstruction(ServerSocket.class);
        MockedConstruction<InetSocketAddress> inetSocketAddressMockedConstruction = mockConstruction(InetSocketAddress.class, (mock, context) -> {
            assertEquals(InetAddress.getByName(ipAddress), context.arguments().getFirst());
            assertEquals(port, context.arguments().get(1));
        });

        when(configuration.getEnvironments()).thenReturn(environments);
        when(environments.getAppium()).thenReturn(appium);
        when(appium.getCapabilities()).thenReturn(capabilities);
        when(appium.isCollectServerLogs()).thenReturn(false);
        when(appium.getService()).thenReturn(service);
        when(service.getIpAddress()).thenReturn(ipAddress);
        when(service.getPort()).thenReturn(port);

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
        serverSocketMockedConstruction.close();
        inetSocketAddressMockedConstruction.close();
    }

    @Test
    @DisplayName("sessionOpened should initialise the AppiumDriverLocalService without collecting server logs")
    void sessionOpenedExternal() {
        final String ipAddress = "ipAddress";
        final int port = 123;

        // isRunningOn
        MockedConstruction<ServerSocket> serverSocketMockedConstruction = mockConstructionWithAnswer(ServerSocket.class, answer -> {
            throw new IOException();
        });

        when(configuration.getEnvironments()).thenReturn(environments);
        when(environments.getAppium()).thenReturn(appium);
        when(appium.getService()).thenReturn(service);
        when(service.getIpAddress()).thenReturn(ipAddress);
        when(service.getPort()).thenReturn(port);

        appiumEnvironment.sessionOpened();

        verifyNoInteractions(driverService);

        serverSocketMockedConstruction.close();
    }

    @Test
    @DisplayName("sessionClosed should just stop the driverService")
    void sessionClosed() {
        appiumEnvironment.sessionClosed();

        verify(driverService).stop();
    }

    @Test
    @DisplayName("sessionClosed should do nothing if Appium is an external service")
    void sessionClosedFalse() {
        Reflections.setField("external", appiumEnvironment, true);

        appiumEnvironment.sessionClosed();

        verifyNoInteractions(driverService);
    }

    @Test
    @DisplayName("setupFor should delegate the webDriver construction to the actual subclass, calling the super setFileDetectorFor")
    void setupFor() {
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
    void shutdown() {
        appiumEnvironment.shutdown();

        verify(driverService).close();
    }

    @Test
    @DisplayName("shutdown should do nothing if Appium is an external service")
    void shutdownFalse() {
        Reflections.setField("external", appiumEnvironment, true);

        appiumEnvironment.shutdown();

        verifyNoInteractions(driverService);
    }

    @Test
    @DisplayName("isRunningAt should return true if the provided ip address is not local")
    void isRunningAtRemote() {
        final String ipAddress = "ipAddress";

        assertTrue(appiumEnvironment.isRunningAt(ipAddress, 123));
    }

    @DisplayName("isRunningAt should return true if the provided port is already in use")
    @ParameterizedTest(name = "with ip address {0}")
    @ValueSource(strings = {"localhost", "127.0.0.1", "0.0.0.0"})
    void isRunningAt(final String ipAddress) {
        MockedConstruction<ServerSocket> serverSocketMockedConstruction = mockConstructionWithAnswer(ServerSocket.class, answer -> {
            throw new IOException();
        });

        assertTrue(appiumEnvironment.isRunningAt(ipAddress, 123));

        serverSocketMockedConstruction.close();
    }

    @DisplayName("isRunningAt should return false if the provided port is free")
    @ParameterizedTest(name = "with ip address {0}")
    @ValueSource(strings = {"localhost", "127.0.0.1", "0.0.0.0"})
    void isRunningAtFalse(final String ipAddress) throws IOException {
        final int port = 123;
        MockedConstruction<ServerSocket> serverSocketMockedConstruction = mockConstruction(ServerSocket.class);
        MockedConstruction<InetSocketAddress> inetSocketAddressMockedConstruction = mockConstruction(InetSocketAddress.class, (mock, context) -> {
            assertEquals(InetAddress.getByName("localhost"), context.arguments().getFirst());
            assertEquals(port, context.arguments().get(1));
        });

        assertFalse(appiumEnvironment.isRunningAt(ipAddress, port));

        final ServerSocket serverSocket = serverSocketMockedConstruction.constructed().getFirst();
        verify(serverSocket).setReuseAddress(false);
        verify(serverSocket).bind(inetSocketAddressMockedConstruction.constructed().getFirst(), 50);

        inetSocketAddressMockedConstruction.close();
        serverSocketMockedConstruction.close();
    }
}