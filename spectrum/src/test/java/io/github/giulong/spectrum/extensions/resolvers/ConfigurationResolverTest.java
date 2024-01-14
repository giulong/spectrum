package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigurationResolver")
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

    @BeforeEach
    public void beforeEach() {
        configurationMockedStatic = mockStatic(Configuration.class);
    }

    @AfterEach
    public void afterEach() {
        configurationMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of Actions on the current stored WebDriver")
    public void testResolveParameter() {
        when(Configuration.getInstance()).thenReturn(configuration);

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);

        final ConfigurationResolver configurationResolver = new ConfigurationResolver();
        configurationResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).getOrComputeIfAbsent(eq(CONFIGURATION), functionArgumentCaptor.capture(), eq(Configuration.class));
        Function<String, Configuration> function = functionArgumentCaptor.getValue();
        final Configuration actual = function.apply("value");

        verify(rootStore).put(CONFIGURATION, configuration);
        assertEquals(configuration, actual);
    }
}
