package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.TestYaml;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.YamlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.DataResolver.DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    private Configuration.Data dataConfiguration;

    @Mock
    private Parameter parameter;

    @Mock
    private Type type;

    @Mock
    private YamlUtils yamlUtils;

    @Mock
    private TestYaml data;

    @Captor
    private ArgumentCaptor<Function<String, TestYaml>> runnableArgumentCaptor;

    @InjectMocks
    private DataResolver<TestYaml> dataResolver;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("yamlUtils", dataResolver, yamlUtils);
    }

    @DisplayName("supportsParameter should check if the generic type name is exactly Data")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    void supportsParameter(final String typeName, final boolean expected) {
        when(parameterContext.getParameter()).thenReturn(parameter);
        when(parameter.getParameterizedType()).thenReturn(type);
        when(type.getTypeName()).thenReturn(typeName);

        assertEquals(expected, dataResolver.supportsParameter(parameterContext, extensionContext));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("Data", true),
                arguments("not-good", false)
        );
    }

    @DisplayName("resolveParameter should load the data class from client side and deserialize the data.yaml on it")
    @ParameterizedTest(name = "with class {0}")
    @ValueSource(classes = {TestClass.class, TestParentClass.class})
    void resolveParameter(final Class<?> clazz) {
        final String dataFolder = "dataFolder";

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getData()).thenReturn(dataConfiguration);
        when(dataConfiguration.getFolder()).thenReturn(dataFolder);

        doReturn(clazz).when(extensionContext).getRequiredTestClass();

        when(yamlUtils.readClient(eq(dataFolder + "/data.yaml"), any())).thenReturn(data);

        dataResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).computeIfAbsent(eq(DATA), runnableArgumentCaptor.capture(), any());
        Function<String, TestYaml> function = runnableArgumentCaptor.getValue();
        TestYaml actual = function.apply("value");

        assertEquals(data, actual);
    }

    @DisplayName("resolveParameter should return null if Void is provided as generic Data")
    @ParameterizedTest(name = "with class {0}")
    @ValueSource(classes = {VoidClass.class, VoidParentClass.class})
    void resolveParameterVoid(final Class<?> clazz) {
        doReturn(clazz).when(extensionContext).getRequiredTestClass();

        assertNull(dataResolver.resolveParameter(parameterContext, extensionContext));

        verifyNoInteractions(yamlUtils);
        verifyNoInteractions(rootContext);
    }

    private static class Parent extends SpectrumTest<String> {
    }

    private static final class TestClass extends SpectrumTest<String> {
    }

    private static final class TestParentClass extends Parent {
    }

    private static class ParentVoid extends SpectrumTest<Void> {
    }

    private static final class VoidClass extends SpectrumTest<Void> {
    }

    private static final class VoidParentClass extends ParentVoid {
    }
}
