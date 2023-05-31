package com.github.giulong.spectrum.extensions.watchers;

import com.github.giulong.spectrum.enums.TestBookResult;
import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.pojos.testbook.TestBookStatistics;
import com.github.giulong.spectrum.pojos.testbook.TestBookTest;
import com.github.giulong.spectrum.utils.testbook.TestBook;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Map;
import java.util.Optional;

import static com.github.giulong.spectrum.enums.TestBookResult.*;
import static com.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
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

    public void updateTestBook(final ExtensionContext context, final TestBookResult result) {
        final TestBook testBook = context.getRoot().getStore(GLOBAL).get(CONFIGURATION, Configuration.class).getApplication().getTestBook();
        final TestBookStatistics statistics = testBook.getStatistics();
        final Map<String, TestBookTest> tests = testBook.getTests();
        final String fullName = context.getParent().orElseThrow().getDisplayName() + context.getDisplayName();
        final TestBookTest test = TestBookTest.builder()
                .className(context.getParent().orElseThrow().getDisplayName())
                .testName(context.getDisplayName())
                .build();

        if (tests.containsKey(fullName)) {
            log.debug("Setting TestBook result {} for test '{}'", result, fullName);
            final int weight = test.getWeight();

            test.setResult(result);
            statistics.getTotalCount().get(result).getTotal().incrementAndGet();
            statistics.getTotalWeightedCount().get(result).getTotal().addAndGet(weight);
            statistics.getGrandTotalCount().get(result).getTotal().incrementAndGet();
            statistics.getGrandTotalWeightedCount().get(result).getTotal().addAndGet(weight);
        } else {
            log.debug("Setting TestBook result {} for unmapped test '{}'", result, test);
            testBook.getUnmappedTests().put(fullName, test);
            statistics.getGrandTotalCount().get(result).getTotal().incrementAndGet();
            statistics.getGrandTotalWeightedCount().get(result).getTotal().incrementAndGet();
        }
    }
}
