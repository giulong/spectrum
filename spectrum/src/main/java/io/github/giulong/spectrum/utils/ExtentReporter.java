package io.github.giulong.spectrum.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
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

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static com.aventstack.extentreports.Status.INFO;
import static com.aventstack.extentreports.Status.SKIP;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.buildTestIdFrom;
import static java.util.Comparator.comparingLong;
import static lombok.AccessLevel.PROTECTED;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
@NoArgsConstructor(access = PROTECTED)
@Getter
public class ExtentReporter implements SessionHook, CanProduceMetadata {

    private static final ExtentReporter INSTANCE = new ExtentReporter();

    protected final FileUtils fileUtils = FileUtils.getInstance();
    protected final Configuration configuration = Configuration.getInstance();

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

        extentReports = new ExtentReports();
        extentReports.attachReporter(new ExtentSparkReporter(reportPath)
                .config(ExtentSparkReporterConfig
                        .builder()
                        .documentTitle(extent.getDocumentTitle())
                        .reportName(reportName)
                        .theme(Theme.valueOf(extent.getTheme()))
                        .timeStampFormat(extent.getTimeStampFormat())
                        .css(fileUtils.read("/css/report.css"))
                        .build()));

        log.info("After the execution, you'll find the '{}' report at file:///{}", reportName, reportPath);
    }

    @SneakyThrows
    @Override
    public void sessionClosed() {
        log.debug("Session closed hook");

        final Configuration.Extent extent = configuration.getExtent();

        extentReports.flush();

        if (extent.isOpenAtEnd()) {
            log.debug("Opening extent report in default browser");
            Desktop.getDesktop().open(getReportPathFrom(extent).toFile());
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
        final File file = getReportPathFrom(configuration.getExtent()).toFile();
        final int maxSize = getRetention().getSuccessful();
        final FixedSizeQueue<File> queue = metadataManager.getSuccessfulQueueOf(this);

        log.debug("Adding metadata '{}'. Current size: {}, max capacity: {}", file, queue.size(), maxSize);
        queue.shrinkTo(maxSize - 1).add(file);
        metadataManager.setSuccessfulQueueOf(this, queue);
    }

    public void cleanupOldReportsIn(final String folder) {
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

        final List<File> files = Arrays
                .stream(folderContent)
                .filter(file -> !file.isDirectory())
                .sorted(comparingLong(File::lastModified))
                .toList();

        final List<File> directories = Arrays
                .stream(folderContent)
                .filter(File::isDirectory)
                .toList();

        final int toDelete = retention.deleteOldArtifactsFrom(files, this);

        for (int i = 0; i < toDelete; i++) {
            final String directoryName = fileUtils.removeExtensionFrom(files.get(i).getName());

            directories
                    .stream()
                    .peek(directory -> log.debug("Checking if directory needs to be deleted: {}", directory))
                    .filter(directory -> directory.getName().equals(directoryName))
                    .peek(directory -> log.debug("Found directory to delete: {}", directory))
                    .findFirst()
                    .map(File::toPath)
                    .ifPresent(fileUtils::deleteDirectory);
        }
    }

    public Path getReportPathFrom(final Configuration.Extent extent) {
        return Path.of(extent.getReportFolder(), extent.getFileName()).toAbsolutePath();
    }

    public ExtentTest createExtentTestFrom(final ExtensionContext context) {
        final TestData testData = context.getStore(GLOBAL).get(TEST_DATA, TestData.class);

        return extentReports.createTest(String.format("<div id=\"%s\">%s</div>%s", testData.getTestId(), testData.getClassDisplayName(), testData.getMethodDisplayName()));
    }

    public void attachVideo(final ExtentTest extentTest, final Video.ExtentTest videoExtentTest, final String testId, final Path path) {
        final int width = videoExtentTest.getWidth();
        final int height = videoExtentTest.getHeight();

        extentTest.info(String.format("<video id=\"video-%s\" controls width=\"%d\" height=\"%d\" src=\"%s\" type=\"video/mp4\"/>", testId, width, height, path));
    }

    public void logTestStartOf(final ExtentTest extentTest) {
        extentTest.info(createLabel("START TEST", getColorOf(INFO)));
    }

    protected ExtentColor getColorOf(final Status status) {
        return switch (status) {
            case FAIL -> RED;
            case SKIP -> AMBER;
            default -> GREEN;
        };
    }

    public void logTestEnd(final ExtensionContext context, final Status status) {
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final StatefulExtentTest statefulExtentTest = store.getOrComputeIfAbsent(STATEFUL_EXTENT_TEST, e -> {
            final String className = context.getRequiredTestClass().getSimpleName();
            final String classDisplayName = context.getParent().orElseThrow().getDisplayName();
            final String methodDisplayName = context.getDisplayName();
            final String testId = buildTestIdFrom(className, methodDisplayName);

            store.put(TEST_DATA, TestData
                    .builder()
                    .classDisplayName(classDisplayName)
                    .methodDisplayName(methodDisplayName)
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
}
