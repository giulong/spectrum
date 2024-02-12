package io.github.giulong.spectrum.browsers;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static io.github.giulong.spectrum.browsers.Android.APP_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UiAutomator2")
class UiAutomator2Test {

    @Mock
    private UiAutomator2Options uiAutomator2Options;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.WebDriver webDriver;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Map<String, Object> gridCapabilities;

    @Mock
    private Configuration.WebDriver.UiAutomator2 uiAutomator2Configuration;

    @Captor
    private ArgumentCaptor<DesiredCapabilities> desiredCapabilitiesArgumentCaptor;

    @InjectMocks
    private UiAutomator2 uiAutomator2;

    @BeforeEach
    public void beforeEach() {
        Reflections.setParentField("configuration", uiAutomator2, uiAutomator2.getClass().getSuperclass().getSuperclass().getSuperclass(), configuration);
        Reflections.setParentField("capabilities", uiAutomator2, uiAutomator2.getClass().getSuperclass().getSuperclass().getSuperclass(), uiAutomator2Options);
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of UiAutomator2Options and set the capabilities from the yaml on it, when a relative path is provided as 'app' capability")
    public void buildCapabilities() {
        final String appPath = "relative/path";
        final String appAbsolutePath = System.getProperty("user.dir") + "/relative/path";

        MockedConstruction<UiAutomator2Options> desiredCapabilitiesMockedConstruction = mockConstruction(UiAutomator2Options.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getWebDriver()).thenReturn(webDriver);
        when(webDriver.getUiAutomator2()).thenReturn(uiAutomator2Configuration);
        when(uiAutomator2Configuration.getCapabilities()).thenReturn(capabilities);

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        uiAutomator2.buildCapabilities();

        final UiAutomator2Options actual = (UiAutomator2Options) Reflections.getParentFieldValue("capabilities", uiAutomator2, uiAutomator2.getClass().getSuperclass().getSuperclass().getSuperclass());
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        verify(capabilities).put(APP_CAPABILITY, appAbsolutePath);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of UiAutomator2Options and set the capabilities from the yaml on it, when an absolute path is provided as 'app' capability")
    public void buildCapabilitiesAbsoluteAppPath() {
        final String appPath = "/absolute/path";

        MockedConstruction<UiAutomator2Options> desiredCapabilitiesMockedConstruction = mockConstruction(UiAutomator2Options.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getWebDriver()).thenReturn(webDriver);
        when(webDriver.getUiAutomator2()).thenReturn(uiAutomator2Configuration);
        when(uiAutomator2Configuration.getCapabilities()).thenReturn(capabilities);

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        uiAutomator2.buildCapabilities();

        final UiAutomator2Options actual = (UiAutomator2Options) Reflections.getParentFieldValue("capabilities", uiAutomator2, uiAutomator2.getClass().getSuperclass().getSuperclass().getSuperclass());
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

        final UiAutomator2Options actual = uiAutomator2.mergeGridCapabilitiesFrom(gridCapabilities);
        verify(uiAutomator2Options).merge(desiredCapabilitiesMockedConstruction.constructed().getFirst());
        assertEquals(actual, uiAutomator2Options);

        desiredCapabilitiesMockedConstruction.close();
    }
}