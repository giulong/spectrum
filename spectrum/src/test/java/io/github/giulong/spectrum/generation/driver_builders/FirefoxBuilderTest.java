package io.github.giulong.spectrum.generation.driver_builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

class FirefoxBuilderTest {

    @InjectMocks
    private FirefoxBuilder builder;

    @Test
    @DisplayName("buildFrom should create an instance of Firefox with the default args and capabilities")
    void buildFromNoArgs() {
        MockedConstruction<FirefoxOptions> optionsMockedConstruction = mockConstruction(
                (mock, context) -> {
                    when(mock.addArguments(List.of())).thenReturn(mock);
                    when(mock.enableBiDi()).thenReturn(mock);
                });

        MockedConstruction<FirefoxDriver> driverMockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(optionsMockedConstruction.constructed().getFirst(), context.arguments().getFirst()));

        final FirefoxDriver actual = builder.buildFrom(null, null);

        final FirefoxOptions constructed = optionsMockedConstruction.constructed().getFirst();
        verify(constructed).addArguments(List.of());
        verify(constructed).enableBiDi();
        verifyNoMoreInteractions(constructed);

        assertEquals(driverMockedConstruction.constructed().getFirst(), actual);

        optionsMockedConstruction.close();
        driverMockedConstruction.close();
    }

    @Test
    @DisplayName("buildFrom should create an instance of Firefox adding the provided args and capabilities")
    void buildFrom() {
        final String argsProperty = "arg1,arg2";
        final String capabilitiesProperty = "cap1=value1,cap2=value2";

        MockedConstruction<FirefoxOptions> optionsMockedConstruction = mockConstruction(
                (mock, context) -> {
                    when(mock.addArguments(List.of("arg1", "arg2"))).thenReturn(mock);
                    when(mock.enableBiDi()).thenReturn(mock);
                });

        MockedConstruction<FirefoxDriver> driverMockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(optionsMockedConstruction.constructed().getFirst(), context.arguments().getFirst()));

        final FirefoxDriver actual = builder.buildFrom(argsProperty, capabilitiesProperty);

        final FirefoxOptions constructed = optionsMockedConstruction.constructed().getFirst();
        verify(constructed).addArguments(List.of("arg1", "arg2"));
        verify(constructed).setCapability("cap1", "value1");
        verify(constructed).setCapability("cap2", "value2");
        verify(constructed).enableBiDi();
        verifyNoMoreInteractions(constructed);

        assertEquals(driverMockedConstruction.constructed().getFirst(), actual);

        optionsMockedConstruction.close();
        driverMockedConstruction.close();
    }
}
