package io.github.giulong.spectrum.drivers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.ie.InternetExplorerDriverLogLevel;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.service.DriverService;

class InternetExplorerTest {

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Configuration.Drivers driversConfig;

    @Mock
    private Configuration.Drivers.Ie ieConfig;

    @Mock
    private Configuration.Drivers.Ie.Service service;

    @Mock
    private File extractPath;

    @InjectMocks
    private InternetExplorer internetExplorer;

    @Test
    @DisplayName("getDriverServiceBuilder should return a new instance of InternetExplorerDriverService.Builder()")
    void getDriverServiceBuilder() {
        final String host = "host";

        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.getIe()).thenReturn(ieConfig);
        when(ieConfig.getService()).thenReturn(service);
        when(service.getLogLevel()).thenReturn(InternetExplorerDriverLogLevel.TRACE);
        when(service.isSilent()).thenReturn(true);
        when(service.getHost()).thenReturn(host);
        when(service.getExtractPath()).thenReturn(extractPath);

        MockedConstruction<InternetExplorerDriverService.Builder> mockedConstruction = mockConstruction((mock, context) -> {
            when(mock.withLogLevel(InternetExplorerDriverLogLevel.TRACE)).thenReturn(mock);
            when(mock.withSilent(true)).thenReturn(mock);
            when(mock.withHost(host)).thenReturn(mock);
            when(mock.withExtractPath(extractPath)).thenReturn(mock);
        });

        final DriverService.Builder<InternetExplorerDriverService, InternetExplorerDriverService.Builder> driverServiceBuilder = internetExplorer.getDriverServiceBuilder();
        assertEquals(mockedConstruction.constructed().getFirst(), driverServiceBuilder);

        mockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilities should build an instance of InternetExplorerOptions based on the provided configuration")
    void buildCapabilities() {
        final List<String> arguments = List.of("args");

        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.getIe()).thenReturn(ieConfig);
        when(ieConfig.getArgs()).thenReturn(arguments);
        when(ieConfig.getCapabilities()).thenReturn(Map.of("capability", "value"));

        MockedConstruction<InternetExplorerOptions> mockedConstruction = mockConstruction(
                (mock, context) -> when(mock.attachToEdgeChrome()).thenReturn(mock));

        internetExplorer.buildCapabilities();

        final InternetExplorerOptions internetExplorerOptions = mockedConstruction.constructed().getFirst();
        verify(internetExplorerOptions).attachToEdgeChrome();
        verify(internetExplorerOptions).setCapability("capability", (Object) "value");
        verify(internetExplorerOptions).addCommandSwitches("args");

        assertEquals(internetExplorerOptions, Reflections.getFieldValue("capabilities", internetExplorer));

        mockedConstruction.close();
    }
}
