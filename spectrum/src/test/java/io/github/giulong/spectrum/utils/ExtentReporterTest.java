package io.github.giulong.spectrum.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ExtentSparkReporterConfig;
import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.aventstack.extentreports.Status.*;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.aventstack.extentreports.reporter.configuration.Theme.DARK;
import static io.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtentReporter")
class ExtentReporterTest {

    private static final String REPORT_FOLDER = "reportFolder";
    private static final String INLINE_REPORT_FOLDER = "inlineReportFolder";

    private static MockedStatic<TestData> testDataMockedStatic;
    private static MockedStatic<FreeMarkerWrapper> freeMarkerWrapperMockedStatic;
    private static MockedStatic<Path> pathMockedStatic;
    private static MockedStatic<FileUtils> fileUtilsMockedStatic;
    private static MockedStatic<Files> filesMockedStatic;
    private MockedStatic<MetadataManager> metadataManagerMockedStatic;

    @Mock
    private Configuration configuration;

    @Mock
    private ExtentSparkReporterConfig extentSparkReporterConfig;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private HtmlUtils htmlUtils;

    @Mock
    private TestData.TestDataBuilder testDataBuilder;

    @Mock
    private TestData testData;

    @Mock
    private Path path;

    @Mock
    private Path inlineReportFolder;

    @Mock
    private Path inlineReportPath;

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
    private ExtentReports extentReports;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private Video.ExtentTest videoExtentTest;

    @Mock
    private Retention retention;

    @Mock
    private RuntimeException exception;

    @Mock
    private FixedSizeQueue<File> fileFixedSizeQueue;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext parentContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private SpectrumTest<?> spectrumTest;

    @Mock
    private MetadataManager metadataManager;

    @Captor
    private ArgumentCaptor<Function<String, ExtentTest>> functionArgumentCaptor;

    @Captor
    private ArgumentCaptor<Markup> markupArgumentCaptor;

    @Captor
    private ArgumentCaptor<Markup> skipMarkupArgumentCaptor;

