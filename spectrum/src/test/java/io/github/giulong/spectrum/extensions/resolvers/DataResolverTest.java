package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.TestYaml;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.YamlUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;

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

    private static MockedStatic<YamlUtils> yamlUtilsMockedStatic;

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
    public void beforeEach() {
        yamlUtilsMockedStatic = mockStatic(YamlUtils.class);
    }

    @AfterEach
    public void afterEach() {
        yamlUtilsMockedStatic.close();
    }

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
        final String dataFolder = "dataFolder";

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getData()).thenReturn(dataConfiguration);
        when(dataConfiguration.getFolder()).thenReturn(dataFolder);
        when(dataConfiguration.getFqdn()).thenReturn("io.github.giulong.spectrum.TestYaml");

        when(YamlUtils.getInstance()).thenReturn(yamlUtils);
        when(yamlUtils.read(eq(dataFolder + "/data.yaml"), any())).thenReturn(data);

        dataResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).getOrComputeIfAbsent(eq(DATA), runnableArgumentCaptor.capture(), any());
        Function<String, TestYaml> function = runnableArgumentCaptor.getValue();
        TestYaml actual = function.apply("value");

        assertEquals(data, actual);
    }

    @Test
    @DisplayName("resolveParameter should return null if the provided fqdn is not a valid class name")
    public void resolveParameterException() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getData()).thenReturn(dataConfiguration);
        when(dataConfiguration.getFqdn()).thenReturn("invalid");

        assertNull(dataResolver.resolveParameter(parameterContext, extensionContext));
        verify(rootStore, never()).getOrComputeIfAbsent(eq(DATA), runnableArgumentCaptor.capture(), any());
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("Data", true),
                arguments("not-good", false)
        );
    }
}
