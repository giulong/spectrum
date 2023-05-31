package com.github.giulong.spectrum.extensions.watchers;

import com.github.giulong.spectrum.enums.Result;
import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.pojos.testbook.TestBookStatistics;
import com.github.giulong.spectrum.pojos.testbook.TestBookStatistics.Statistics;
import com.github.giulong.spectrum.pojos.testbook.TestBookTest;
import com.github.giulong.spectrum.utils.testbook.TestBook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static com.github.giulong.spectrum.enums.Result.*;
import static com.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestBookWatcher")
class TestBookWatcherTest {

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext parentContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Application application;

    @Mock
    private TestBook testBook;

    @Mock
    private TestBookStatistics statistics;

    @Mock
    private TestBookTest test;

    @Mock
    private TestBookTest actualTest;

    @InjectMocks
    private TestBookWatcher testBookWatcher;

    private void updateTestBookStubsFor(final Result result) {
        final Map<String, TestBookTest> unmappedTests = new HashMap<>();
        final Map<Result, Statistics> grandTotalCount = new HashMap<>();
        final Map<Result, Statistics> grandTotalWeightedCount = new HashMap<>();
        final Statistics grandTotalStatistics = new Statistics();
        final Statistics grandTotalWeightedStatistics = new Statistics();
        final Map<String, Set<TestBookTest>> groupedUnmappedTests = new HashMap<>();

        grandTotalStatistics.getTotal().set(123);
        grandTotalCount.put(result, grandTotalStatistics);
        grandTotalWeightedStatistics.getTotal().set(456);
        grandTotalWeightedCount.put(result, grandTotalWeightedStatistics);

        when(context.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getApplication()).thenReturn(application);
        when(application.getTestBook()).thenReturn(testBook);
        when(testBook.getUnmappedTests()).thenReturn(unmappedTests);
        when(testBook.getStatistics()).thenReturn(statistics);
        when(testBook.getGroupedUnmappedTests()).thenReturn(groupedUnmappedTests);
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn("className");
        when(context.getDisplayName()).thenReturn("testName");

        when(statistics.getGrandTotalCount()).thenReturn(grandTotalCount);
        when(statistics.getGrandTotalWeightedCount()).thenReturn(grandTotalWeightedCount);
    }

    private void updateTestBookAssertionsFor(final Result result) {
        final TestBookTest unmappedTest = testBook.getUnmappedTests().get(String.format("%s %s", "className", "testName"));
        assertEquals("className", unmappedTest.getClassName());
        assertEquals("testName", unmappedTest.getTestName());
        assertEquals(result, unmappedTest.getResult());
        assertEquals(124, statistics.getGrandTotalCount().get(result).getTotal().get());
        assertEquals(457, statistics.getGrandTotalWeightedCount().get(result).getTotal().get());
        assertTrue(testBook.getGroupedUnmappedTests().get("className").contains(unmappedTest));
    }

    @Test
    @DisplayName("testDisabled should update the testbook with the currently disabled test")
    public void testDisabled() {
        final Result result = DISABLED;
        updateTestBookStubsFor(result);

        testBookWatcher.testDisabled(context, Optional.of("reason"));
        updateTestBookAssertionsFor(result);
    }

    @Test
    @DisplayName("testSuccessful should update the testbook with the currently successful test")
    public void testSuccessful() {
        final Result result = SUCCESSFUL;
        updateTestBookStubsFor(result);

        testBookWatcher.testSuccessful(context);
        updateTestBookAssertionsFor(result);
    }

    @Test
    @DisplayName("testAborted should update the testbook with the currently aborted test")
    public void testAborted() {
        final Result result = ABORTED;
        updateTestBookStubsFor(result);

        testBookWatcher.testAborted(context, new RuntimeException());
        updateTestBookAssertionsFor(result);
    }

    @Test
    @DisplayName("testFailed should update the testbook with the currently failed test")
    public void testFailed() {
        final Result result = FAILED;
        updateTestBookStubsFor(result);

        testBookWatcher.testFailed(context, new RuntimeException());
        updateTestBookAssertionsFor(result);
    }

