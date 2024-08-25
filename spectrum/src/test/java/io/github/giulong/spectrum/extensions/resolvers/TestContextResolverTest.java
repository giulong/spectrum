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
import org.mockito.MockedConstruction;

import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.TEST_CONTEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class TestContextResolverTest {

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ContextManager contextManager;

    @InjectMocks
    private TestContextResolver testContextResolver;

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("contextManager", testContextResolver, contextManager);
    }

    @Test
    @DisplayName("resolveParameter should return an instance of TestContext")
    public void resolveParameter() {
        final String uniqueId = "uniqueId";
        final MockedConstruction<TestContext> testContextMockedConstruction = mockConstruction(TestContext.class);

        when(context.getStore(GLOBAL)).thenReturn(store);
        when(context.getUniqueId()).thenReturn(uniqueId);

        final TestContext actual = testContextResolver.resolveParameter(parameterContext, context);

        assertEquals(testContextMockedConstruction.constructed().getFirst(), actual);

        verify(store).put(TEST_CONTEXT, actual);
        verify(contextManager).put(uniqueId, actual);

        testContextMockedConstruction.close();
    }
}