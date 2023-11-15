package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.types.TestData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class TestDataResolver extends TypeBasedParameterResolver<TestData> {

    public static final String TEST_DATA = "testData";

    @Override
    public TestData resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", TEST_DATA);
        final Configuration.Extent extent = context.getRoot().getStore(GLOBAL).get(CONFIGURATION, Configuration.class).getExtent();
        final String reportFolder = extent.getReportFolder();
        final String className = context.getRequiredTestClass().getSimpleName();
        final String methodName = context.getRequiredTestMethod().getName();
        final Path screenshotFolderPath = getScreenshotFolderPathForCurrentTest(reportFolder, className, methodName);
        final TestData testData = TestData
                .builder()
                .className(className)
                .methodName(methodName)
                .screenshotFolderPath(screenshotFolderPath)
                .build();

        context.getStore(GLOBAL).put(TEST_DATA, testData);
        return testData;
    }

    @SneakyThrows
    public Path getScreenshotFolderPathForCurrentTest(final String reportsFolder, final String className, final String methodName) {
        final Path path = Path.of(reportsFolder, "screenshots", className, methodName).toAbsolutePath();
        Files.createDirectories(path);
        return path;
    }
}
