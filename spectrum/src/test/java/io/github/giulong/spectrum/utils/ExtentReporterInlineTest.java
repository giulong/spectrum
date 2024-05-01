package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtentReporterInlineTest {

    private static final String REPORT_FOLDER = "reportFolder";
    private static final String INLINE_REPORT_FOLDER = "inlineReportFolder";

    private static MockedStatic<Path> pathMockedStatic;
    private static MockedStatic<Files> filesMockedStatic;

    @Mock
    private Configuration configuration;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private Path path;

    @Mock
    private Path inlinePath;

    @Mock
    private Path absolutePath;

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

    @Mock
    private HtmlUtils htmlUtils;

    @InjectMocks
    private ExtentReporterInline extentReporterInline;

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("fileUtils", extentReporterInline, fileUtils);
        Reflections.setField("htmlUtils", extentReporterInline, htmlUtils);
        Reflections.setField("configuration", extentReporterInline, configuration);

        pathMockedStatic = mockStatic(Path.class);
        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    public void afterEach() {
        pathMockedStatic.close();
        filesMockedStatic.close();
    }

    // cleanupOldReports stubs from parent
    private void cleanupOldReportsStubsFor(final String reportFolder) {
        final int total = 123;
        final String file1Name = "file1Name";
        final String file2Name = "file2Name";
        final String directory1Name = "directory1Name";
        final String directory2Name = "directory2Name";

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);
        when(retention.getTotal()).thenReturn(total);

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

        when(retention.deleteOldArtifactsFrom(List.of(file1, file2), extentReporterInline)).thenReturn(2);

        when(fileUtils.removeExtensionFrom(file1Name)).thenReturn(directory1Name);
        when(fileUtils.removeExtensionFrom(file2Name)).thenReturn(directory2Name);

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);
        when(retention.getTotal()).thenReturn(total);
    }

    @Test
    @DisplayName("sessionOpened should return immediately if inline report is not active")
    public void sessionOpenedFalse() {
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.isInline()).thenReturn(false);

        extentReporterInline.sessionOpened();

        verifyNoMoreInteractions(configuration);
        verifyNoMoreInteractions(extent);
    }

    @Test
    @DisplayName("sessionOpened should just log the path of the report that will be produced in the end")
    public void sessionOpened() {
        final String fileName = "fileName";

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.isInline()).thenReturn(true);

        // getReportPathFrom
        when(extent.getInlineReportFolder()).thenReturn(INLINE_REPORT_FOLDER);
        when(extent.getFileName()).thenReturn(fileName);
        when(Path.of(INLINE_REPORT_FOLDER, fileName)).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);

        extentReporterInline.sessionOpened();

        verify(extent).getReportName();
    }

    @Test
    @DisplayName("sessionClosed should cleanup the old reports even if inline report is not active")
    public void sessionClosedFalse() {
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.isInline()).thenReturn(false);

        when(extent.getInlineReportFolder()).thenReturn(INLINE_REPORT_FOLDER);
        cleanupOldReportsStubsFor(INLINE_REPORT_FOLDER);

        extentReporterInline.sessionClosed();

        verifyNoInteractions(htmlUtils);
    }

    @Test
    @DisplayName("sessionClosed should produce the inline report and cleanup the old ones")
    public void sessionClosed() throws IOException {
        final String fileName = "fileName";
        final String readString = "readString";
        final String inlineReport = "inlineReport";

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.isInline()).thenReturn(true);

        when(extent.getReportFolder()).thenReturn(REPORT_FOLDER);
        when(extent.getInlineReportFolder()).thenReturn(INLINE_REPORT_FOLDER);

        // getReportPathFrom
        when(extent.getReportFolder()).thenReturn(REPORT_FOLDER);
        when(extent.getFileName()).thenReturn(fileName);
        when(Path.of(REPORT_FOLDER, fileName)).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);

        cleanupOldReportsStubsFor(REPORT_FOLDER);
        cleanupOldReportsStubsFor(INLINE_REPORT_FOLDER);

        when(Files.readString(absolutePath)).thenReturn(readString);
        when(htmlUtils.inline(readString)).thenReturn(inlineReport);
        when(extent.getInlineReportFolder()).thenReturn(INLINE_REPORT_FOLDER);
        when(Path.of(INLINE_REPORT_FOLDER, fileName)).thenReturn(inlinePath);

        extentReporterInline.sessionClosed();

        verify(fileUtils).write(inlinePath, inlineReport);
    }

    @Test
    @DisplayName("getReportPathFrom should should return the absolute path resulting from the composition of extent's report folder and filename")
    public void getReportPathFrom() {
        final String fileName = "fileName";

        when(extent.getInlineReportFolder()).thenReturn(INLINE_REPORT_FOLDER);
        when(extent.getFileName()).thenReturn(fileName);
        when(Path.of(INLINE_REPORT_FOLDER, fileName)).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);

        assertEquals(absolutePath, extentReporterInline.getReportPathFrom(extent));
    }
}