package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.types.ProjectProperties;
import io.github.giulong.spectrum.utils.reporters.FileReporter;
import io.github.giulong.spectrum.utils.reporters.LogReporter;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class MetadataManagerTest {

    private static MockedStatic<Path> pathMockedStatic;

    @Mock
    private Summary summary;

    @Mock
    private TestBook testBook;

    @Mock
    private LogReporter.LogTestBookReporter testBookReporter1;

    @Mock
    private FileReporter.TxtTestBookReporter testBookReporter2;

    @Mock
    private LogReporter.LogSummaryReporter summaryReporter1;

    @Mock
    private FileReporter.TxtSummaryReporter summaryReporter2;

    @MockSingleton
    @SuppressWarnings("unused")
    private ExtentReporter extentReporter;

    @Mock
    private Path path;

    @Mock
    private Path filePath;

    @MockSingleton
    @SuppressWarnings("unused")
    private YamlUtils yamlUtils;

    @MockSingleton
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @MockSingleton
    @SuppressWarnings("unused")
    private JsonUtils jsonUtils;

    @MockSingleton
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private MetadataManager.Metadata metadata;

    @Mock
    private MetadataManager.Metadata parsedMetadata;

    @Mock
    private MetadataManager.Metadata.Execution execution;

    @Mock
    private MetadataManager.Metadata.Execution.Successful successful;

    @Mock
    private Map<String, FixedSizeQueue<File>> reports;

    @Mock
    private FixedSizeQueue<File> fileFixedSizeQueue;

    @Mock
    private ProjectProperties projectProperties;

    @Mock
    private File file;

    @InjectMocks
    private MetadataManager metadataManager;

    @BeforeEach
    void beforeEach() {
        pathMockedStatic = mockStatic(Path.class);
    }

    @AfterEach
    void afterEach() {
        pathMockedStatic.close();
    }

    private void buildPathStubs() {
        final String name = "name";
        final String fileName = name + "-metadata.json";
        final String cacheFolder = "cacheFolder";

        when(yamlUtils.readInternal("properties.yaml", ProjectProperties.class)).thenReturn(projectProperties);
        when(projectProperties.get("name")).thenReturn(name);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getCacheFolder()).thenReturn(cacheFolder);
        when(Path.of(cacheFolder)).thenReturn(path);
        when(path.resolve(fileName)).thenReturn(filePath);
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(MetadataManager.getInstance(), MetadataManager.getInstance());
    }

    @Test
    @DisplayName("sessionOpened should parse the existing metadata file cached")
    void sessionOpened() {
        buildPathStubs();

        when(filePath.toFile()).thenReturn(file);
        when(jsonUtils.readOrEmpty(file, MetadataManager.Metadata.class)).thenReturn(parsedMetadata);

        assertEquals(metadata, Reflections.getFieldValue("metadata", metadataManager));
        metadataManager.sessionOpened();

        assertEquals(parsedMetadata, Reflections.getFieldValue("metadata", metadataManager));
    }

    @Test
    @DisplayName("sessionClosed should write the metadata.json in the configured cache folder")
    void sessionClosed() {
        final String content = "content";

        buildPathStubs();

        when(configuration.getSummary()).thenReturn(summary);
        when(summary.isExecutionSuccessful()).thenReturn(false);
        when(jsonUtils.write(metadata)).thenReturn(content);

        metadataManager.sessionClosed();

        verify(fileUtils).write(filePath, content);
    }

    @Test
    @DisplayName("sessionClosed should write the metadata.json updating the successful reports when the execution is successful")
    void sessionClosedFromSuccessful() {
        final String content = "content";

        buildPathStubs();
        when(configuration.getSummary()).thenReturn(summary);
        when(summary.isExecutionSuccessful()).thenReturn(true);
        when(configuration.getTestBook()).thenReturn(testBook);
        when(testBook.getReporters()).thenReturn(List.of(testBookReporter1, testBookReporter2));
        when(summary.getReporters()).thenReturn(List.of(summaryReporter1, summaryReporter2));
        when(jsonUtils.write(metadata)).thenReturn(content);

        metadataManager.sessionClosed();

        verify(extentReporter).produceMetadata();
        verifyNoMoreInteractions(testBookReporter1);
        verify(testBookReporter2).produceMetadata();

        verifyNoMoreInteractions(summaryReporter1);
        verify(summaryReporter2).produceMetadata();

        verify(fileUtils).write(filePath, content);
    }

    @Test
    @DisplayName("getNamespaceOf should return the provided object's class simple name")
    void getNamespaceOf() {
        assertEquals("String", metadataManager.getNamespaceOf("a string"));
    }

    @Test
    @DisplayName("getSuccessfulQueueOf should return the successful reports queue for the provided producer if it exists already")
    void getSuccessfulQueueOf() {
        final Map<String, FixedSizeQueue<File>> localReports = new HashMap<>(Map.of(extentReporter.getClass().getSimpleName(), fileFixedSizeQueue));

        when(metadata.getExecution()).thenReturn(execution);
        when(execution.getSuccessful()).thenReturn(successful);
        when(successful.getReports()).thenReturn(localReports);

        assertEquals(fileFixedSizeQueue, metadataManager.getSuccessfulQueueOf(extentReporter));
    }

    @Test
    @DisplayName("getSuccessfulQueueOf should return a new reports queue for the provided producer if it doesn't exists already")
    void getSuccessfulQueueOfNew() {
        final Map<String, FixedSizeQueue<File>> localReports = new HashMap<>(Map.of());

        //noinspection rawtypes
        MockedConstruction<FixedSizeQueue> fixedSizeQueueMockedConstruction = mockConstruction(FixedSizeQueue.class);

        when(metadata.getExecution()).thenReturn(execution);
        when(execution.getSuccessful()).thenReturn(successful);
        when(successful.getReports()).thenReturn(localReports);

        final FixedSizeQueue<File> actual = metadataManager.getSuccessfulQueueOf(extentReporter);
        assertEquals(fixedSizeQueueMockedConstruction.constructed().getFirst(), actual);

        fixedSizeQueueMockedConstruction.close();
    }

    @Test
    @DisplayName("setSuccessfulQueueOf should set the provided queue in the namespace of the provided producer")
    void setSuccessfulQueueOf() {
        when(metadata.getExecution()).thenReturn(execution);
        when(execution.getSuccessful()).thenReturn(successful);
        when(successful.getReports()).thenReturn(reports);

        metadataManager.setSuccessfulQueueOf(extentReporter, fileFixedSizeQueue);
        verify(reports).put(extentReporter.getClass().getSimpleName(), fileFixedSizeQueue);
    }

    @Test
    @DisplayName("buildPath should return the path of the project-specific metadata.json")
    void buildPath() {
        buildPathStubs();

        assertEquals(filePath, metadataManager.buildPath());
    }
}
