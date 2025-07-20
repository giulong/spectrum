package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class TestDataResolver extends TypeBasedParameterResolver<TestData> {

    public static final String TEST_DATA = "testData";

    private final FileUtils fileUtils = FileUtils.getInstance();
    private final ContextManager contextManager = ContextManager.getInstance();

    @Override
    public TestData resolveParameter(final ParameterContext arg0, final ExtensionContext context) {
        log.debug("Resolving {}", TEST_DATA);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final Configuration configuration = context.getRoot().getStore(GLOBAL).get(CONFIGURATION, Configuration.class);
        final Configuration.Extent extent = configuration.getExtent();
        final String reportFolder = extent.getReportFolder();
        final Class<?> clazz = context.getRequiredTestClass();
        final String className = clazz.getSimpleName();
        final String methodName = context.getRequiredTestMethod().getName();
        final String classDisplayName = fileUtils.sanitize(getDisplayNameOf(clazz));
        final String displayName = fileUtils.sanitize(joinTestDisplayNamesIn(context));
        final String testId = buildTestIdFrom(className, displayName);
        final String fileName = fileUtils.removeExtensionFrom(extent.getFileName());
        final Path videoPath = getVideoPathForCurrentTest(configuration.getVideo().isDisabled(), reportFolder, fileName, classDisplayName, displayName);
        final TestData testData = TestData
                .builder()
                .className(className)
                .methodName(methodName)
                .classDisplayName(classDisplayName)
                .displayName(displayName)
                .testId(testId)
                .videoPath(videoPath)
                .build();

        store.put(TEST_DATA, testData);
        contextManager.put(context, TEST_DATA, testData);
        return testData;
    }

    public static String buildTestIdFrom(final String className, final String testName) {
        return String.format("%s-%s", transformInKebabCase(className), transformInKebabCase(testName));
    }

    public static String getDisplayNameOf(final Class<?> clazz) {
        return clazz.isAnnotationPresent(DisplayName.class) ? clazz.getAnnotation(DisplayName.class).value() : clazz.getSimpleName();
    }

    public static String joinTestDisplayNamesIn(final ExtensionContext context) {
        final List<String> displayNames = new ArrayList<>();
        ExtensionContext currentContext = context;

        while (currentContext.getParent().orElseThrow().getParent().isPresent()) {
            displayNames.add(currentContext.getDisplayName());
            currentContext = currentContext.getParent().orElseThrow();
        }

        return String.join(" ", displayNames.reversed());
    }

    Path getVideoPathForCurrentTest(final boolean disabled, final String reportsFolder, final String extentFileName, final String className, final String methodName) {
        if (disabled) {
            log.trace("Video disabled: avoiding video folder creation");
            return null;
        }

        return fileUtils
                .deleteContentOf(Path.of(reportsFolder, extentFileName, "videos", className, methodName).toAbsolutePath())
                .resolve(String.format("%s.mp4", randomUUID()));
    }

    static String transformInKebabCase(final String string) {
        return string.replaceAll("\\s", "-").toLowerCase();
    }
}
