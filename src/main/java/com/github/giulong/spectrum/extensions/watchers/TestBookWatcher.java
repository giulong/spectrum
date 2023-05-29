package com.github.giulong.spectrum.extensions.watchers;

import com.github.giulong.spectrum.pojos.testbook.TestBookStatistics;
import com.github.giulong.spectrum.utils.testbook.TestBook;
import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.pojos.testbook.TestBookResult;
import com.github.giulong.spectrum.pojos.testbook.TestBookTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Map;
import java.util.Optional;

import static com.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.github.giulong.spectrum.pojos.testbook.TestBookResult.Status.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class TestBookWatcher implements TestWatcher {

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
        final TestBookStatistics statistics = testBook.getStatistics();
        final Map<TestBookTest, TestBookResult> tests = testBook.getTests();
        final TestBookTest testToCheck = TestBookTest.builder()
                .className(context.getParent().orElseThrow().getDisplayName())
                .testName(context.getDisplayName())
                .build();

        if (tests.containsKey(testToCheck)) {
            log.debug("Setting TestBook status {} for test '{}'", status, testToCheck);
            final int weight = tests
                    .keySet()
                    .stream()
                    .filter(t -> t.equals(testToCheck))
                    .findFirst()
                    .orElseThrow()
                    .getWeight();

            tests.get(testToCheck).setStatus(status);
            statistics.getTotalCount().get(status).getTotal().incrementAndGet();
            statistics.getTotalWeightedCount().get(status).getTotal().addAndGet(weight);
            statistics.getGrandTotalCount().get(status).getTotal().incrementAndGet();
            statistics.getGrandTotalWeightedCount().get(status).getTotal().addAndGet(weight);
        } else {
            log.debug("Setting TestBook status {} for unmapped test '{}'", status, testToCheck);
            testBook.getUnmappedTests().put(testToCheck, new TestBookResult(status));
            statistics.getGrandTotalCount().get(status).getTotal().incrementAndGet();
            statistics.getGrandTotalWeightedCount().get(status).getTotal().incrementAndGet();
        }
    }
}
