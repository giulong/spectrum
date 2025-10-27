package io.github.giulong.spectrum.utils;

import static com.aventstack.extentreports.Status.INFO;
import static com.aventstack.extentreports.Status.SKIP;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.*;
import static lombok.AccessLevel.PRIVATE;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.model.Report;
import com.aventstack.extentreports.model.Test;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ExtentSparkReporterConfig;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.interfaces.SessionHook;
import io.github.giulong.spectrum.interfaces.reports.CanProduceMetadata;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
@Getter
@NoArgsConstructor(access = PRIVATE)
public class ExtentReporter implements SessionHook, CanProduceMetadata {

    private static final ExtentReporter INSTANCE = new ExtentReporter();

    protected final FileUtils fileUtils = FileUtils.getInstance();
    protected final Configuration configuration = Configuration.getInstance();
    protected final HtmlUtils htmlUtils = HtmlUtils.getInstance();

    private final ContextManager contextManager = ContextManager.getInstance();

    private ExtentReports extentReports;

    public static ExtentReporter getInstance() {
        return INSTANCE;
    }

    @Override
    public void sessionOpened() {
        log.debug("Session opened hook");

        final Configuration.Extent extent = configuration.getExtent();
        final String reportPath = getReportPathFrom(extent).toString().replace("\\", "/");
        final String reportName = extent.getReportName();
        final String css = fileUtils.read("css/internal-report.css") + fileUtils.read(extent.getCss());
        final String js = fileUtils.read("js/internal-report.js") + fileUtils.read(extent.getJs());

        extentReports = new ExtentReports();
        extentReports.attachReporter(new ExtentSparkReporter(reportPath)
                .config(ExtentSparkReporterConfig
                        .builder()
                        .documentTitle(extent.getDocumentTitle())
                        .reportName(reportName)
                        .theme(Theme.valueOf(extent.getTheme()))
                        .timeStampFormat(extent.getTimeStampFormat())
                        .css(css)
                        .js(js)
                        .build()));

        log.info("After the execution, you'll find the '{}' report at file:///{}", reportName, reportPath);
    }

    @SneakyThrows
    @Override
    public void sessionClosed() {
        log.debug("Session closed hook");

        sortTests();
        extentReports.flush();

        final Configuration.Extent extent = configuration.getExtent();
        final Path path = getReportPathFrom(extent);
        final String inlineReport = htmlUtils.inline(Files.readString(path));

        fileUtils.write(path, inlineReport);

        if (extent.isOpenAtEnd()) {
            log.debug("Opening extent report in default browser");
            Desktop.getDesktop().open(path.toFile());
        }

        cleanupOldReportsIn(extent.getReportFolder());
    }

    @Override
    public Retention getRetention() {
        return configuration.getExtent().getRetention();
    }

    @Override
    public void produceMetadata() {
        final MetadataManager metadataManager = MetadataManager.getInstance();
        final File file = getMetadata().toFile();
        final int maxSize = getRetention().getSuccessful();
        final FixedSizeQueue<File> queue = metadataManager.getSuccessfulQueueOf(this);

        log.debug("Adding metadata '{}'. Current size: {}, max capacity: {}", file, queue.size(), maxSize);
        queue.shrinkTo(maxSize - 1).add(file);
        metadataManager.setSuccessfulQueueOf(this, queue);
    }

    public ExtentTest createExtentTestFrom(final ExtensionContext context) {
        final TestData testData = contextManager.get(context, TEST_DATA, TestData.class);
        final String tag = htmlUtils.generateTestInfoDivs(testData.getTestId(), testData.getClassDisplayName(), testData.getDisplayName());

        return extentReports
                .createTest(tag)
                .assignCategory(context.getTags().toArray(new String[0]));
    }

    public void attachVideo(final ExtentTest extentTest, final Video.ExtentTest videoExtentTest, final String testId, final Path path) {
        final String width = videoExtentTest.getWidth();
        final String height = videoExtentTest.getHeight();
        extentTest.info(htmlUtils.generateVideoTag(testId, width, height, path));
    }

    public void logTestStartOf(final ExtentTest extentTest) {
        extentTest.info(createLabel("START TEST", getColorOf(INFO)));
    }

    public void logTestEnd(final ExtensionContext context, final Status status) {
        final TestContext testContext = contextManager.get(context);
        final StatefulExtentTest statefulExtentTest = testContext.computeIfAbsent(STATEFUL_EXTENT_TEST, k -> {
            final Class<?> clazz = context.getRequiredTestClass();
            final String className = clazz.getSimpleName();
            final String methodName = context.getRequiredTestMethod().getName();
            final String classDisplayName = fileUtils.sanitize(getDisplayNameOf(clazz));
            final String displayName = fileUtils.sanitize(joinTestDisplayNamesIn(context));
            final String testId = buildTestIdFrom(className, displayName);

            testContext.put(TEST_DATA, TestData
                    .builder()
                    .className(className)
                    .methodName(methodName)
                    .classDisplayName(classDisplayName)
                    .displayName(displayName)
                    .testId(testId)
                    .build());

            return StatefulExtentTest
                    .builder()
                    .currentNode(createExtentTestFrom(context))
                    .build();
        }, StatefulExtentTest.class);

        switch (status) {
            case SKIP -> {
                final String disabledValue = context.getRequiredTestMethod().getAnnotation(Disabled.class).value();
                final String reason = "".equals(disabledValue) ? "no reason" : disabledValue;
                statefulExtentTest.getCurrentNode().skip(createLabel("Skipped: " + reason, getColorOf(SKIP)));
            }
            case FAIL -> {
                final SpectrumTest<?> spectrumTest = (SpectrumTest<?>) context.getRequiredTestInstance();
                statefulExtentTest.getCurrentNode().fail(context.getExecutionException().orElse(new RuntimeException("Test Failed with no exception")));
                spectrumTest.screenshotFail(createLabel("TEST FAILED", RED).getMarkup());
            }
            default -> statefulExtentTest.getCurrentNode().log(status, createLabel("END TEST", getColorOf(status)));
        }
    }

    Path getMetadata() {
        return getReportPathFrom(configuration.getExtent()).getParent();
    }

    Path getReportPathFrom(final Configuration.Extent extent) {
        final String fileName = extent.getFileName();
        return Path.of(extent.getReportFolder(), fileUtils.removeExtensionFrom(fileName), fileName).toAbsolutePath();
    }

    void sortTests() {
        final Report report = extentReports.getReport();
        final List<Test> tests = new ArrayList<>(report.getTestList());

        tests.stream().map(Test::getName).forEach(extentReports::removeTest);
        tests.sort(configuration.getExtent().getSort());
        tests.forEach(report::addTest);
    }

    void cleanupOldReportsIn(final String folder) {
        final Retention retention = configuration.getExtent().getRetention();
        log.info("Extent reports to keep in {}: {}", folder, retention.getTotal());

        final File[] folderContent = Path
                .of(folder)
                .toFile()
                .listFiles();

        if (folderContent == null) {
            log.debug("Extent reports folder {} is empty already", folder);
            return;
        }

        retention.deleteArtifactsFrom(List.of(folderContent), this);
    }

    ExtentColor getColorOf(final Status status) {
        return switch (status) {
            case FAIL -> RED;
            case SKIP -> AMBER;
            default -> GREEN;
        };
    }
}
