package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.github.giulong.spectrum.drivers.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private DriverDeserializer driverDeserializer;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(DriverDeserializer.getInstance(), DriverDeserializer.getInstance());
    }

    @DisplayName("deserialize should delegate to the parent method passing the string value")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void deserialize(final String value, final Driver<?, ?, ?> expected) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(value);
        when(jsonParser.currentName()).thenReturn("key");

        assertInstanceOf(expected.getClass(), driverDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @DisplayName("deserialize should throw an exception if the provided key is not a valid driver name")
    public void deserializeNotExisting() throws IOException {
        String notValidDriver = "notValidDriver";
        when(jsonParser.getValueAsString()).thenReturn(notValidDriver);
        when(jsonParser.currentName()).thenReturn("key");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> driverDeserializer.deserialize(jsonParser, deserializationContext));
        assertEquals("Value '" + notValidDriver + "' is not a valid driver!", exception.getMessage());
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("${justToTestInterpolation:-chrome}", mock(Chrome.class)),
                arguments("chrome", mock(Chrome.class)),
                arguments("firefox", mock(Firefox.class)),
                arguments("edge", mock(Edge.class)),
                arguments("safari", mock(Safari.class)),
                arguments("uiAutomator2", mock(UiAutomator2.class)),
                arguments("espresso", mock(Espresso.class)),
                arguments("xcuiTest", mock(XCUITest.class)),
                arguments("windows", mock(Windows.class)),
                arguments("mac2", mock(Mac2.class)),
                arguments("appiumGeneric", mock(AppiumGeneric.class))
        );
    }
}
