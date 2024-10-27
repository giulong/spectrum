package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.TEST_CONTEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestContextResolverTest {

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ContextManager contextManager;

    @Mock
    private TestContext testContext;

    @InjectMocks
    private TestContextResolver testContextResolver;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("contextManager", testContextResolver, contextManager);
    }

    @Test
    @DisplayName("resolveParameter should return an instance of TestContext")
    void resolveParameter() {
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(contextManager.initFor(context)).thenReturn(testContext);

        final TestContext actual = testContextResolver.resolveParameter(parameterContext, context);

        assertEquals(testContext, actual);
        verify(store).put(TEST_CONTEXT, actual);
    }
}
