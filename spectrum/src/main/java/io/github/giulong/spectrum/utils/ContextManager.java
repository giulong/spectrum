package io.github.giulong.spectrum.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class ContextManager {

    private static final ContextManager INSTANCE = new ContextManager();

    public static ContextManager getInstance() {
        return INSTANCE;
    }

    private final Map<String, TestContext> testContexts = new ConcurrentHashMap<>();

    public void put(final String uniqueId, final TestContext testContext) {
        testContexts.put(uniqueId, testContext);
    }

    public TestContext get(final String uniqueId) {
        return testContexts.get(uniqueId);
    }
}
