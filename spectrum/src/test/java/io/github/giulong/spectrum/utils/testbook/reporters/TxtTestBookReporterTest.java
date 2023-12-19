package io.github.giulong.spectrum.utils.testbook.reporters;

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
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TxtTestBookReporter")
class TxtTestBookReporterTest {

    private static final Pattern PATTERN = Pattern.compile(".*[0-9]{2}-[0-9]{2}-[0-9]{4}_[0-9]{2}-[0-9]{2}-[0-9]{2}.txt");

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
    private TxtTestBookReporter testBookReporter;

    @BeforeEach
    public void beforeEach() {
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
        final String file1Name = "file1Name.txt";
        final String file2Name = "file2Name.txt";
        final String file3Name = "file2Name.notMatching";
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

        testBookReporter.cleanupOldReports();

        assertThat(stringArgumentCaptor.getValue(), matchesPattern(PATTERN));

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

        testBookReporter.cleanupOldReports();

        assertThat(stringArgumentCaptor.getValue(), matchesPattern(PATTERN));

        verifyNoMoreInteractions(retention);
    }

    @Test
    @DisplayName("doOutputFrom should interpolate the timestamp in the provided template name, create the output dir and write the file in it")
    public void doOutputFrom() {
        final String interpolatedTemplate = "interpolatedTemplate";

        when(Path.of(stringArgumentCaptor.capture())).thenReturn(path);
        when(path.getParent()).thenReturn(parentPath);

        testBookReporter.doOutputFrom(interpolatedTemplate);
        filesMockedStatic.verify(() -> Files.createDirectories(parentPath));
        filesMockedStatic.verify(() -> {
            Files.write(path, interpolatedTemplate.getBytes());
            assertThat(stringArgumentCaptor.getValue(), matchesPattern(PATTERN));
        });
    }
}
