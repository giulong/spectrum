package io.github.giulong.spectrum.extensions.resolvers.bidi;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.lang.reflect.Parameter;
import java.util.stream.Stream;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;

import lombok.Getter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.HasBiDi;

class BiDiTypeBasedParameterResolverTest {

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private WebDriver driver;

    @Mock(extraInterfaces = HasBiDi.class)
    private WebDriver bidiDriver;

    @Mock
    private Parameter parameter;

    @InjectMocks
    private DummyBiDiTypeBasedParameterResolver biDiTypeBasedParameterResolver;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", biDiTypeBasedParameterResolver, configuration);
    }

    @DisplayName("supportsParameter should check if the provided parameter type matches the concrete instance type")
    @ParameterizedTest(name = "with concrete type {0} we expect {1}")
    @MethodSource("valuesProvider")
    void supportsParameter(final Class<?> clazz, final boolean expected) {
        when(parameterContext.getParameter()).thenReturn(parameter);
        doReturn(clazz).when(parameter).getType();

        assertEquals(expected, biDiTypeBasedParameterResolver.supportsParameter(parameterContext, context));

        verifyNoInteractions(context);
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(String.class, true),
                arguments(Object.class, false));
    }

    @Test
    @DisplayName("resolveParameter should do nothing if the provided driver doesn't support BiDi")
    void resolveParameter() {
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(DRIVER, WebDriver.class)).thenReturn(driver);
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.isBiDi()).thenReturn(false);

        assertNull(biDiTypeBasedParameterResolver.resolveParameter(parameterContext, context));

        verifyNoInteractions(parameterContext);
        verifyNoMoreInteractions(context);
        verifyNoMoreInteractions(driver);
        verifyNoMoreInteractions(store);
    }

    @Test
    @DisplayName("resolveParameter should delegate to the concrete instance to resolve the parameter if the provided driver supports BiDi")
    void resolveParameterBiDi() {
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(DRIVER, WebDriver.class)).thenReturn(bidiDriver);
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.isBiDi()).thenReturn(true);

        assertEquals("parameter", biDiTypeBasedParameterResolver.resolveParameter(parameterContext, context));

        verify(store).put("dummy", "parameter");

        verifyNoInteractions(parameterContext);
        verifyNoMoreInteractions(context);
        verifyNoMoreInteractions(driver);
    }

    @Getter
    private static final class DummyBiDiTypeBasedParameterResolver extends BiDiTypeBasedParameterResolver<String> {

        private final String key = "dummy";

        private final Class<String> type = String.class;

        @Override
        public String resolveParameterFor(final WebDriver driver) {
            return "parameter";
        }
    }
}
