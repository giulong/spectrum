package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.utils.reporters.FileReporter;
import io.github.giulong.spectrum.utils.reporters.LogReporter;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.giulong.spectrum.utils.MetadataManager.FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Metadata")
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

    @Mock
    private ExtentReporter extentReporter;

    @Mock
    private Path path;

    @Mock
    private Path filePath;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private JsonUtils jsonUtils;

    @Mock
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
    private File file;

    @InjectMocks
    private MetadataManager metadataManager;

    @BeforeEach
    public void beforeEach() {
        pathMockedStatic = mockStatic(Path.class);

        ReflectionUtils.setField("jsonUtils", metadataManager, jsonUtils);
        ReflectionUtils.setField("fileUtils", metadataManager, fileUtils);
        ReflectionUtils.setField("extentReporter", metadataManager, extentReporter);
    }

    @AfterEach
    public void afterEach() {
        pathMockedStatic.close();
        ;
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(MetadataManager.getInstance(), MetadataManager.getInstance());
    }

    @Test
    @DisplayName("sessionOpenedFrom should parse the existing metadata file cached")
    public void sessionOpenedFrom() {
        final String cacheFolder = "cacheFolder";

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getCacheFolder()).thenReturn(cacheFolder);
        when(Path.of(cacheFolder)).thenReturn(path);
        when(path.resolve(FILE_NAME)).thenReturn(filePath);
        when(filePath.toFile()).thenReturn(file);
        when(jsonUtils.readOrEmpty(file, MetadataManager.Metadata.class)).thenReturn(parsedMetadata);

        assertEquals(metadata, ReflectionUtils.getFieldValue("metadata", metadataManager));
        metadataManager.sessionOpenedFrom(configuration);

        assertEquals(parsedMetadata, ReflectionUtils.getFieldValue("metadata", metadataManager));
    }

    @Test
    @DisplayName("sessionClosedFrom should write the metadata.json in the configured cache folder")
    public void sessionClosedFrom() {
        final String cacheFolder = "cacheFolder";
        final String content = "content";

        when(configuration.getSummary()).thenReturn(summary);
        when(summary.isExecutionSuccessful()).thenReturn(false);

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getCacheFolder()).thenReturn(cacheFolder);
        when(Path.of(cacheFolder)).thenReturn(path);
        when(path.resolve(FILE_NAME)).thenReturn(filePath);
        when(jsonUtils.write(metadata)).thenReturn(content);

        metadataManager.sessionClosedFrom(configuration);

        verify(fileUtils).write(filePath, content);
    }

    @Test
    @DisplayName("sessionClosedFrom should write the metadata.json updating the successful reports when the execution is successful")
    public void sessionClosedFromSuccessful() {
        final String cacheFolder = "cacheFolder";
        final String content = "content";

        when(configuration.getSummary()).thenReturn(summary);
        when(summary.isExecutionSuccessful()).thenReturn(true);

        when(configuration.getTestBook()).thenReturn(testBook);
        when(testBook.getReporters()).thenReturn(List.of(testBookReporter1, testBookReporter2));

        when(summary.getReporters()).thenReturn(List.of(summaryReporter1, summaryReporter2));

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getCacheFolder()).thenReturn(cacheFolder);
        when(Path.of(cacheFolder)).thenReturn(path);
        when(path.resolve(FILE_NAME)).thenReturn(filePath);
        when(jsonUtils.write(metadata)).thenReturn(content);

        metadataManager.sessionClosedFrom(configuration);

        verify(extentReporter).produceMetadata();
        verifyNoMoreInteractions(testBookReporter1);
        verify(testBookReporter2).produceMetadata();

        verifyNoMoreInteractions(summaryReporter1);
        verify(summaryReporter2).produceMetadata();

        verify(fileUtils).write(filePath, content);
    }

    @Test
    @DisplayName("getNamespaceOf should return the provided object's class simple name")
    public void getNamespaceOf() {
        assertEquals("String", metadataManager.getNamespaceOf("a string"));
    }

    @Test
    @DisplayName("getSuccessfulQueueOf should return the successful reports queue for the provided producer if it exists already")
    public void getSuccessfulQueueOf() {
        final Map<String, FixedSizeQueue<File>> reports = new HashMap<>(Map.of(extentReporter.getClass().getSimpleName(), fileFixedSizeQueue));

        when(metadata.getExecution()).thenReturn(execution);
        when(execution.getSuccessful()).thenReturn(successful);
        when(successful.getReports()).thenReturn(reports);

        assertEquals(fileFixedSizeQueue, metadataManager.getSuccessfulQueueOf(extentReporter));
    }

    @Test
    @DisplayName("getSuccessfulQueueOf should return a new reports queue for the provided producer if it doesn't exists already")
    public void getSuccessfulQueueOfNew() {
        final Map<String, FixedSizeQueue<File>> reports = new HashMap<>(Map.of());

        //noinspection rawtypes
        MockedConstruction<FixedSizeQueue> fixedSizeQueueMockedConstruction = mockConstruction(FixedSizeQueue.class);

        when(metadata.getExecution()).thenReturn(execution);
        when(execution.getSuccessful()).thenReturn(successful);
        when(successful.getReports()).thenReturn(reports);

        final FixedSizeQueue<File> actual = metadataManager.getSuccessfulQueueOf(extentReporter);
        assertEquals(fixedSizeQueueMockedConstruction.constructed().getFirst(), actual);

        fixedSizeQueueMockedConstruction.close();
    }

    @Test
    @DisplayName("setSuccessfulQueueOf should set the provided queue in the namespace of the provided producer")
    public void setSuccessfulQueueOf() {
        when(metadata.getExecution()).thenReturn(execution);
        when(execution.getSuccessful()).thenReturn(successful);
        when(successful.getReports()).thenReturn(reports);

        metadataManager.setSuccessfulQueueOf(extentReporter, fileFixedSizeQueue);
        verify(reports).put(extentReporter.getClass().getSimpleName(), fileFixedSizeQueue);
    }
}
