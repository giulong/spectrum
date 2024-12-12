package io.github.giulong.spectrum.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.model.Report;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ExtentSparkReporterConfig;
import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.tests_comparators.TestsComparator;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.aventstack.extentreports.Status.*;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.aventstack.extentreports.reporter.configuration.Theme.DARK;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class ExtentReporterTest {

    private static final String REPORT_FOLDER = "reportFolder";
    private static final String NAME_ONE = "name1";
    private static final String NAME_TWO = "name2";

    private MockedStatic<TestData> testDataMockedStatic;
    private MockedStatic<FreeMarkerWrapper> freeMarkerWrapperMockedStatic;
    private MockedStatic<Path> pathMockedStatic;
    private MockedStatic<FileUtils> fileUtilsMockedStatic;
    private MockedStatic<Files> filesMockedStatic;
    private MockedStatic<MetadataManager> metadataManagerMockedStatic;
    private MockedStatic<Desktop> desktopMockedStatic;
    private MockedStatic<ExtentSparkReporterConfig> extentSparkReporterConfigMockedStatic;

    @Mock
    private ExtentSparkReporterConfig.ExtentSparkReporterConfigBuilder<?, ?> extentSparkReporterConfigBuilder;

    @Mock
    private ContextManager contextManager;

    @Mock
    private TestContext testContext;

    @Mock
    private Configuration configuration;

    @Mock
    private ExtentSparkReporterConfig extentSparkReporterConfig;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private TestData.TestDataBuilder testDataBuilder;

    @Mock
    private TestData testData;

    @Mock
    private Path path;

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
    private StatefulExtentTest statefulExtentTest;

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
    private Desktop desktop;

    @Mock
    private SpectrumTest<?> spectrumTest;

    @Mock
    private Report report;

    @Mock
    private com.aventstack.extentreports.model.Test test1;

    @Mock
    private com.aventstack.extentreports.model.Test test2;

    @Mock
    private TestsComparator sort;

    @Mock
    private MetadataManager metadataManager;

    @Captor
    private ArgumentCaptor<Function<String, StatefulExtentTest>> statefulExtentTestArgumentCaptor;

    @Captor
    private ArgumentCaptor<Markup> markupArgumentCaptor;

    @Captor
    private ArgumentCaptor<Markup> skipMarkupArgumentCaptor;

    @InjectMocks
    private ExtentReporter extentReporter;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("fileUtils", extentReporter, fileUtils);
        Reflections.setField("configuration", extentReporter, configuration);
        Reflections.setField("contextManager", extentReporter, contextManager);

        testDataMockedStatic = mockStatic(TestData.class);
        freeMarkerWrapperMockedStatic = mockStatic(FreeMarkerWrapper.class);
        pathMockedStatic = mockStatic(Path.class);
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
        filesMockedStatic = mockStatic(Files.class);
        metadataManagerMockedStatic = mockStatic(MetadataManager.class);
        desktopMockedStatic = mockStatic(Desktop.class);
        extentSparkReporterConfigMockedStatic = mockStatic(ExtentSparkReporterConfig.class);
    }

    @AfterEach
    void afterEach() {
        testDataMockedStatic.close();
        freeMarkerWrapperMockedStatic.close();
        pathMockedStatic.close();
        fileUtilsMockedStatic.close();
        filesMockedStatic.close();
        metadataManagerMockedStatic.close();
        desktopMockedStatic.close();
        extentSparkReporterConfigMockedStatic.close();
    }

    private void sortTestStubs() {
        when(extentReports.getReport()).thenReturn(report);
        when(report.getTestList()).thenReturn(List.of(test1, test2));
        when(test1.getName()).thenReturn(NAME_ONE);
        when(test2.getName()).thenReturn(NAME_TWO);

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getSort()).thenReturn(sort);
    }

    private void cleanupOldReportsStubs() {
        final int total = 123;
        final String file1Name = "file1Name";
        final String file2Name = "file2Name";
        final String directory1Name = "directory1Name";
        final String directory2Name = "directory2Name";

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);
        when(retention.getTotal()).thenReturn(total);

        when(Path.of(REPORT_FOLDER)).thenReturn(path);
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

        when(fileUtils.removeExtensionFrom(file1Name)).thenReturn(directory1Name);
        when(fileUtils.removeExtensionFrom(file2Name)).thenReturn(directory2Name);
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(ExtentReporter.getInstance(), ExtentReporter.getInstance());
    }

    @Test
    @DisplayName("sessionOpened should init the extent report")
    void sessionOpened() {
        final String fileName = "fileName";
        final String fileNameWithoutExtension = "fileNameWithoutExtension";
        final String reportName = "reportName";
        final String documentTitle = "documentTitle";
        final String theme = "DARK";
        final String timeStampFormat = "timeStampFormat";
        final String css = "css";
        final String js = "js";
        final String extentCss = "extentCss";
        final String extentJs = "extentJs";
        final String absolutePathToString = "absolute\\Path\\To\\String";
        final String absolutePathToStringReplaced = "absolute/Path/To/String";

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(REPORT_FOLDER);
        when(extent.getFileName()).thenReturn(fileName);
        when(fileUtils.removeExtensionFrom(fileName)).thenReturn(fileNameWithoutExtension);
        when(Path.of(REPORT_FOLDER, fileNameWithoutExtension, fileName)).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);
        when(absolutePath.toString()).thenReturn(absolutePathToString);
        when(extent.getReportName()).thenReturn(reportName);
        when(extent.getDocumentTitle()).thenReturn(documentTitle);
        when(extent.getTheme()).thenReturn(theme);
        when(extent.getTimeStampFormat()).thenReturn(timeStampFormat);
        when(extent.getCss()).thenReturn(extentCss);
        when(fileUtils.read(extentCss)).thenReturn(css);
        when(extent.getJs()).thenReturn(extentJs);
        when(fileUtils.read(extentJs)).thenReturn(js);

        extentSparkReporterConfigMockedStatic.when(ExtentSparkReporterConfig::builder).thenReturn(extentSparkReporterConfigBuilder);
        doReturn(extentSparkReporterConfigBuilder).when(extentSparkReporterConfigBuilder).documentTitle(documentTitle);
        doReturn(extentSparkReporterConfigBuilder).when(extentSparkReporterConfigBuilder).reportName(reportName);
        doReturn(extentSparkReporterConfigBuilder).when(extentSparkReporterConfigBuilder).theme(DARK);
        doReturn(extentSparkReporterConfigBuilder).when(extentSparkReporterConfigBuilder).timeStampFormat(timeStampFormat);
        doReturn(extentSparkReporterConfigBuilder).when(extentSparkReporterConfigBuilder).css(css);
        doReturn(extentSparkReporterConfigBuilder).when(extentSparkReporterConfigBuilder).js(js);
        doReturn(extentSparkReporterConfig).when(extentSparkReporterConfigBuilder).build();

        MockedConstruction<ExtentReports> extentReportsMockedConstruction = mockConstruction(ExtentReports.class);

        MockedConstruction<ExtentSparkReporter> extentSparkReporterMockedConstruction = mockConstruction(ExtentSparkReporter.class, (mock, executionContext) -> {
            assertEquals(absolutePathToStringReplaced, executionContext.arguments().getFirst());
            when(mock.config(extentSparkReporterConfig)).thenReturn(mock);
        });

        extentReporter.sessionOpened();

        final ExtentReports localExtentReports = extentReportsMockedConstruction.constructed().getFirst();
        verify(localExtentReports).attachReporter(extentSparkReporterMockedConstruction.constructed().toArray(new ExtentSparkReporter[0]));

        extentSparkReporterMockedConstruction.close();
        extentReportsMockedConstruction.close();
    }

    @Test
    @DisplayName("sessionClosed should flush the extent report and cleanup old ones")
    void sessionClosed() {
        final int total = 123;

        cleanupOldReportsStubs();
        sortTestStubs();

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);
        when(retention.getTotal()).thenReturn(total);
        when(extent.getReportFolder()).thenReturn(REPORT_FOLDER);

        extentReporter.sessionClosed();

        verify(extentReports).flush();

        // cleanupOldReports
        verify(fileUtils).deleteDirectory(directory1Path);
        verify(fileUtils).deleteDirectory(directory2Path);
    }

    @Test
    @DisplayName("sessionClosed should open the report")
    void sessionClosedOpenReport() throws IOException {
        final int total = 123;
        final String reportFolder = "reportFolder";
        final String fileName = "fileName";
        final String fileNameWithoutExtension = "fileNameWithoutExtension";

        cleanupOldReportsStubs();
        sortTestStubs();

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);
        when(retention.getTotal()).thenReturn(total);
        when(extent.getReportFolder()).thenReturn(REPORT_FOLDER);

        when(extent.isOpenAtEnd()).thenReturn(true);

        when(extent.getReportFolder()).thenReturn(reportFolder);
        when(extent.getFileName()).thenReturn(fileName);
        when(fileUtils.removeExtensionFrom(fileName)).thenReturn(fileNameWithoutExtension);
        when(Path.of(reportFolder, fileNameWithoutExtension, fileName)).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);
        when(absolutePath.toFile()).thenReturn(file1);
        when(Desktop.getDesktop()).thenReturn(desktop);

        extentReporter.sessionClosed();

        verify(extentReports).flush();

        // cleanupOldReports
        verify(fileUtils).deleteDirectory(directory1Path);
        verify(fileUtils).deleteDirectory(directory2Path);
        verify(desktop).open(file1);
    }

    @Test
    @DisplayName("cleanupOldReportsIn should delete the proper number of old reports and the corresponding directories")
    void cleanupOldReportsIn() {
        cleanupOldReportsStubs();

        extentReporter.cleanupOldReportsIn(REPORT_FOLDER);

        verify(fileUtils).deleteDirectory(directory1Path);
        verify(fileUtils).deleteDirectory(directory2Path);
    }

    @Test
    @DisplayName("cleanupOldReportsIn should return if the provided folder is empty")
    void cleanupOldReportsInEmptyFolder() {
        final String localFolder = "localFolder";
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);
        when(Path.of(localFolder)).thenReturn(path);
        when(path.toFile()).thenReturn(file1);

        extentReporter.cleanupOldReportsIn(localFolder);

        verifyNoInteractions(fileUtils);
    }

    @Test
    @DisplayName("getRetention should return the extent's retention")
    void getRetention() {
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);

        assertEquals(retention, extentReporter.getRetention());
    }

    @Test
    @DisplayName("produceMetadata should shrink the queue in the provided metadata bound to the given namespace, and add the new element to it")
    void produceMetadata() {
        final String reportFolder = "reportFolder";
        final String fileName = "fileName";
        final String fileNameWithoutExtension = "fileNameWithoutExtension";
        final String namespace = "namespace";
        final int retentionSuccessful = 123;
        final Map<String, FixedSizeQueue<File>> reports = new HashMap<>(Map.of(namespace, fileFixedSizeQueue));

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getRetention()).thenReturn(retention);

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(reportFolder);
        when(extent.getFileName()).thenReturn(fileName);
        when(fileUtils.removeExtensionFrom(fileName)).thenReturn(fileNameWithoutExtension);
        when(Path.of(reportFolder, fileNameWithoutExtension, fileName)).thenReturn(path);
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
    @DisplayName("sortTests should sort the tests with the configured comparator")
    void sortTests() {
        sortTestStubs();

        extentReporter.sortTests();

        verify(extentReports).removeTest(NAME_ONE);
        verify(extentReports).removeTest(NAME_TWO);
        verify(report).addTest(test1);
        verify(report).addTest(test2);
    }

    @Test
    @DisplayName("getReportPathFrom should return the absolute path resulting from the composition of extent's report folder and filename")
    void getReportPathFrom() {
        final String reportFolder = "reportFolder";
        final String fileName = "fileName";
        final String fileNameWithoutExtension = "fileNameWithoutExtension";

        when(extent.getReportFolder()).thenReturn(reportFolder);
        when(extent.getFileName()).thenReturn(fileName);
        when(fileUtils.removeExtensionFrom(fileName)).thenReturn(fileNameWithoutExtension);
        when(Path.of(reportFolder, fileNameWithoutExtension, fileName)).thenReturn(path);
        when(path.toAbsolutePath()).thenReturn(absolutePath);

        assertEquals(absolutePath, extentReporter.getReportPathFrom(extent));
    }

    @Test
    @DisplayName("createExtentTestFrom should create the test from the provided testData and return it")
    void createExtentTestFrom() {
        final String testId = "testId";
        final String classDisplayName = "classDisplayName";
        final String displayName = "displayName";
        final Set<String> tags = Set.of("t1", "t2");

        when(contextManager.get(context, TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getTestId()).thenReturn(testId);
        when(testData.getClassDisplayName()).thenReturn(classDisplayName);
        when(testData.getDisplayName()).thenReturn(displayName);
        when(context.getTags()).thenReturn(tags);
        when(extentReports.createTest(String.format("<div id=\"%s\">%s</div>%s", testId, classDisplayName, displayName))).thenReturn(extentTest);
        when(extentTest.assignCategory(tags.toArray(new String[0]))).thenReturn(extentTest);

        assertEquals(extentTest, extentReporter.createExtentTestFrom(context));
    }

    @Test
    @DisplayName("attachVideo should add the video in the provided extent test")
    void attachVideo() {
        final String testId = "testId";
        final int width = 123;
        final int height = 456;

        when(videoExtentTest.getWidth()).thenReturn(width);
        when(videoExtentTest.getHeight()).thenReturn(height);

        extentReporter.attachVideo(extentTest, videoExtentTest, testId, path);

        verify(extentTest).info(String.format("<video id=\"video-%s\" controls width=\"%d\" height=\"%d\" src=\"%s\" type=\"video/mp4\"/>", testId, width, height, path));
    }

    @Test
    @DisplayName("logTestStartOf should log the start label in the provided test")
    void logTestStartOf() {
        extentReporter.logTestStartOf(extentTest);

        final ArgumentCaptor<Markup> localMarkupArgumentCaptor = ArgumentCaptor.forClass(Markup.class);
        verify(extentTest).info(localMarkupArgumentCaptor.capture());
        Markup markup = localMarkupArgumentCaptor.getValue();
        assertEquals(createLabel("START TEST", GREEN).getMarkup(), markup.getMarkup());
    }

    @DisplayName("getColorOf should return the color corresponding to the provided status")
    @ParameterizedTest()
    @MethodSource("valuesProvider")
    void getColorOf(final Status status, final ExtentColor expected) {
        assertEquals(expected, extentReporter.getColorOf(status));
    }

    static Stream<Arguments> valuesProvider() {
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
    void logTestEndDisabled(final String methodName, String expected) throws NoSuchMethodException {
        logTestEndStubs();
        when(context.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod(methodName));
        when(testDataBuilder.methodName(methodName)).thenReturn(testDataBuilder);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        extentReporter.logTestEnd(context, SKIP);

        statefulExtentTestArgumentCaptor.getValue().apply("value");

        verify(extentTest).skip(skipMarkupArgumentCaptor.capture());
        assertEquals("<span class='badge white-text amber'>Skipped: " + expected + "</span>", skipMarkupArgumentCaptor.getValue().getMarkup());
    }

    @Test
    @DisplayName("logTestEnd should add a screenshot to the report and delegate to finalizeTest")
    void logTestEndFailed() throws NoSuchMethodException {
        final String classDisplayName = "classDisplayName";
        final String methodName = "logTestEndStubs";

        logTestEndStubs();
        when(context.getRequiredTestInstance()).thenReturn(spectrumTest);
        when(context.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod(methodName));
        when(testDataBuilder.methodName(methodName)).thenReturn(testDataBuilder);
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(classDisplayName);
        when(context.getExecutionException()).thenReturn(Optional.of(exception));
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        extentReporter.logTestEnd(context, FAIL);
        statefulExtentTestArgumentCaptor.getValue().apply("value");

        verify(extentTest).fail(exception);
        verify(spectrumTest).screenshotFail("<span class='badge white-text red'>TEST FAILED</span>");
    }

    @Test
    @DisplayName("logTestEnd should add a log in the extent report by default")
    void logTestEndDefault() throws NoSuchMethodException {
        final String classDisplayName = "classDisplayName";
        final String methodName = "logTestEndStubs";

        logTestEndStubs();
        when(context.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod(methodName));
        when(testDataBuilder.methodName(methodName)).thenReturn(testDataBuilder);
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(classDisplayName);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        extentReporter.logTestEnd(context, PASS);

        statefulExtentTestArgumentCaptor.getValue().apply("value");
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

    @SneakyThrows
    private void logTestEndStubs() {
        final String className = "String";
        final String classDisplayName = "classDisplayName";
        final String displayName = "displayName";
        final String testId = "string-displayname";

        when(TestData.builder()).thenReturn(testDataBuilder);
        doReturn(String.class).when(context).getRequiredTestClass();
        when(context.getDisplayName()).thenReturn(displayName);
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(classDisplayName);
        when(testDataBuilder.className(className)).thenReturn(testDataBuilder);
        when(testDataBuilder.classDisplayName(classDisplayName)).thenReturn(testDataBuilder);
        when(testDataBuilder.displayName(displayName)).thenReturn(testDataBuilder);
        when(testDataBuilder.testId(testId)).thenReturn(testDataBuilder);
        when(testDataBuilder.build()).thenReturn(testData);
        when(testData.getTestId()).thenReturn(testId);
        when(testData.getClassDisplayName()).thenReturn(classDisplayName);
        when(testData.getDisplayName()).thenReturn(displayName);
        when(extentReports.createTest(String.format("<div id=\"%s\">%s</div>%s", testId, classDisplayName, displayName))).thenReturn(extentTest);

        when(contextManager.get(context)).thenReturn(testContext);
        when(contextManager.get(context, TEST_DATA, TestData.class)).thenReturn(testData);
        when(testContext.computeIfAbsent(eq(STATEFUL_EXTENT_TEST), statefulExtentTestArgumentCaptor.capture(), eq(StatefulExtentTest.class))).thenReturn(statefulExtentTest);
    }
}
