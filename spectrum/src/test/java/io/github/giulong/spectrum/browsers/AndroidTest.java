package io.github.giulong.spectrum.browsers;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Android")
class AndroidTest {

    @Mock
    private UiAutomator2Options uiAutomator2Options;

    @Mock
    private WebDriver androidWebDriver;

    @Mock
    private WebDriver.Options options;

    @Mock
    private WebDriver.Timeouts timeouts;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.WebDriver webDriver;

    @Mock
    private Configuration.WebDriver.Waits waits;

    @Mock
    private Duration duration;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Map<String, String> gridCapabilities;

    @Mock
    private Configuration.WebDriver.Android androidConfiguration;

    @Captor
    private ArgumentCaptor<DesiredCapabilities> desiredCapabilitiesArgumentCaptor;

    @InjectMocks
    private Android android;

    @BeforeEach
    public void beforeEach() {
        Reflections.setParentField("configuration", android, android.getClass().getSuperclass(), configuration);
        Reflections.setParentField("capabilities", android, android.getClass().getSuperclass(), uiAutomator2Options);
    }

    @Test
    @DisplayName("getDriverServiceBuilder should return an instance of AppiumDriverServiceBuilder")
    public void getDriverServiceBuilder() {
        MockedConstruction<AppiumServiceBuilder> appiumServiceBuilderMockedConstruction = mockConstruction(AppiumServiceBuilder.class);

        final AppiumServiceBuilder actual = (AppiumServiceBuilder) android.getDriverServiceBuilder();
        assertEquals(appiumServiceBuilderMockedConstruction.constructed().getFirst(), actual);

        appiumServiceBuilderMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of UiAutomator2Options and set the capabilities from the yaml on it")
    public void buildCapabilities() {
        MockedConstruction<UiAutomator2Options> desiredCapabilitiesMockedConstruction = mockConstruction(UiAutomator2Options.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getWebDriver()).thenReturn(webDriver);
        when(webDriver.getAndroid()).thenReturn(androidConfiguration);
        when(androidConfiguration.getCapabilities()).thenReturn(capabilities);

        android.buildCapabilities();

        final UiAutomator2Options actual = (UiAutomator2Options) Reflections.getParentFieldValue("capabilities", android);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities and return the capabilities")
    public void mergeGridCapabilitiesFrom() {
        when(uiAutomator2Options.merge(desiredCapabilitiesArgumentCaptor.capture())).thenReturn(uiAutomator2Options);

        MockedConstruction<DesiredCapabilities> desiredCapabilitiesMockedConstruction = mockConstruction(DesiredCapabilities.class, (mock, context) -> {
            assertEquals(gridCapabilities, context.arguments().getFirst());
        });

        final UiAutomator2Options actual = android.mergeGridCapabilitiesFrom(gridCapabilities);
        verify(uiAutomator2Options).merge(desiredCapabilitiesMockedConstruction.constructed().getFirst());
        assertEquals(actual, uiAutomator2Options);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("configureWaitsOf should configure just the implicitWait, since the others are not implemented")
    public void configureWaitsOf() {
        when(waits.getImplicit()).thenReturn(duration);

        when(androidWebDriver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);

        android.configureWaitsOf(androidWebDriver, waits);

        verify(timeouts).implicitlyWait(duration);
    }
}