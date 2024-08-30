package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.nio.file.Path;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class TestDataResolver extends TypeBasedParameterResolver<TestData> {

    public static final String TEST_DATA = "testData";

    private final FileUtils fileUtils = FileUtils.getInstance();
    private final ContextManager contextManager = ContextManager.getInstance();

    @Override
    public TestData resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", TEST_DATA);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final Configuration configuration = context.getRoot().getStore(GLOBAL).get(CONFIGURATION, Configuration.class);
        final Configuration.Extent extent = configuration.getExtent();
        final String reportFolder = extent.getReportFolder();
        final String className = context.getRequiredTestClass().getSimpleName();
        final String methodName = context.getRequiredTestMethod().getName();
        final String classDisplayName = context.getParent().orElseThrow().getDisplayName();
        final String displayName = context.getDisplayName();
        final String testId = buildTestIdFrom(className, displayName);
        final String fileName = fileUtils.removeExtensionFrom(extent.getFileName());
        final Path screenshotFolderPath = getScreenshotFolderPathForCurrentTest(reportFolder, fileName, classDisplayName, displayName);
        final Path videoPath = getVideoPathForCurrentTest(configuration.getVideo().isDisabled(), reportFolder, fileName, classDisplayName, displayName);
        final TestData testData = TestData
                .builder()
                .className(className)
                .methodName(methodName)
                .classDisplayName(classDisplayName)
                .displayName(displayName)
                .testId(testId)
                .screenshotFolderPath(screenshotFolderPath)
                .videoPath(videoPath)
                .build();

        store.put(TEST_DATA, testData);
        contextManager.get(context.getUniqueId()).put(TEST_DATA, testData);
        return testData;
    }

    public Path getScreenshotFolderPathForCurrentTest(final String reportsFolder, final String extentFileName, final String className, final String methodName) {
        return fileUtils.deleteContentOf(Path.of(reportsFolder, extentFileName, "screenshots", className, methodName).toAbsolutePath());
    }

    public Path getVideoPathForCurrentTest(final boolean disabled, final String reportsFolder, final String extentFileName, final String className, final String methodName) {
        if (disabled) {
            log.trace("Video disabled: avoiding video folder creation");
            return null;
        }

        return fileUtils
                .deleteContentOf(Path.of(reportsFolder, extentFileName, "videos", className, methodName).toAbsolutePath())
                .resolve(String.format("%s.mp4", randomUUID()));
    }

    public static String buildTestIdFrom(final String className, final String testName) {
        return String.format("%s-%s", transformInKebabCase(className), transformInKebabCase(testName));
    }

    protected static String transformInKebabCase(final String string) {
        return string.replaceAll("\\s", "-").toLowerCase();
    }
}