    @Test
    @DisplayName("updateTestBook should update the testbook with the currently unmapped test")
    public void updateTestBook() {
        final Result result = FAILED;
        updateTestBookStubsFor(result);

        testBookWatcher.updateTestBook(context, result);
        updateTestBookAssertionsFor(result);
    }

    @Test
    @DisplayName("updateTestBook should update the testbook with the currently mapped test")
    public void updateTestBookMappedTest() {
        final Result result = FAILED;
        final Map<String, TestBookTest> mappedTests = new HashMap<>();
        final Map<Result, Statistics> totalCount = new HashMap<>();
        final Map<Result, Statistics> totalWeightedCount = new HashMap<>();
        final Map<Result, Statistics> grandTotalCount = new HashMap<>();
        final Map<Result, Statistics> grandTotalWeightedCount = new HashMap<>();
        final Statistics totalStatistics = new Statistics();
        final Statistics totalWeightedStatistics = new Statistics();
        final Statistics grandTotalStatistics = new Statistics();
        final Statistics grandTotalWeightedStatistics = new Statistics();
        final Map<String, Set<TestBookTest>> groupedMappedTests = new HashMap<>();
        final String fullName = String.format("%s %s", "className", "testName");
        final int weight = 789;

        mappedTests.put(fullName, actualTest);
        totalStatistics.getTotal().set(3);
        totalWeightedStatistics.getTotal().set(6);
        grandTotalStatistics.getTotal().set(123);
        totalCount.put(result, totalStatistics);
        totalWeightedCount.put(result, totalWeightedStatistics);
        grandTotalCount.put(result, grandTotalStatistics);
        grandTotalWeightedStatistics.getTotal().set(456);
        grandTotalWeightedCount.put(result, grandTotalWeightedStatistics);

        when(context.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getApplication()).thenReturn(application);
        when(application.getTestBook()).thenReturn(testBook);
        when(testBook.getMappedTests()).thenReturn(mappedTests);
        when(testBook.getStatistics()).thenReturn(statistics);
        when(testBook.getGroupedMappedTests()).thenReturn(groupedMappedTests);
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn("className");
        when(context.getDisplayName()).thenReturn("testName");
        when(actualTest.getWeight()).thenReturn(weight);

        when(statistics.getTotalCount()).thenReturn(totalCount);
        when(statistics.getTotalWeightedCount()).thenReturn(totalWeightedCount);
        when(statistics.getGrandTotalCount()).thenReturn(grandTotalCount);
        when(statistics.getGrandTotalWeightedCount()).thenReturn(grandTotalWeightedCount);

        testBookWatcher.updateTestBook(context, result);

        verify(actualTest).setResult(result);
        assertEquals(4, statistics.getTotalCount().get(result).getTotal().get());
        assertEquals(6 + weight, statistics.getTotalWeightedCount().get(result).getTotal().get());
        assertEquals(124, statistics.getGrandTotalCount().get(result).getTotal().get());
        assertEquals(456 + weight, statistics.getGrandTotalWeightedCount().get(result).getTotal().get());
        assertTrue(testBook.getGroupedMappedTests().get("className").contains(actualTest));
    }

    @DisplayName("updateGroupedTests should add the provided test to the provided map of grouped tests")
    @ParameterizedTest(name = "with className {0} and grouped tests {1}")
    @MethodSource("valuesProvider")
    public void updateGroupedTests(final String className, final Map<String, Set<TestBookTest>> groupedTests) {
        testBookWatcher.updateGroupedTests(groupedTests, className, test);

        assertTrue(groupedTests.get(className).contains(test));
    }

    public static Stream<Arguments> valuesProvider() throws IOException {
        return Stream.of(
                arguments("className", new HashMap<>()),
                arguments("className", new HashMap<>() {{ put("className", new HashSet<>()); }})
        );
    }
}
