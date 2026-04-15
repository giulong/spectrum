package io.github.giulong.spectrum.generation.driver_builders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

class ChromiumBuilderTest {

    @InjectMocks
    private ChromeBuilder builder;

    @Test
    @DisplayName("buildFrom should create an instance of Chrome with the default args and capabilities")
    void buildFromNoArgs() {
        MockedConstruction<ChromeOptions> chromeOptionsMockedConstruction = mockConstruction(
                (mock, context) -> when(mock.addArguments(List.of("--disable-web-security"))).thenReturn(mock));

        MockedConstruction<ChromeDriver> chromeDriverMockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(chromeOptionsMockedConstruction.constructed().getFirst(), context.arguments().getFirst()));

        final ChromeDriver actual = builder.buildFrom(null, null);

        final ChromeOptions constructed = chromeOptionsMockedConstruction.constructed().getFirst();
        verify(constructed).addArguments(List.of("--disable-web-security"));
        verify(constructed).setCapability("webSocketUrl", true);
        verifyNoMoreInteractions(constructed);

        assertEquals(chromeDriverMockedConstruction.constructed().getFirst(), actual);

        chromeOptionsMockedConstruction.close();
        chromeDriverMockedConstruction.close();
    }

    @Test
    @DisplayName("buildFrom should create an instance of Chrome adding the provided args and capabilities")
    void buildFrom() {
        final String argsProperty = "arg1,arg2";
        final String capabilitiesProperty = "cap1=value1,cap2=value2";

        MockedConstruction<ChromeOptions> chromeOptionsMockedConstruction = mockConstruction(
                (mock, context) -> when(mock.addArguments(List.of("--disable-web-security", "arg1", "arg2"))).thenReturn(mock));

        MockedConstruction<ChromeDriver> chromeDriverMockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(chromeOptionsMockedConstruction.constructed().getFirst(), context.arguments().getFirst()));

        final ChromeDriver actual = builder.buildFrom(argsProperty, capabilitiesProperty);

        final ChromeOptions constructed = chromeOptionsMockedConstruction.constructed().getFirst();
        verify(constructed).addArguments(List.of("--disable-web-security", "arg1", "arg2"));
        verify(constructed).setCapability("webSocketUrl", true);
        verify(constructed).setCapability("cap1", "value1");
        verify(constructed).setCapability("cap2", "value2");
        verifyNoMoreInteractions(constructed);

        assertEquals(chromeDriverMockedConstruction.constructed().getFirst(), actual);

        chromeOptionsMockedConstruction.close();
        chromeDriverMockedConstruction.close();
    }
}
