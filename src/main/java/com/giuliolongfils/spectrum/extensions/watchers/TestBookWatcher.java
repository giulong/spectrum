package com.giuliolongfils.spectrum.extensions.watchers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import com.giuliolongfils.spectrum.utils.testbook.TestBook;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

import static com.giuliolongfils.spectrum.extensions.SpectrumExtension.CLASS_NAME;
import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.giuliolongfils.spectrum.pojos.testbook.TestBookResult.Status.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class TestBookWatcher implements TestWatcher {

    public static final String SEPARATOR = "::";

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
        final String className = context.getStore(GLOBAL).get(CLASS_NAME, String.class);
        final TestBook testBook = context.getRoot().getStore(GLOBAL).get(CONFIGURATION, Configuration.class).getApplication().getTestBook();
        final String fullName = String.format("%s%s%s", className, SEPARATOR, context.getDisplayName());

        if (testBook.getTests().containsKey(fullName)) {
            log.debug("Setting TestBook status {} for test '{}'", status, fullName);
            testBook.getTests().get(fullName).setStatus(status);
        } else {
            log.debug("Setting TestBook status {} for unmapped test '{}'", status, fullName);
            testBook.getUnmappedTests().put(fullName, new TestBookResult(status));
        }
    }
}
