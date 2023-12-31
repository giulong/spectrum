package io.github.giulong.spectrum.utils.reporters;

import io.github.giulong.spectrum.utils.ReflectionUtils;
import io.github.giulong.spectrum.utils.Retention;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileReporter")
class FileReporterTest {

    private static final String OUTPUT = "output.abc";

    private MockedStatic<Path> pathMockedStatic;
    private MockedStatic<Files> filesMockedStatic;

    @Mock
    private Path path;

    @Mock
    private Path parentPath;

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

    @Mock
    private Retention retention;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @InjectMocks
    private FileReporter fileReporter;

    @BeforeEach
    public void beforeEach() {
        ReflectionUtils.setField("output", fileReporter, OUTPUT);
        ReflectionUtils.setField("retention", fileReporter, retention);
        pathMockedStatic = mockStatic(Path.class);
        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    public void afterEach() {
        pathMockedStatic.close();
        filesMockedStatic.close();
    }

    @Test
    @DisplayName("cleanupOldReports should delete the proper number of old reports and the corresponding directories")
    public void cleanupOldReports() {
        final int total = 123;
        final String file1Name = "file1Name.abc";
        final String file2Name = "file2Name.abc";
        final String file3Name = "file3Name.notMatching";
        final long lastModified = 1L;

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
        when(file1.lastModified()).thenReturn(lastModified);
        when(file2.lastModified()).thenReturn(lastModified);

        fileReporter.cleanupOldReports();

        assertEquals(OUTPUT, stringArgumentCaptor.getValue());

        verify(retention).deleteOldArtifactsFrom(List.of(file1, file2));
    }

    @Test
    @DisplayName("cleanupOldReports should do nothing when the reports folder is empty")
    public void cleanupOldReportsEmpty() {
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
    public void doOutputFrom() {
        final String interpolatedTemplate = "interpolatedTemplate";

        when(Path.of(stringArgumentCaptor.capture())).thenReturn(path);
        when(path.getParent()).thenReturn(parentPath);

        fileReporter.doOutputFrom(interpolatedTemplate);
        filesMockedStatic.verify(() -> Files.createDirectories(parentPath));
        filesMockedStatic.verify(() -> {
            Files.write(path, interpolatedTemplate.getBytes());
            assertEquals(OUTPUT, stringArgumentCaptor.getValue());
        });
    }
}
