package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.WebDriver;

import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AndroidTest {

    @Mock
    private WebDriver androidWebDriver;

    @Mock
    private WebDriver.Options options;

    @Mock
    private WebDriver.Timeouts timeouts;

    @Mock
    private Configuration.Drivers.Waits waits;

    @Mock
    private Duration duration;

    @Mock
    private URL url;

    @Mock
    private UiAutomator2Options capabilities;

    @InjectMocks
    private UiAutomator2 android;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("capabilities", android, capabilities);
    }

    @Test
    @DisplayName("configureWaitsOf should configure just the implicitWait, since the others are not implemented")
    void configureWaitsOf() {
        when(waits.getImplicit()).thenReturn(duration);

        when(androidWebDriver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);

        android.configureWaitsOf(androidWebDriver, waits);

        verify(timeouts).implicitlyWait(duration);
    }

    @Test
    @DisplayName("buildDriverFor should return a new instance of AndroidDriver for the provided url and the instance capabilities")
    void buildDriverFor() {
        MockedConstruction<AndroidDriver> androidDriverMockedConstruction = mockConstruction(AndroidDriver.class, (mock, context) -> {
            assertEquals(url, context.arguments().getFirst());
            assertEquals(capabilities, context.arguments().get(1));
        });

        assertEquals(android.buildDriverFor(url), androidDriverMockedConstruction.constructed().getFirst());

        androidDriverMockedConstruction.close();
    }
}