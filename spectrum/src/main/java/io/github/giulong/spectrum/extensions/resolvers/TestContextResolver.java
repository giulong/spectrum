package io.github.giulong.spectrum.extensions.resolvers;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.TestContext;

import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class TestContextResolver extends TypeBasedParameterResolver<TestContext> {

    public static final String TEST_CONTEXT = "testContext";
    public static final String EXTENSION_CONTEXT = "extensionContext";

    private final ContextManager contextManager = ContextManager.getInstance();

    @Override
    public TestContext resolveParameter(@NonNull final ParameterContext parameterContext, @NonNull final ExtensionContext context) {
        log.debug("Resolving {}", TEST_CONTEXT);

        final TestContext testContext = contextManager.initFor(context);
        context.getStore(GLOBAL).put(TEST_CONTEXT, testContext);
        testContext.put(EXTENSION_CONTEXT, context);

        return testContext;
    }
}
