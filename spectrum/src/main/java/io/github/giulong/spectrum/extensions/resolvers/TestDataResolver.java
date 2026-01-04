package io.github.giulong.spectrum.extensions.resolvers;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.TestData;

import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class TestDataResolver extends TypeBasedParameterResolver<TestData> {

    public static final String TEST_DATA = "testData";

    private final FileUtils fileUtils = FileUtils.getInstance();
    private final ContextManager contextManager = ContextManager.getInstance();

    @Override
    public TestData resolveParameter(@NonNull final ParameterContext parameterContext, final ExtensionContext context) {
        log.debug("Resolving {}", TEST_DATA);

        final Class<?> clazz = context.getRequiredTestClass();
        final String className = clazz.getSimpleName();
        final String methodName = context.getRequiredTestMethod().getName();
        final String classDisplayName = fileUtils.sanitize(getDisplayNameOf(clazz));
        final String displayName = fileUtils.sanitize(joinTestDisplayNamesIn(context));
        final String testId = buildTestIdFrom(className, displayName);
        final Path videoPath = fileUtils.createTempFile("video", ".mp4");
        final Configuration.VisualRegression visualRegression = context.getRoot().getStore(GLOBAL).get(CONFIGURATION, Configuration.class).getVisualRegression();
        final Path visualRegressionPath = getVisualRegressionPathFrom(visualRegression.getSnapshots().getFolder(), classDisplayName, displayName);
        final TestData testData = TestData
                .builder()
                .className(className)
                .methodName(methodName)
                .classDisplayName(classDisplayName)
                .displayName(displayName)
                .testId(testId)
                .videoPath(videoPath)
                .visualRegression(TestData.VisualRegression
                        .builder()
                        .path(visualRegressionPath)
                        .build())
                .build();

        context.getStore(GLOBAL).put(TEST_DATA, testData);
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

    Path getVisualRegressionPathFrom(final String basePath, final String className, final String methodName) {
        return Path.of(basePath).resolve(className).resolve(methodName).toAbsolutePath();
    }

    static String transformInKebabCase(final String string) {
        return string.replaceAll("\\s", "-").toLowerCase();
    }
}
