package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Js;
import io.github.giulong.spectrum.utils.JsWebElementProxyBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.JsResolver.JS;
import static io.github.giulong.spectrum.extensions.resolvers.JsWebElementProxyBuilderResolver.JS_WEB_ELEMENT_PROXY_BUILDER;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JsWebElementProxyBuilderResolver")
class JsWebElementProxyBuilderResolverTest {

    private static MockedStatic<Pattern> patternMockedStatic;
    private static MockedStatic<JsWebElementProxyBuilder> jsWebElementProxyBuilderMockedStatic;

    @Mock
    private JsWebElementProxyBuilder.JsWebElementProxyBuilderBuilder jsWebElementProxyBuilderBuilder;

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
    private Configuration.Extent extent;

    @Mock
    private Pattern pattern;

    @Mock
    private Js js;

    @Mock
    private JsWebElementProxyBuilder jsWebElementProxyBuilder;

    @Mock
    private Method method1;

    @Mock
    private Method method2;

    @Captor
    private ArgumentCaptor<Map<Method, Method>> mapArgumentCaptor;

    @Captor
    private ArgumentCaptor<Function<String, JsWebElementProxyBuilder>> functionArgumentCaptor;

    @InjectMocks
    private JsWebElementProxyBuilderResolver jsWebElementProxyBuilderResolver;

    @BeforeEach
    public void beforeEach() {
        patternMockedStatic = mockStatic(Pattern.class);
        jsWebElementProxyBuilderMockedStatic = mockStatic(JsWebElementProxyBuilder.class);
    }

    @AfterEach
    public void afterEach() {
        patternMockedStatic.close();
        jsWebElementProxyBuilderMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of JsWebElementProxyBuilder")
    public void resolveParameter() {
        final String locatorRegex = "locatorRegex";

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.getOrComputeIfAbsent(eq(JS_WEB_ELEMENT_PROXY_BUILDER), functionArgumentCaptor.capture(), eq(JsWebElementProxyBuilder.class))).thenReturn(jsWebElementProxyBuilder);

        when(rootStore.get(JS, Js.class)).thenReturn(js);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getLocatorRegex()).thenReturn(locatorRegex);
        when(Pattern.compile(locatorRegex)).thenReturn(pattern);

        when(JsWebElementProxyBuilder.builder()).thenReturn(jsWebElementProxyBuilderBuilder);
        when(jsWebElementProxyBuilderBuilder.js(js)).thenReturn(jsWebElementProxyBuilderBuilder);
        when(jsWebElementProxyBuilderBuilder.locatorPattern(pattern)).thenReturn(jsWebElementProxyBuilderBuilder);
        when(jsWebElementProxyBuilderBuilder.methods(mapArgumentCaptor.capture())).thenReturn(jsWebElementProxyBuilderBuilder);
        when(jsWebElementProxyBuilderBuilder.build()).thenReturn(jsWebElementProxyBuilder);

        final JsWebElementProxyBuilder actual = jsWebElementProxyBuilderResolver.resolveParameter(parameterContext, extensionContext);

        Function<String, JsWebElementProxyBuilder> function = functionArgumentCaptor.getValue();
        final JsWebElementProxyBuilder captured = function.apply("value");

        assertEquals(jsWebElementProxyBuilder, actual);
        assertEquals(jsWebElementProxyBuilder, captured);
    }

    @Test
    @DisplayName("methodsEqual should return true if the provided methods have the same signature")
    public void methodsEqual() {
        final String name = "name";
        final Class<String> returnType = String.class;
        final Class<?>[] parameterTypes = new Class<?>[]{String.class, Integer.class};

        when(method1.getName()).thenReturn(name);
        when(method2.getName()).thenReturn(name);
        doReturn(returnType).when(method1).getReturnType();
        doReturn(returnType).when(method2).getReturnType();
        when(method1.getParameterTypes()).thenReturn(parameterTypes);
        when(method2.getParameterTypes()).thenReturn(parameterTypes);

        assertTrue(jsWebElementProxyBuilderResolver.methodsEqual(method1, method2));
    }

    @Test
    @DisplayName("methodsEqual should return false if the provided methods have different names")
    public void methodsEqualFalse1() {
        final String name1 = "name1";
        final String name2 = "name2";

        when(method1.getName()).thenReturn(name1);
        when(method2.getName()).thenReturn(name2);

        assertFalse(jsWebElementProxyBuilderResolver.methodsEqual(method1, method2));
    }

    @Test
    @DisplayName("methodsEqual should return false if the provided methods have different return types")
    public void methodsEqualFalse2() {
        final String name = "name";
        final Class<String> returnType1 = String.class;
        final Class<Integer> returnType2 = Integer.class;

        when(method1.getName()).thenReturn(name);
        when(method2.getName()).thenReturn(name);
        doReturn(returnType1).when(method1).getReturnType();
        doReturn(returnType2).when(method2).getReturnType();

        assertFalse(jsWebElementProxyBuilderResolver.methodsEqual(method1, method2));
    }

    @Test
    @DisplayName("methodsEqual should return false if the provided methods have different parameter types")
    public void methodsEqualFalse3() {
        final String name = "name";
        final Class<String> returnType = String.class;
        final Class<?>[] parameterTypes1 = new Class<?>[]{String.class, Integer.class};
        final Class<?>[] parameterTypes2 = new Class<?>[]{Boolean.class};

        when(method1.getName()).thenReturn(name);
        when(method2.getName()).thenReturn(name);
        doReturn(returnType).when(method1).getReturnType();
        doReturn(returnType).when(method2).getReturnType();
        when(method1.getParameterTypes()).thenReturn(parameterTypes1);
        when(method2.getParameterTypes()).thenReturn(parameterTypes2);

        assertFalse(jsWebElementProxyBuilderResolver.methodsEqual(method1, method2));
    }
}