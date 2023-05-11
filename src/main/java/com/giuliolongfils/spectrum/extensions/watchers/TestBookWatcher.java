package com.giuliolongfils.spectrum.extensions.watchers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.TestBookResult;
import com.giuliolongfils.spectrum.utils.testbook.TestBookParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

import static com.giuliolongfils.spectrum.extensions.SpectrumExtension.CLASS_NAME;
import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.giuliolongfils.spectrum.pojos.TestBookResult.Status.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class TestBookWatcher implements TestWatcher {

    @Override
    public void testDisabled(final ExtensionContext context, final Optional<String> reason) {
        updateTestBook(context, SKIPPED);
    }

    @Override
    public void testSuccessful(final ExtensionContext context) {
        updateTestBook(context, PASSED);
    }

    @Override
    public void testAborted(final ExtensionContext context, final Throwable throwable) {
        updateTestBook(context, FAILED);
    }

    @Override
    public void testFailed(final ExtensionContext context, final Throwable exception) {
        updateTestBook(context, FAILED);
    }

    public void updateTestBook(final ExtensionContext context, final TestBookResult.Status status) {
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final TestBookParser testBookParser = rootStore.get(CONFIGURATION, Configuration.class).getApplication().getTestBookParser();

        testBookParser.setStatus(store.get(CLASS_NAME, String.class), context.getDisplayName(), status);
        testBookParser.getOutput().forEach(output -> output.updateWith(testBookParser.getTestBook()));
    }
}
