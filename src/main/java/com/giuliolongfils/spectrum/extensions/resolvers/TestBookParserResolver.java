package com.giuliolongfils.spectrum.extensions.resolvers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.utils.testbook.TestBookParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class TestBookParserResolver extends TypeBasedParameterResolver<TestBookParser> {

    public static final String TEST_BOOK_PARSER = "testBookParser";

    @Override
    public TestBookParser resolveParameter(final ParameterContext parameterContext, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", TEST_BOOK_PARSER);

        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final TestBookParser testBookParser = rootStore.get(CONFIGURATION, Configuration.class).getApplication().getTestBookParser();

        testBookParser.parseTests();
        rootStore.put(TEST_BOOK_PARSER, testBookParser);
        return testBookParser;
    }
}
