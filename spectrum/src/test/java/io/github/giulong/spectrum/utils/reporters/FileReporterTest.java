package io.github.giulong.spectrum.utils.reporters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class FileReporterTest {

    private static final String OUTPUT = "output.abc";

    private MockedStatic<Path> pathMockedStatic;
    private MockedStatic<Files> filesMockedStatic;
    private MockedStatic<Desktop> desktopMockedStatic;

    @MockFinal
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @Mock
    private Desktop desktop;

    @Mock
    private Path path;

    @Mock
    private Path parentPath;

    @Mock
    private Path absolutePath;

    @Mock
    private File folder;

    @Mock
    private File file1;

    @Mock
    private File file2;

    @Mock
    private File file3;

    @Mock
    private File directory1;

    @Mock
    private File directory2;

    @MockFinal
    @SuppressWarnings("unused")
    private MetadataManager metadataManager;

    @Mock
    private FixedSizeQueue<File> fileFixedSizeQueue;

    @MockFinal
    @SuppressWarnings("unused")
    private Retention retention;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @InjectMocks
    private DummyFileReporter fileReporter;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("output", fileReporter, OUTPUT);

        pathMockedStatic = mockStatic(Path.class);
        filesMockedStatic = mockStatic(Files.class);
        desktopMockedStatic = mockStatic(Desktop.class);
    }

    @AfterEach
    void afterEach() {
        pathMockedStatic.close();
        filesMockedStatic.close();
        desktopMockedStatic.close();
    }

    @Test
    @DisplayName("cleanupOldReports should delete the proper number of old reports and the corresponding directories")
    void cleanupOldReports() {
        final int total = 123;
        final String file1Name = "file1Name.abc";
        final String file2Name = "file2Name.abc";
        final String file3Name = "file3Name.notMatching";

        when(fileUtils.getExtensionOf(OUTPUT)).thenReturn("abc");
        when(retention.getTotal()).thenReturn(total);

        when(Path.of(stringArgumentCaptor.capture())).thenReturn(path);
        when(path.getParent()).thenReturn(parentPath);
        when(parentPath.toFile()).thenReturn(folder);
        when(folder.listFiles()).thenReturn(new File[]{file1, file2, file3, directory1, directory2});

        when(file1.isDirectory()).thenReturn(false);
        when(file2.isDirectory()).thenReturn(false);
        when(directory1.isDirectory()).thenReturn(true);
        when(directory2.isDirectory()).thenReturn(true);
        when(file1.getName()).thenReturn(file1Name);
        when(file2.getName()).thenReturn(file2Name);
        when(file3.getName()).thenReturn(file3Name);

        fileReporter.cleanupOldReports();

        assertEquals(OUTPUT, stringArgumentCaptor.getValue());

        verify(retention).deleteArtifactsFrom(List.of(file1, file2), fileReporter);
    }

    @Test
    @DisplayName("cleanupOldReports should do nothing when the reports folder is empty")
    void cleanupOldReportsEmpty() {
        final int total = 123;

        when(retention.getTotal()).thenReturn(total);

        when(Path.of(stringArgumentCaptor.capture())).thenReturn(path);
        when(path.getParent()).thenReturn(parentPath);
        when(parentPath.toFile()).thenReturn(folder);
        when(folder.listFiles()).thenReturn(null);

        fileReporter.cleanupOldReports();

        assertEquals(OUTPUT, stringArgumentCaptor.getValue());

        verifyNoMoreInteractions(retention);
    }

    @Test
    @DisplayName("doOutputFrom should interpolate the timestamp in the provided template name, create the output dir and write the file in it")
    void doOutputFrom() {
        final String interpolatedTemplate = "interpolatedTemplate";

        fileReporter.doOutputFrom(interpolatedTemplate);

        verify(fileUtils).write(OUTPUT, interpolatedTemplate);
    }

    @Test
    @DisplayName("produceMetadata should shrink the queue in the provided metadata bound to the given namespace, and add the new element to it")
    void produceMetadata() {
        final String namespace = "namespace";
        final int retentionSuccessful = 123;
        final Map<String, FixedSizeQueue<File>> reports = new HashMap<>(Map.of(namespace, fileFixedSizeQueue));

        when(Path.of(stringArgumentCaptor.capture())).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);
        when(absolutePath.toFile()).thenReturn(file1);
        when(retention.getSuccessful()).thenReturn(retentionSuccessful);
        when(metadataManager.getSuccessfulQueueOf(fileReporter)).thenReturn(fileFixedSizeQueue);
        when(fileFixedSizeQueue.shrinkTo(retentionSuccessful - 1)).thenReturn(fileFixedSizeQueue);

        fileReporter.produceMetadata();

        verify(fileFixedSizeQueue).add(file1);
        assertEquals(fileFixedSizeQueue, reports.get(namespace));
    }

    @Test
    @DisplayName("open should open the report")
    void open() throws IOException {
        Reflections.setField("openAtEnd", fileReporter, true);

        when(Path.of(OUTPUT)).thenReturn(path);
        when(path.toFile()).thenReturn(file1);
        when(Desktop.getDesktop()).thenReturn(desktop);

        fileReporter.open();

        verify(desktop).open(file1);
    }

    @Test
    @DisplayName("open should do nothing if openAtEnd is false")
    void openFalse() {
        fileReporter.open();

        verifyNoInteractions(desktop);
    }

    private static final class DummyFileReporter extends FileReporter {
    }
}
