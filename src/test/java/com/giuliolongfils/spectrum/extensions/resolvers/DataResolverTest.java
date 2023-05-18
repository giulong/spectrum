package com.giuliolongfils.spectrum.extensions.resolvers;

import com.giuliolongfils.spectrum.TestYaml;
import com.giuliolongfils.spectrum.pojos.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.giuliolongfils.spectrum.extensions.resolvers.DataResolver.DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataResolver")
class DataResolverTest {

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Data data;

    @Mock
    private Parameter parameter;

    @Mock
    private Type type;

    @Captor
    private ArgumentCaptor<Function<String, TestYaml>> runnableArgumentCaptor;

    @Captor
    private ArgumentCaptor<TestYaml> testYamlArgumentCaptor;

    @InjectMocks
    private DataResolver<TestYaml> dataResolver;

    @DisplayName("supportsParameter should check if the generic type name is exactly Data")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void supportsParameter(final String typeName, final boolean expected) {
        when(parameterContext.getParameter()).thenReturn(parameter);
        when(parameter.getParameterizedType()).thenReturn(type);
        when(type.getTypeName()).thenReturn(typeName);

        assertEquals(expected, dataResolver.supportsParameter(parameterContext, extensionContext));
    }

    @Test
    @DisplayName("resolveParameter should load the data class from client side and deserialize the data.yaml on it")
    public void resolveParameter() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getData()).thenReturn(data);
        when(data.getFqdn()).thenReturn("com.giuliolongfils.spectrum.TestYaml");

        dataResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).getOrComputeIfAbsent(eq(DATA), runnableArgumentCaptor.capture(), any());
        Function<String, TestYaml> function = runnableArgumentCaptor.getValue();
        TestYaml actual = function.apply("value");

        verify(rootStore).put(eq(DATA), testYamlArgumentCaptor.capture());
        assertEquals(testYamlArgumentCaptor.getValue(), actual);
    }

    @Test
    @DisplayName("resolveParameter should throw an exception if the provided fqdn is not a valid class name")
    public void resolveParameterException() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getData()).thenReturn(data);
        when(data.getFqdn()).thenReturn("invalid");

        assertThrows(ClassNotFoundException.class, () -> dataResolver.resolveParameter(parameterContext, extensionContext));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("Data", true),
                arguments("not-good", false)
        );
    }
}