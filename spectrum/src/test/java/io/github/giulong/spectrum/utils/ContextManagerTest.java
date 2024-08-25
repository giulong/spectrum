package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ContextManagerTest {

    @Mock
    private TestContext testContext;

    @InjectMocks
    private ContextManager contextManager;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(ContextManager.getInstance(), ContextManager.getInstance());
    }

    @Test
    @DisplayName("put should call put on the internal map")
    public void put() {
        final String uniqueId = "uniqueId";

        contextManager.put(uniqueId, testContext);

        @SuppressWarnings("unchecked") final Map<String, TestContext> testContexts = (Map<String, TestContext>) Reflections.getFieldValue("testContexts", contextManager);

        assertEquals(Map.of(uniqueId, testContext), testContexts);
    }

    @Test
    @DisplayName("get should call get on the internal map")
    public void get() {
        final String uniqueId = "uniqueId";
        final Map<String, TestContext> testContexts = Map.of(uniqueId, testContext);

        Reflections.setField("testContexts", contextManager, testContexts);

        assertEquals(testContext, contextManager.get(uniqueId));
    }
}