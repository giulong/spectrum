package io.github.giulong.spectrum.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

class ContextManagerTest {

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext parentContext;

    @Mock
    private TestContext testContext;

    @InjectMocks
    private ContextManager contextManager;

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(ContextManager.getInstance(), ContextManager.getInstance());
    }

    @Test
    @DisplayName("initFor should put the provided TestContext for the provided context")
    void initFor() {
        final String uniqueId = "uniqueId";
        final Map<String, TestContext> testContexts = new HashMap<>();

        Reflections.setField("testContexts", contextManager, testContexts);
        when(context.getUniqueId()).thenReturn(uniqueId);

        final TestContext actual = contextManager.initFor(context, testContext);

        assertEquals(testContext, actual);
        assertEquals(Map.of(uniqueId, actual), testContexts);
    }

    @Test
    @DisplayName("initFor should put a new TestContext for the provided context and return it")
    void initForContext() {
        final String uniqueId = "uniqueId";
        final Map<String, TestContext> testContexts = new HashMap<>();
        final MockedConstruction<TestContext> testContextMockedConstruction = mockConstruction(TestContext.class);

        Reflections.setField("testContexts", contextManager, testContexts);
        when(context.getUniqueId()).thenReturn(uniqueId);

        final TestContext actual = contextManager.initFor(context);

        assertEquals(testContextMockedConstruction.constructed().getFirst(), actual);
        assertEquals(Map.of(uniqueId, actual), testContexts);

        testContextMockedConstruction.close();
    }

    @Test
    @DisplayName("initWithParentFor should put the parent TestContext for the provided context and return it")
    void initWithParentFor() {
        final String uniqueId = "uniqueId";
        final String parentUniqueId = "parentUniqueId";
        final Map<String, TestContext> testContexts = new HashMap<>() {{
            put(parentUniqueId, testContext);
        }};

        Reflections.setField("testContexts", contextManager, testContexts);
        when(context.getUniqueId()).thenReturn(uniqueId);
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getUniqueId()).thenReturn(parentUniqueId);

        final TestContext actual = contextManager.initWithParentFor(context);

        assertEquals(Map.of(parentUniqueId, actual, uniqueId, actual), testContexts);
        assertEquals(testContext, actual);
    }

    @Test
    @DisplayName("put should insert the provided key value pair in the testContext bound to the provided context")
    void put() {
        final String uniqueId = "uniqueId";
        final String key = "key";
        final String value = "value";

        Reflections.setField("testContexts", contextManager, Map.of(uniqueId, testContext));
        when(context.getUniqueId()).thenReturn(uniqueId);

        contextManager.put(context, key, value);

        verify(testContext).put(key, value);
    }

    @Test
    @DisplayName("get should return the testContext associated to the provided context if present")
    void get() {
        final String uniqueId = "uniqueId";

        when(context.getUniqueId()).thenReturn(uniqueId);

        Reflections.setField("testContexts", contextManager, Map.of(uniqueId, testContext));

        assertEquals(testContext, contextManager.get(context));
    }

    @Test
    @DisplayName("get should return a new testContext if no one is yet associated to the provided context")
    void getNew() {
        final String uniqueId = "uniqueId";
        final Map<String, TestContext> testContexts = new HashMap<>();
        final MockedConstruction<TestContext> testContextMockedConstruction = mockConstruction(TestContext.class);

        when(context.getUniqueId()).thenReturn(uniqueId);

        Reflections.setField("testContexts", contextManager, testContexts);

        final TestContext actual = contextManager.get(context);

        assertEquals(testContextMockedConstruction.constructed().getFirst(), actual);
        assertEquals(Map.of(uniqueId, actual), testContexts);

        testContextMockedConstruction.close();
    }

    @Test
    @DisplayName("get should return the value associated to the provided key in the provided context")
    void getKeyValue() {
        final String uniqueId = "uniqueId";
        final String key = "key";
        final String value = "value";
        final Class<?> clazz = String.class;

        when(context.getUniqueId()).thenReturn(uniqueId);
        doReturn(value).when(testContext).get(key, clazz);

        Reflections.setField("testContexts", contextManager, Map.of(uniqueId, testContext));

        assertEquals(value, contextManager.get(context, key, clazz));
    }
}
