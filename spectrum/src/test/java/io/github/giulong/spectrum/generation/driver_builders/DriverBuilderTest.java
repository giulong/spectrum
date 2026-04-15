package io.github.giulong.spectrum.generation.driver_builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DriverBuilderTest {

    @DisplayName("getFor should return an instance of the driver builder corresponding to the provided driver name")
    @ParameterizedTest(name = "with name {0} we expect {1}")
    @MethodSource("valuesProvider")
    void getFor(final String name, final Class<? extends DriverBuilder<?>> builder) {
        assertInstanceOf(builder, DriverBuilder.getFor(name));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("chrome", ChromeBuilder.class),
                arguments("edge", EdgeBuilder.class)
        );
    }

    @Test
    @DisplayName("getFor should throw an exception if the provided name is not valid")
    void getForException() {
        final Exception exception = assertThrows(IllegalArgumentException.class, () -> DriverBuilder.getFor("nope"));
        assertEquals("Value 'nope' is not a valid driver!", exception.getMessage());
    }
}
