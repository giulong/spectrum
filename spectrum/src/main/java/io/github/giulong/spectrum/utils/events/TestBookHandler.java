package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.pojos.testbook.TestBookStatistics;
import io.github.giulong.spectrum.pojos.testbook.TestBookTest;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class TestBookHandler extends EventHandler {

    public void handle(final Event event) {
        final ExtensionContext context = event.getContext();
        final TestBook testBook = context.getRoot().getStore(GLOBAL).get(CONFIGURATION, Configuration.class).getTestBook();

        if (testBook == null) {
            return;
        }

        final Result result = event.getResult();
        final TestBookStatistics statistics = testBook.getStatistics();
        final Map<String, TestBookTest> mappedTests = testBook.getMappedTests();
        final String className = context.getParent().orElseThrow().getDisplayName();
        final String testName = context.getDisplayName();
        final String fullName = String.format("%s %s", className, testName);

        statistics.getGrandTotalCount().get(result).getTotal().incrementAndGet();

        if (mappedTests.containsKey(fullName)) {
            log.debug("Setting TestBook result {} for test '{}'", result, fullName);
            final TestBookTest actualTest = mappedTests.get(fullName);
            final int weight = actualTest.getWeight();

            actualTest.setResult(result);
            statistics.getTotalCount().get(result).getTotal().incrementAndGet();
            statistics.getTotalWeightedCount().get(result).getTotal().addAndGet(weight);
            statistics.getGrandTotalWeightedCount().get(result).getTotal().addAndGet(weight);
            updateGroupedTests(testBook.getGroupedMappedTests(), className, actualTest);
        } else {
            final TestBookTest unmappedTest = TestBookTest.builder()
                    .className(className)
                    .testName(testName)
                    .result(result)
                    .build();

            log.debug("Setting TestBook result {} for unmapped test '{}'", result, unmappedTest);
            testBook.getUnmappedTests().put(fullName, unmappedTest);
            statistics.getGrandTotalWeightedCount().get(result).getTotal().incrementAndGet();
            updateGroupedTests(testBook.getGroupedUnmappedTests(), className, unmappedTest);
        }
    }

    public void updateGroupedTests(final Map<String, Set<TestBookTest>> groupedTests, final String className, final TestBookTest test) {
        final Set<TestBookTest> tests = groupedTests.getOrDefault(className, new HashSet<>());
        tests.add(test);
        groupedTests.put(className, tests);
    }
}
