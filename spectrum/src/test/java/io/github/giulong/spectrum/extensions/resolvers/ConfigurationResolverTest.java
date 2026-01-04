package io.github.giulong.spectrum.extensions.resolvers;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import io.github.giulong.spectrum.utils.Configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;

class ConfigurationResolverTest {

    private static MockedStatic<Configuration> configurationMockedStatic;

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

    @Captor
    private ArgumentCaptor<Function<String, Configuration>> functionArgumentCaptor;

    @InjectMocks
    private ConfigurationResolver configurationResolver;

    @BeforeEach
    void beforeEach() {
        configurationMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        configurationMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of Actions on the current stored WebDriver")
    void testResolveParameter() {
        when(Configuration.getInstance()).thenReturn(configuration);

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.computeIfAbsent(eq(CONFIGURATION), functionArgumentCaptor.capture(), eq(Configuration.class))).thenReturn(configuration);

        final Configuration actual = configurationResolver.resolveParameter(parameterContext, extensionContext);

        Function<String, Configuration> function = functionArgumentCaptor.getValue();
        final Configuration captured = function.apply("value");

        assertEquals(configuration, actual);
        assertEquals(configuration, captured);
    }
}
