package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.TestContext;
import io.github.giulong.spectrum.utils.ContextManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class TestContextResolver extends TypeBasedParameterResolver<TestContext> {

    public static final String TEST_CONTEXT = "testContext";

    private final ContextManager contextManager = ContextManager.getInstance();

    @Override
    public TestContext resolveParameter(final ParameterContext arg0, final ExtensionContext context) {
        log.debug("Resolving {}", TEST_CONTEXT);

        final TestContext testContext = contextManager.initFor(context);
        context.getStore(GLOBAL).put(TEST_CONTEXT, testContext);

        return testContext;
    }
}
