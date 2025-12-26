package io.github.giulong.spectrum.utils;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.junit.jupiter.api.extension.ExtensionContext;

@NoArgsConstructor(access = PRIVATE)
public final class ContextManager {

    private static final ContextManager INSTANCE = new ContextManager();

    public static ContextManager getInstance() {
        return INSTANCE;
    }

    private final Map<String, TestContext> testContexts = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, byte[]> screenshots = new ConcurrentHashMap<>();

    public TestContext initFor(final ExtensionContext context) {
        return initFor(context, new TestContext());
    }

    public TestContext initWithParentFor(final ExtensionContext context) {
        return initFor(context, testContexts.get(context.getParent().orElseThrow().getUniqueId()));
    }

    public void put(final ExtensionContext context, final String key, final Object value) {
        get(context).put(key, value);
    }

    public TestContext get(final ExtensionContext context) {
        final TestContext testContext = testContexts.get(context.getUniqueId());

        if (testContext != null) {
            return testContext;
        }

        return initFor(context);
    }

    public <T> T get(final ExtensionContext context, final String key, final Class<T> clazz) {
        return get(context).get(key, clazz);
    }

    TestContext initFor(final ExtensionContext context, final TestContext testContext) {
        testContexts.put(context.getUniqueId(), testContext);

        return testContext;
    }
}
