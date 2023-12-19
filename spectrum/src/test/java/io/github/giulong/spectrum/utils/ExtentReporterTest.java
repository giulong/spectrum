package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.pojos.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtentReporter")
class ExtentReporterTest {

    private static MockedStatic<Path> pathMockedStatic;
    private static MockedStatic<FileUtils> fileUtilsMockedStatic;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private Path path;

    @Mock
    private Path directory1Path;

    @Mock
    private Path directory2Path;

    @Mock
    private File folder;

    @Mock
    private File file1;

    @Mock
    private File file2;

    @Mock
    private File directory1;

    @Mock
    private File directory2;

    @Mock
    private Configuration.Extent extent;

    @Mock
    private Retention retention;

    @BeforeEach
    public void beforeEach() {
        pathMockedStatic = mockStatic(Path.class);
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
    }

    @AfterEach
    public void afterEach() {
        pathMockedStatic.close();
        fileUtilsMockedStatic.close();
    }

    @Test
    @DisplayName("cleanupOldReports should delete the proper number of old reports and the corresponding directories")
    public void cleanupOldReports() {
        final int total = 123;
        final String reportFolder = "reportFolder";
        final String file1Name = "file1Name";
        final String file2Name = "file2Name";
        final String directory1Name = "directory1Name";
        final String directory2Name = "directory2Name";

        when(extent.getRetention()).thenReturn(retention);
        when(retention.getTotal()).thenReturn(total);
        when(extent.getReportFolder()).thenReturn(reportFolder);

        when(Path.of(reportFolder)).thenReturn(path);
        when(path.toFile()).thenReturn(folder);
        when(folder.listFiles()).thenReturn(new File[]{file1, file2, directory1, directory2});

        when(file1.isDirectory()).thenReturn(false);
        when(file2.isDirectory()).thenReturn(false);
        when(directory1.isDirectory()).thenReturn(true);
        when(directory2.isDirectory()).thenReturn(true);
        when(file1.getName()).thenReturn(file1Name);
        when(file2.getName()).thenReturn(file2Name);
        when(directory1.getName()).thenReturn(directory1Name);
        when(directory2.getName()).thenReturn(directory2Name);
        when(directory1.toPath()).thenReturn(directory1Path);
        when(directory2.toPath()).thenReturn(directory2Path);

        when(retention.deleteOldArtifactsFrom(anyList())).thenReturn(2);

        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.removeExtensionFrom(file1Name)).thenReturn(directory1Name);
        when(fileUtils.removeExtensionFrom(file2Name)).thenReturn(directory2Name);

        final ExtentReporter extentReporter = ExtentReporter
                .builder()
                .extent(extent)
                .build();
        extentReporter.cleanupOldReports();

        verify(fileUtils).deleteDirectory(directory1Path);
        verify(fileUtils).deleteDirectory(directory2Path);
    }
}
