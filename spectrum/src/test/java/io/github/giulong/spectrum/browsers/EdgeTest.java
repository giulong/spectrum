package io.github.giulong.spectrum.browsers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.service.DriverService;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Edge")
class EdgeTest {

    @Mock
    private Configuration.WebDriver webDriverConfig;

    @Mock
    private Configuration.WebDriver.Edge edgeConfig;

    @Mock
    private Level browserLevel;

    @Mock
    private Level driverLevel;

    @Mock
    private Configuration configuration;

    @Mock
    private Level performanceLevel;

    @Mock
    private Configuration.WebDriver.Logs logs;

    @InjectMocks
    private Edge edge;

    @BeforeEach
    public void beforeEach() {
        Reflections.setParentField("configuration", edge, edge.getClass().getSuperclass().getSuperclass(), configuration);
    }

    @Test
    @DisplayName("getDriverServiceBuilder should return a new instance of EdgeDriverService.Builder()")
    public void getDriverServiceBuilder() {
        MockedConstruction<EdgeDriverService.Builder> chromeDriverServiceMockedConstruction = mockConstruction(EdgeDriverService.Builder.class);

        final DriverService.Builder<EdgeDriverService, EdgeDriverService.Builder> driverServiceBuilder = edge.getDriverServiceBuilder();
        assertEquals(chromeDriverServiceMockedConstruction.constructed().getFirst(), driverServiceBuilder);

        chromeDriverServiceMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilitiesFrom should build an instance of Chrome based on the provided configuration")
    public void buildCapabilitiesFrom() {
        when(configuration.getWebDriver()).thenReturn(webDriverConfig);
        when(webDriverConfig.getEdge()).thenReturn(edgeConfig);
        when(webDriverConfig.getLogs()).thenReturn(logs);
        when(logs.getBrowser()).thenReturn(browserLevel);
        when(logs.getDriver()).thenReturn(driverLevel);
        when(logs.getPerformance()).thenReturn(performanceLevel);

        MockedConstruction<EdgeOptions> edgeOptionsMockedConstruction = mockConstruction(EdgeOptions.class);
        MockedConstruction<LoggingPreferences> loggingPreferencesMockedConstruction = mockConstruction(LoggingPreferences.class);

        edge.buildCapabilities();
        final EdgeOptions edgeOptions = edgeOptionsMockedConstruction.constructed().getFirst();
        final LoggingPreferences loggingPreferences = loggingPreferencesMockedConstruction.constructed().getFirst();

        verify(loggingPreferences).enable(BROWSER, browserLevel);
        verify(loggingPreferences).enable(DRIVER, driverLevel);
        verify(loggingPreferences).enable(PERFORMANCE, performanceLevel);
        verify(edgeOptions).setCapability(LOGGING_PREFS, loggingPreferences);

        edgeOptionsMockedConstruction.close();
        loggingPreferencesMockedConstruction.close();
    }
}
