package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.jcodec.codecs.mjpeg.tools.Asserts.assertEquals;
import static org.mockito.Mockito.*;

class RetentionTest {

    @Mock
    private MetadataManager metadataManager;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private ExtentReporter metadataProducer;

    @Mock
    private FixedSizeQueue<File> successfulQueue;

    @Mock
    private File file1;

    @Mock
    private File file2;

    @Mock
    private File file3;

    @Mock
    private File file4;

    @Mock
    private File file5;

    @Mock
    private Path path1;

    @Mock
    private Path path2;

    @Mock
    private Path path3;

    @Mock
    private Path path4;

    @Mock
    private Path path5;

    @Mock
    private File absoluteFile1;

    @Mock
    private File absoluteFile2;

    @Mock
    private File absoluteFile3;

    @Mock
    private File absoluteFile4;

    @Mock
    private File absoluteFile5;

    @InjectMocks
    private Retention retention;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("metadataManager", retention, metadataManager);
        Reflections.setField("fileUtils", retention, fileUtils);
    }

    @Test
    @DisplayName("the default total retention should be Integer.MAX_VALUE")
    void defaultRetention() {
        assertEquals(Integer.MAX_VALUE, retention.getTotal());
    }

    @Test
    @DisplayName("deleteOldArtifactsFrom should delete files if there are more reports than the total allowed")
    void deleteOldArtifactsFrom() {
        final int total = 1;
        final List<File> files = List.of(file1, file2, file3);

        Reflections.setField("total", retention, total);
        when(metadataManager.getSuccessfulQueueOf(metadataProducer)).thenReturn(successfulQueue);

        when(file1.toPath()).thenReturn(path1);
        when(file2.toPath()).thenReturn(path2);

        retention.deleteOldArtifactsFrom(files, metadataProducer);
        verify(fileUtils).deleteDirectory(path1);
        verify(fileUtils).deleteDirectory(path2);
        verify(fileUtils, never()).deleteDirectory(path3);
    }

    @Test
    @DisplayName("deleteOldArtifactsFrom should delete no file if there are less reports than the total allowed")
    void deleteOldArtifactsFromMinimum() {
        final int total = 5;
        final List<File> files = List.of(file1, file2, file3);

        Reflections.setField("total", retention, total);
        when(metadataManager.getSuccessfulQueueOf(metadataProducer)).thenReturn(successfulQueue);

        retention.deleteOldArtifactsFrom(files, metadataProducer);
        verify(file1, never()).delete();
        verify(file2, never()).delete();
        verify(file3, never()).delete();
    }

    @Test
    @DisplayName("#1 deleteOldArtifactsFrom should keep the configured number of successful reports")
    void deleteOldArtifactsFromSuccessfulOne() {
        final int total = 2;
        final int successful = 1;
        final List<File> files = List.of(file1, file2, file3, file4, file5);

        Reflections.setField("total", retention, total);
        Reflections.setField("successful", retention, successful);
        when(metadataManager.getSuccessfulQueueOf(metadataProducer)).thenReturn(successfulQueue);
        when(file1.getAbsoluteFile()).thenReturn(absoluteFile1);
        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);

        when(file2.toPath()).thenReturn(path2);
        when(file3.toPath()).thenReturn(path3);
        when(file4.toPath()).thenReturn(path4);

        retention.deleteOldArtifactsFrom(files, metadataProducer);
        verify(fileUtils, never()).deleteDirectory(path1);
        verify(fileUtils).deleteDirectory(path2);
        verify(fileUtils).deleteDirectory(path3);
        verify(fileUtils).deleteDirectory(path4);
        verify(fileUtils, never()).deleteDirectory(path5);
    }

    @Test
    @DisplayName("#2 deleteOldArtifactsFrom should keep the configured number of successful reports")
    void deleteOldArtifactsFromSuccessfulTwo() {
        final int total = 5;
        final int successful = 1;
        final List<File> files = List.of(file1, file2, file3, file4, file5);

        Reflections.setField("total", retention, total);
        Reflections.setField("successful", retention, successful);
        when(metadataManager.getSuccessfulQueueOf(metadataProducer)).thenReturn(successfulQueue);
        when(file1.getAbsoluteFile()).thenReturn(absoluteFile1);
        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);

        retention.deleteOldArtifactsFrom(files, metadataProducer);
        verify(file1, never()).delete();
        verify(file2, never()).delete();
        verify(file3, never()).delete();
        verify(file4, never()).delete();
        verify(file5, never()).delete();
    }

    @Test
    @DisplayName("#3 deleteOldArtifactsFrom should keep the configured number of successful reports")
    void deleteOldArtifactsFromSuccessfulThree() {
        final int total = 2;
        final int successful = 2;
        final List<File> files = List.of(file1, file2, file3, file4, file5);

        Reflections.setField("total", retention, total);
        Reflections.setField("successful", retention, successful);
        when(metadataManager.getSuccessfulQueueOf(metadataProducer)).thenReturn(successfulQueue);
        when(file1.getAbsoluteFile()).thenReturn(absoluteFile1);
        when(file5.getAbsoluteFile()).thenReturn(absoluteFile5);
        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);

        when(file2.toPath()).thenReturn(path2);
        when(file3.toPath()).thenReturn(path3);
        when(file4.toPath()).thenReturn(path4);

        retention.deleteOldArtifactsFrom(files, metadataProducer);
        verify(fileUtils, never()).deleteDirectory(path1);
        verify(fileUtils).deleteDirectory(path2);
        verify(fileUtils).deleteDirectory(path3);
        verify(fileUtils).deleteDirectory(path4);
        verify(fileUtils, never()).deleteDirectory(path5);
    }

    @Test
    @DisplayName("#4 deleteOldArtifactsFrom should keep the configured number of successful reports")
    void deleteOldArtifactsFromSuccessfulFour() {
        final int total = 3;
        final int successful = 2;
        final List<File> files = List.of(file1, file2, file3, file4, file5);

        Reflections.setField("total", retention, total);
        Reflections.setField("successful", retention, successful);
        when(metadataManager.getSuccessfulQueueOf(metadataProducer)).thenReturn(successfulQueue);
        when(file1.getAbsoluteFile()).thenReturn(absoluteFile1);
        when(file2.getAbsoluteFile()).thenReturn(absoluteFile2);
        when(file3.getAbsoluteFile()).thenReturn(absoluteFile3);
        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);
        when(successfulQueue.contains(absoluteFile2)).thenReturn(false);
        when(successfulQueue.contains(absoluteFile3)).thenReturn(true);

        when(file2.toPath()).thenReturn(path2);
        when(file4.toPath()).thenReturn(path4);

        retention.deleteOldArtifactsFrom(files, metadataProducer);
        verify(fileUtils, never()).deleteDirectory(path1);
        verify(fileUtils).deleteDirectory(path2);
        verify(fileUtils, never()).deleteDirectory(path3);
        verify(fileUtils).deleteDirectory(path4);
        verify(fileUtils, never()).deleteDirectory(path5);
    }

    @Test
    @DisplayName("#5 deleteOldArtifactsFrom should keep the configured number of successful reports")
    void deleteOldArtifactsFromSuccessfulFive() {
        final int total = 3;
        final int successful = 2;
        final List<File> files = List.of(file1, file2, file3, file4, file5);

        Reflections.setField("total", retention, total);
        Reflections.setField("successful", retention, successful);
        when(metadataManager.getSuccessfulQueueOf(metadataProducer)).thenReturn(successfulQueue);
        when(file1.getAbsoluteFile()).thenReturn(absoluteFile1);
        when(file2.getAbsoluteFile()).thenReturn(absoluteFile2);
        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);
        when(successfulQueue.contains(absoluteFile2)).thenReturn(true);

        when(file3.toPath()).thenReturn(path3);
        when(file4.toPath()).thenReturn(path4);

        retention.deleteOldArtifactsFrom(files, metadataProducer);
        verify(fileUtils, never()).deleteDirectory(path1);
        verify(fileUtils, never()).deleteDirectory(path2);
        verify(fileUtils).deleteDirectory(path3);
        verify(fileUtils).deleteDirectory(path4);
        verify(fileUtils, never()).deleteDirectory(path5);
    }

    @Test
    @DisplayName("#6 deleteOldArtifactsFrom should keep the configured number of successful reports")
    void deleteOldArtifactsFromSuccessfulSix() {
        final int total = 3;
        final int successful = 2;
        final List<File> files = List.of(file1, file2, file3, file4, file5);

        Reflections.setField("total", retention, total);
        Reflections.setField("successful", retention, successful);
        when(metadataManager.getSuccessfulQueueOf(metadataProducer)).thenReturn(successfulQueue);
        when(file1.getAbsoluteFile()).thenReturn(absoluteFile1);
        when(file2.getAbsoluteFile()).thenReturn(absoluteFile2);
        when(file3.getAbsoluteFile()).thenReturn(absoluteFile3);
        when(file4.getAbsoluteFile()).thenReturn(absoluteFile4);
        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);
        when(successfulQueue.contains(absoluteFile2)).thenReturn(false);
        when(successfulQueue.contains(absoluteFile3)).thenReturn(false);
        when(successfulQueue.contains(absoluteFile4)).thenReturn(true);

        when(file2.toPath()).thenReturn(path2);
        when(file3.toPath()).thenReturn(path3);

        retention.deleteOldArtifactsFrom(files, metadataProducer);
        verify(fileUtils, never()).deleteDirectory(path1);
        verify(fileUtils).deleteDirectory(path2);
        verify(fileUtils).deleteDirectory(path3);
        verify(fileUtils, never()).deleteDirectory(path4);
        verify(fileUtils, never()).deleteDirectory(path5);
    }
}
