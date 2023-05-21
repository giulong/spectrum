package com.giuliolongfils.spectrum.extensions.watchers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.utils.testbook.TestBook;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookStatistics;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.giuliolongfils.spectrum.pojos.testbook.TestBookResult.Status.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class TestBookWatcher implements TestWatcher {

    public static final String SEPARATOR = "::";

    private static int total;

    @Override
    public void testDisabled(final ExtensionContext context, final Optional<String> reason) {
        updateTestBook(context, DISABLED);
    }

    @Override
    public void testSuccessful(final ExtensionContext context) {
        updateTestBook(context, SUCCESSFUL);
    }

    @Override
    public void testAborted(final ExtensionContext context, final Throwable throwable) {
        updateTestBook(context, ABORTED);
    }

    @Override
    public void testFailed(final ExtensionContext context, final Throwable exception) {
        updateTestBook(context, FAILED);
    }

    public void updateTestBook(final ExtensionContext context, final TestBookResult.Status status) {
        final TestBook testBook = context.getRoot().getStore(GLOBAL).get(CONFIGURATION, Configuration.class).getApplication().getTestBook();
        final String fullName = String.format("%s%s%s", context.getParent().orElseThrow().getDisplayName(), SEPARATOR, context.getDisplayName());

        if (testBook.getTests().containsKey(fullName)) {
            log.debug("Setting TestBook status {} for test '{}'", status, fullName);
            testBook.getTests().get(fullName).setStatus(status);
            updateStatistics(testBook.getStatistics(), status);
        } else {
            log.debug("Setting TestBook status {} for unmapped test '{}'", status, fullName);
            testBook.getUnmappedTests().put(fullName, new TestBookResult(status));
        }

        updateGrandTotalStatistics(testBook.getStatistics(), status);
    }

    public void updateStatistics(final TestBookStatistics statistics, final TestBookResult.Status status) {
        final int incremented;

        switch (status) {
            case SUCCESSFUL -> incremented = statistics.getSuccessful().incrementAndGet();
            case FAILED -> incremented = statistics.getFailed().incrementAndGet();
            case ABORTED -> incremented = statistics.getAborted().incrementAndGet();
            case DISABLED -> incremented = statistics.getDisabled().incrementAndGet();
            default -> incremented = -1;
        }

        log.trace("TestBook statistics updated total of {} tests: {}", status, incremented);
    }

    public void updateGrandTotalStatistics(final TestBookStatistics statistics, final TestBookResult.Status status) {
        final int incremented;
        log.debug("Updating grand total testBook statistics. Grand Total: {}", ++total);

        switch (status) {
            case SUCCESSFUL -> incremented = statistics.getGrandTotalSuccessful().incrementAndGet();
            case FAILED -> incremented = statistics.getGrandTotalFailed().incrementAndGet();
            case ABORTED -> incremented = statistics.getGrandTotalAborted().incrementAndGet();
            case DISABLED -> incremented = statistics.getGrandTotalDisabled().incrementAndGet();
            default -> incremented = -1;
        }

        log.trace("Grand Total TestBook statistics updated total of {} tests: {}", status, incremented);
    }
}
