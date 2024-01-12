package io.github.giulong.spectrum.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
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

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.aventstack.extentreports.Status.INFO;
import static com.aventstack.extentreports.Status.SKIP;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static io.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.buildTestIdFrom;
import static java.util.Comparator.comparingLong;
import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
@Getter
public class ExtentReporter implements SessionHook, CanProduceMetadata {

    private static final ExtentReporter INSTANCE = new ExtentReporter();

    private final FileUtils fileUtils = FileUtils.getInstance();
    private final Configuration configuration = Configuration.getInstance();

    private ExtentReports extentReports;

    public static ExtentReporter getInstance() {
        return INSTANCE;
    }

    @Override
    public void sessionOpenedFrom(final Configuration configuration) {
        log.debug("Session opened hook");

        final Configuration.Extent extent = configuration.getExtent();
        final String reportPath = getReportPathFrom(extent).toString().replace("\\", "/");
        final String reportName = extent.getReportName();
        final ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

        sparkReporter.config().setDocumentTitle(extent.getDocumentTitle());
        sparkReporter.config().setReportName(reportName);
        sparkReporter.config().setTheme(Theme.valueOf(extent.getTheme()));
        sparkReporter.config().setTimeStampFormat(extent.getTimeStampFormat());
        sparkReporter.config().setCss(fileUtils.read("/css/report.css"));

        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);

        log.info("After the execution, you'll find the '{}' report at file:///{}", reportName, reportPath);
    }

    @Override
    public void sessionClosedFrom(final Configuration configuration) {
        log.debug("Session closed hook");
        extentReports.flush();
        cleanupOldReports(configuration.getExtent());
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
        queue
                .shrinkTo(maxSize - 1)
                .add(file);

        metadataManager.setSuccessfulQueueOf(this, queue);
    }

    @SneakyThrows
    public void cleanupOldReports(final Configuration.Extent extent) {
        final Retention retention = extent.getRetention();
        log.info("Extent reports to keep: {}", retention.getTotal());

        final File[] folderContent = Objects
                .requireNonNull(Path
                        .of(extent.getReportFolder())
                        .toFile()
                        .listFiles());

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
                    .filter(directory -> directory.getName().equals(directoryName))
                    .findFirst()
                    .map(File::toPath)
                    .ifPresent(fileUtils::deleteDirectory);
        }
    }

    public Path getReportPathFrom(final Configuration.Extent extent) {
        return Path.of(extent.getReportFolder(), extent.getFileName()).toAbsolutePath();
    }

    public ExtentTest createExtentTestFrom(final TestData testData) {
        return extentReports.createTest(String.format("<div id=\"%s\">%s</div>%s", testData.getTestId(), testData.getClassDisplayName(), testData.getMethodDisplayName()));
    }

    public void attachVideo(final ExtentTest extentTest, final Video.ExtentTest videoExtentTest, final TestData testData) {
        final int width = videoExtentTest.getWidth();
        final int height = videoExtentTest.getHeight();

        extentTest.info(String.format("<video id=\"video-%s\" controls width=\"%d\" height=\"%d\" src=\"%s\" type=\"video/mp4\"/>",
                testData.getTestId(), width, height, testData.getVideoPath()));
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
        final ExtentTest extentTest = store.getOrComputeIfAbsent(EXTENT_TEST, e -> {
            final String className = context.getRequiredTestClass().getSimpleName();
            final String classDisplayName = context.getParent().orElseThrow().getDisplayName();
            final String methodDisplayName = context.getDisplayName();
            final String testId = buildTestIdFrom(className, methodDisplayName);
            final TestData testData = TestData
                    .builder()
                    .classDisplayName(classDisplayName)
                    .methodDisplayName(methodDisplayName)
                    .testId(testId)
                    .build();

            return createExtentTestFrom(testData);
        }, ExtentTest.class);

        switch (status) {
            case SKIP -> {
                final String disabledValue = context.getRequiredTestMethod().getAnnotation(Disabled.class).value();
                final String reason = "".equals(disabledValue) ? "no reason" : disabledValue;
                extentTest.skip(createLabel("Skipped: " + reason, getColorOf(SKIP)));
            }
            case FAIL -> {
                final SpectrumTest<?> spectrumTest = (SpectrumTest<?>) context.getRequiredTestInstance();
                extentTest.fail(context.getExecutionException().orElse(new RuntimeException("Test Failed with no exception")));
                spectrumTest.screenshotFail(createLabel("TEST FAILED", RED).getMarkup());
            }
            default -> extentTest.log(status, createLabel("END TEST", getColorOf(status)));
        }
    }
}
