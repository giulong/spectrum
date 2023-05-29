package com.github.giulong.spectrum.extensions.resolvers;

import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.utils.YamlParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigurationResolver")
class ConfigurationResolverTest {

    private MockedStatic<YamlParser> yamlParserMockedStatic;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private YamlParser yamlParser;

    @Mock
    private Configuration configuration;

    @Captor
    private ArgumentCaptor<Function<String, Configuration>> functionArgumentCaptor;

    @BeforeEach
    public void beforeEach() {
        yamlParserMockedStatic = mockStatic(YamlParser.class);
    }

    @AfterEach
    public void afterEach() {
        yamlParserMockedStatic.close();
        VARS.clear();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of Actions on the current stored WebDriver")
    public void testResolveParameter() {
        final String env = "env";
        final String envConfiguration = String.format("configuration-%s.yaml", env);

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(YamlParser.getInstance()).thenReturn(yamlParser);

        // parseEnv
        when(yamlParser.readInternalNode(ENV_NODE, CONFIGURATION_YAML, String.class)).thenReturn(env);
        when(yamlParser.readInternalNode(ENV_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultEnv");

        // parseVars
        when(yamlParser.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));

        when(yamlParser.readInternal(DEFAULT_CONFIGURATION_YAML, Configuration.class)).thenReturn(configuration);

        final ConfigurationResolver configurationResolver = new ConfigurationResolver();
        configurationResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).getOrComputeIfAbsent(eq(CONFIGURATION), functionArgumentCaptor.capture(), eq(Configuration.class));
        Function<String, Configuration> function = functionArgumentCaptor.getValue();
        final Configuration actual = function.apply("value");

        verify(yamlParser).updateWithFile(configuration, CONFIGURATION_YAML);
        verify(yamlParser).updateWithFile(configuration, envConfiguration);
        verify(rootStore).put(CONFIGURATION, configuration);

        assertEquals(configuration, actual);
    }

    @DisplayName("parseEnv should parse the env node from both the default configuration.yaml and the environment-specific and return the merged value")
    @ParameterizedTest(name = "with env {0} and default env {1} we expect {2}")
    @MethodSource("envValuesProvider")
    public void parseEnv(final String env, final String defaultEnv, final String expected) {
        when(YamlParser.getInstance()).thenReturn(yamlParser);
        final ConfigurationResolver configurationResolver = new ConfigurationResolver();

        when(yamlParser.readInternalNode(ENV_NODE, CONFIGURATION_YAML, String.class)).thenReturn(env);
        when(yamlParser.readInternalNode(ENV_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn(defaultEnv);

        assertEquals(expected, configurationResolver.parseEnv());
    }

    @DisplayName("parseVars should put in the VARS map all the variables read from the configuration yaml files")
    @ParameterizedTest
    @MethodSource("varsValuesProvider")
    public void parseVars(final Map<String, String> defaultVars, final Map<String, String> vars, final Map<String, String> envVars, final Map<String, String> expected) {
        final String envConfiguration = "envConfiguration";

        when(YamlParser.getInstance()).thenReturn(yamlParser);
        final ConfigurationResolver configurationResolver = new ConfigurationResolver();

        when(yamlParser.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(defaultVars);
        when(yamlParser.readNode(VARS_NODE, CONFIGURATION_YAML, Map.class)).thenReturn(vars);
        when(yamlParser.readNode(VARS_NODE, envConfiguration, Map.class)).thenReturn(envVars);

        configurationResolver.parseVars(envConfiguration);
        assertEquals(expected, VARS);
    }

    public static Stream<Arguments> envValuesProvider() {
        return Stream.of(
                arguments("overridden-env", "default-env", "overridden-env"),
                arguments(null, "default-env", "default-env")
        );
    }

    public static Stream<Arguments> varsValuesProvider() {
        return Stream.of(
                arguments(Map.of("one", "one"), null, null, Map.of("one", "one")),
                arguments(Map.of("one", "one"), Map.of("two", "two"), null, Map.of("one", "one", "two", "two")),
                arguments(Map.of("one", "one"), null, Map.of("three", "three"), Map.of("one", "one", "three", "three")),
                arguments(Map.of("one", "one"), Map.of("two", "two"), Map.of("three", "three"), Map.of("one", "one", "two", "two", "three", "three"))
        );
    }
}