    @InjectMocks
    private ExtentReporter extentReporter;

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("fileUtils", extentReporter, fileUtils);
        Reflections.setField("configuration", extentReporter, configuration);
        Reflections.setField("htmlUtils", extentReporter, htmlUtils);
        testDataMockedStatic = mockStatic(TestData.class);
        freeMarkerWrapperMockedStatic = mockStatic(FreeMarkerWrapper.class);
        pathMockedStatic = mockStatic(Path.class);
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
        filesMockedStatic = mockStatic(Files.class);
        metadataManagerMockedStatic = mockStatic(MetadataManager.class);
    }

    @AfterEach
    public void afterEach() {
        testDataMockedStatic.close();
        freeMarkerWrapperMockedStatic.close();
        pathMockedStatic.close();
        fileUtilsMockedStatic.close();
        filesMockedStatic.close();
        metadataManagerMockedStatic.close();
    }

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

        when(retention.deleteOldArtifactsFrom(List.of(file1, file2), extentReporter)).thenReturn(2);

        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.removeExtensionFrom(file1Name)).thenReturn(directory1Name);
        when(fileUtils.removeExtensionFrom(file2Name)).thenReturn(directory2Name);
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(ExtentReporter.getInstance(), ExtentReporter.getInstance());
    }

    @Test
    @DisplayName("sessionOpened should init the extent report")
    public void sessionOpened() {
        final String fileName = "fileName";
        final String reportName = "reportName";
        final String documentTitle = "documentTitle";
        final String theme = "DARK";
        final String timeStampFormat = "timeStampFormat";
        final String css = "css";
        final String absolutePathToString = "absolute\\Path\\To\\String";
        final String absolutePathToStringReplaced = "absolute/Path/To/String";

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(REPORT_FOLDER);
        when(extent.getFileName()).thenReturn(fileName);
        when(Path.of(REPORT_FOLDER, fileName)).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);
        when(absolutePath.toString()).thenReturn(absolutePathToString);
        when(extent.getReportName()).thenReturn(reportName);
        when(extent.getDocumentTitle()).thenReturn(documentTitle);
        when(extent.getTheme()).thenReturn(theme);
        when(extent.getTimeStampFormat()).thenReturn(timeStampFormat);
        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.read("/css/report.css")).thenReturn(css);

        MockedConstruction<ExtentReports> extentReportsMockedConstruction = mockConstruction(ExtentReports.class);

        MockedConstruction<ExtentSparkReporter> extentSparkReporterMockedConstruction = mockConstruction(ExtentSparkReporter.class, (mock, context) -> {
            assertEquals(absolutePathToStringReplaced, context.arguments().getFirst());
            when(mock.config()).thenReturn(extentSparkReporterConfig);
        });

        extentReporter.sessionOpened();

        verify(extentSparkReporterConfig).setDocumentTitle(documentTitle);
        verify(extentSparkReporterConfig).setReportName(reportName);
        verify(extentSparkReporterConfig).setTheme(DARK);
        verify(extentSparkReporterConfig).setTimeStampFormat(timeStampFormat);
        verify(extentSparkReporterConfig).setCss(css);

        final ExtentReports extentReports = extentReportsMockedConstruction.constructed().getFirst();
        verify(extentReports).attachReporter(extentSparkReporterMockedConstruction.constructed().toArray(new ExtentSparkReporter[0]));

        extentSparkReporterMockedConstruction.close();
        extentReportsMockedConstruction.close();
    }

    @Test
    @DisplayName("sessionClosed should flush the extent report and cleanup old ones")
    public void sessionClosed() {
        final int total = 123;

        cleanupOldReportsStubsFor(REPORT_FOLDER);
        cleanupOldReportsStubsFor(INLINE_REPORT_FOLDER);

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);
        when(retention.getTotal()).thenReturn(total);
        when(extent.getReportFolder()).thenReturn(REPORT_FOLDER);
        when(extent.getInlineReportFolder()).thenReturn(inlineReportFolder);
        when(inlineReportFolder.toString()).thenReturn(INLINE_REPORT_FOLDER);

        extentReporter.sessionClosed();

        verify(extentReports).flush();

        // cleanupOldReports
        verify(fileUtils, times(2)).deleteDirectory(directory1Path);
        verify(fileUtils, times(2)).deleteDirectory(directory2Path);
    }

    @Test
    @DisplayName("sessionClosed should also inline the report")
    public void sessionClosedInlineReport() throws IOException {
        final String reportFolder = "reportFolder";
        final String fileName = "fileName";
        final String fileNameWithoutExtension = "fileNameWithoutExtension";
        final String readString = "readString";
        final String inlineReport = "inlineReport";

        cleanupOldReportsStubsFor(REPORT_FOLDER);
        cleanupOldReportsStubsFor(INLINE_REPORT_FOLDER);

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.isInline()).thenReturn(true);
        when(extent.getReportFolder()).thenReturn(reportFolder);
        when(extent.getFileName()).thenReturn(fileName);
        when(Path.of(reportFolder, fileName)).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);

        when(Files.readString(absolutePath)).thenReturn(readString);
        when(htmlUtils.inline(readString)).thenReturn(inlineReport);

        when(fileUtils.removeExtensionFrom(fileName)).thenReturn(fileNameWithoutExtension);
        when(extent.getInlineReportFolder()).thenReturn(inlineReportFolder);
        when(inlineReportFolder.resolve(fileNameWithoutExtension + "-inline.html")).thenReturn(inlineReportPath);

        when(extent.getRetention()).thenReturn(retention);

        extentReporter.sessionClosed();

        verify(extentReports).flush();
        verify(fileUtils).write(inlineReportPath, inlineReport);

        // cleanupOldReports
        verify(fileUtils, times(2)).deleteDirectory(directory1Path);
        verify(fileUtils, times(2)).deleteDirectory(directory2Path);
    }

    @Test
    @DisplayName("cleanupOldReportsIn should delete the proper number of old reports and the corresponding directories")
    public void cleanupOldReportsIn() {
        cleanupOldReportsStubsFor(REPORT_FOLDER);

        extentReporter.cleanupOldReportsIn(REPORT_FOLDER);

        verify(fileUtils).deleteDirectory(directory1Path);
        verify(fileUtils).deleteDirectory(directory2Path);
    }

    @Test
    @DisplayName("cleanupOldReportsIn should return if the provided folder is empty")
    public void cleanupOldReportsInEmptyFolder() {
        final String folder = "folder";
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);
        when(Path.of(folder)).thenReturn(path);
        when(path.toFile()).thenReturn(file1);

        extentReporter.cleanupOldReportsIn(folder);

        verifyNoInteractions(fileUtils);
    }

    @Test
    @DisplayName("getRetention should return the extent's retention")
    public void getRetention() {
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);

        assertEquals(retention, extentReporter.getRetention());
    }

    @Test
    @DisplayName("produceMetadata should shrink the queue in the provided metadata bound to the given namespace, and add the new element to it")
    public void produceMetadata() {
        final String reportFolder = "reportFolder";
        final String fileName = "fileName";
        final String namespace = "namespace";
        final int retentionSuccessful = 123;
        final Map<String, FixedSizeQueue<File>> reports = new HashMap<>(Map.of(namespace, fileFixedSizeQueue));

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(reportFolder);
        when(extent.getFileName()).thenReturn(fileName);
        when(Path.of(reportFolder, fileName)).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);
        when(absolutePath.toFile()).thenReturn(file1);
        when(retention.getSuccessful()).thenReturn(retentionSuccessful);

        when(MetadataManager.getInstance()).thenReturn(metadataManager);
        when(metadataManager.getSuccessfulQueueOf(extentReporter)).thenReturn(fileFixedSizeQueue);
        when(fileFixedSizeQueue.shrinkTo(retentionSuccessful - 1)).thenReturn(fileFixedSizeQueue);

        extentReporter.produceMetadata();

        verify(fileFixedSizeQueue).add(file1);
        assertEquals(fileFixedSizeQueue, reports.get(namespace));
    }

    @Test
    @DisplayName("getReportPathFrom should return the absolute path resulting from the composition of extent's report folder and filename")
    public void getReportPathFrom() {
        final String reportFolder = "reportFolder";
        final String fileName = "fileName";

        when(extent.getReportFolder()).thenReturn(reportFolder);
        when(extent.getFileName()).thenReturn(fileName);
        when(Path.of(reportFolder, fileName)).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);

        assertEquals(absolutePath, extentReporter.getReportPathFrom(extent));
    }

    @Test
    @DisplayName("createExtentTestFrom should create the test from the provided testData and return it")
    public void createExtentTestFrom() {
        final String testId = "testId";
        final String classDisplayName = "classDisplayName";
        final String methodDisplayName = "methodDisplayName";

        when(testData.getTestId()).thenReturn(testId);
        when(testData.getClassDisplayName()).thenReturn(classDisplayName);
        when(testData.getMethodDisplayName()).thenReturn(methodDisplayName);
        when(extentReports.createTest(String.format("<div id=\"%s\">%s</div>%s", testId, classDisplayName, methodDisplayName))).thenReturn(extentTest);

        assertEquals(extentTest, extentReporter.createExtentTestFrom(testData));
    }

    @Test
    @DisplayName("attachVideo should add the video in the provided extent test")
    public void attachVideo() {
        final String testId = "testId";
        final int width = 123;
        final int height = 456;

        when(testData.getTestId()).thenReturn(testId);
        when(testData.getVideoPath()).thenReturn(path);
        when(videoExtentTest.getWidth()).thenReturn(width);
        when(videoExtentTest.getHeight()).thenReturn(height);

        extentReporter.attachVideo(extentTest, videoExtentTest, testData);

        verify(extentTest).info(String.format("<video id=\"video-%s\" controls width=\"%d\" height=\"%d\" src=\"%s\" type=\"video/mp4\"/>", testId, width, height, path));
    }

    @Test
    @DisplayName("logTestStartOf should log the start label in the provided test")
    public void logTestStartOf() {
        extentReporter.logTestStartOf(extentTest);

        ArgumentCaptor<Markup> markupArgumentCaptor = ArgumentCaptor.forClass(Markup.class);
        verify(extentTest).info(markupArgumentCaptor.capture());
        Markup markup = markupArgumentCaptor.getValue();
        assertEquals(createLabel("START TEST", GREEN).getMarkup(), markup.getMarkup());
    }

    @DisplayName("getColorOf should return the color corresponding to the provided status")
    @ParameterizedTest()
    @MethodSource("valuesProvider")
    public void getColorOf(final Status status, final ExtentColor expected) {
        assertEquals(expected, extentReporter.getColorOf(status));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(FAIL, RED),
                arguments(SKIP, AMBER),
                arguments(INFO, GREEN)
        );
    }

    @DisplayName("logTestEnd should create the test in the report and delegate to finalizeTest")
    @ParameterizedTest(name = "with method {0} we expect {1}")
    @CsvSource({
            "noReasonMethod,no reason",
            "reasonMethod,specific reason"
    })
    public void logTestEndDisabled(final String methodName, String expected) throws NoSuchMethodException {
        final String classDisplayName = "classDisplayName";

        logTestEndStubs();
        when(context.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod(methodName));
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(classDisplayName);

        extentReporter.logTestEnd(context, SKIP);
        final ExtentTest extentTest = verifyAndGetExtentTest();

        verify(extentTest).skip(skipMarkupArgumentCaptor.capture());
        assertEquals("<span class='badge white-text amber'>Skipped: " + expected + "</span>", skipMarkupArgumentCaptor.getValue().getMarkup());
    }

    @Test
    @DisplayName("logTestEnd should add a screenshot to the report and delegate to finalizeTest")
    public void logTestEndFailed() {
        final String classDisplayName = "classDisplayName";

        logTestEndStubs();
        when(context.getRequiredTestInstance()).thenReturn(spectrumTest);
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(classDisplayName);
        when(context.getExecutionException()).thenReturn(Optional.of(exception));

        extentReporter.logTestEnd(context, FAIL);
        final ExtentTest extentTest = verifyAndGetExtentTest();

        verify(extentTest).fail(exception);
        verify(spectrumTest).screenshotFail("<span class='badge white-text red'>TEST FAILED</span>");
    }

    @Test
    @DisplayName("logTestEnd should add a log in the extent report by default")
    public void logTestEndDefault() {
        final String classDisplayName = "classDisplayName";

        logTestEndStubs();
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(classDisplayName);

        extentReporter.logTestEnd(context, PASS);

        final ExtentTest extentTest = verifyAndGetExtentTest();
        verify(extentTest).log(eq(PASS), markupArgumentCaptor.capture());
        assertEquals("<span class='badge white-text green'>END TEST</span>", markupArgumentCaptor.getValue().getMarkup());
    }

    @Disabled
    @SuppressWarnings("unused")
    private void noReasonMethod() {
    }

    @Disabled("specific reason")
    @SuppressWarnings("unused")
    private void reasonMethod() {
    }

    private void logTestEndStubs() {
        final String classDisplayName = "classDisplayName";
        final String methodDisplayName = "methodDisplayName";
        final String testId = "string-methoddisplayname";

        when(TestData.builder()).thenReturn(testDataBuilder);
        doReturn(String.class).when(context).getRequiredTestClass();
        when(context.getDisplayName()).thenReturn(methodDisplayName);
        when(testDataBuilder.classDisplayName(classDisplayName)).thenReturn(testDataBuilder);
        when(testDataBuilder.methodDisplayName(methodDisplayName)).thenReturn(testDataBuilder);
        when(testDataBuilder.testId(testId)).thenReturn(testDataBuilder);
        when(testDataBuilder.build()).thenReturn(testData);
        when(testData.getTestId()).thenReturn(testId);
        when(testData.getClassDisplayName()).thenReturn(classDisplayName);
        when(testData.getMethodDisplayName()).thenReturn(methodDisplayName);
        when(extentReports.createTest(String.format("<div id=\"%s\">%s</div>%s", testId, classDisplayName, methodDisplayName))).thenReturn(extentTest);

        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.getOrComputeIfAbsent(eq(EXTENT_TEST), functionArgumentCaptor.capture(), eq(ExtentTest.class))).thenReturn(extentTest);
    }

    private ExtentTest verifyAndGetExtentTest() {
        Function<String, ExtentTest> function = functionArgumentCaptor.getValue();
        return function.apply("value");
    }
}
