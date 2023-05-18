package com.giuliolongfils.spectrum.extensions.resolvers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataResolver")
class DataResolverTest {

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private Parameter parameter;

    @Mock
    private Type type;

    @InjectMocks
    private DataResolver<?> dataResolver;

    @DisplayName("supportsParameter should check if the generic type name is exactly Data")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void supportsParameter(String typeName, boolean expected) {
        when(parameterContext.getParameter()).thenReturn(parameter);
        when(parameter.getParameterizedType()).thenReturn(type);
        when(type.getTypeName()).thenReturn(typeName);

        assertEquals(expected, dataResolver.supportsParameter(parameterContext, extensionContext));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("Data", true),
                arguments("not-good", false)
        );
    }
}