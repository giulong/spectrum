package com.giuliolongfils.spectrum.internals.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.giuliolongfils.spectrum.browsers.*;
import org.junit.jupiter.api.BeforeAll;
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

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.VARS;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BrowserDeserializer")
class BrowserDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private BrowserDeserializer browserDeserializer;

    private static final String varInEnv = "varInEnv";

    @BeforeAll
    public static void beforeAll() {
        VARS.put("varInEnv", varInEnv);
    }

    @DisplayName("deserialize should delegate to the parent method passing the string value")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void deserialize(final String value, final Browser<?> expected) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(value);
        when(jsonParser.currentName()).thenReturn("key");

        assertInstanceOf(expected.getClass(), browserDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @DisplayName("deserialize should throw an exception if the provided key is not a valid browser name")
    public void deserializeNotExisting() throws IOException {
        String notValidBrowser = "notValidBrowser";
        when(jsonParser.getValueAsString()).thenReturn(notValidBrowser);
        when(jsonParser.currentName()).thenReturn("key");

        Exception exception = assertThrows(RuntimeException.class, () -> browserDeserializer.deserialize(jsonParser, deserializationContext));
        assertEquals("Value '" + notValidBrowser + "' is not a valid browser!", exception.getMessage());
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("${justToTestInterpolation:-chrome}", mock(Chrome.class)),
                arguments("chrome", mock(Chrome.class)),
                arguments("firefox", mock(Firefox.class)),
                arguments("ie", mock(InternetExplorer.class)),
                arguments("edge", mock(Edge.class))
        );
    }
}