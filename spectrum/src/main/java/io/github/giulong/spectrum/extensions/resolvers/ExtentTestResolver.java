package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.ExtentReporter;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentTestResolver extends TypeBasedParameterResolver<ExtentTest> {

    public static final String EXTENT_TEST = "extentTest";

    private final ExtentReporter extentReporter = ExtentReporter.getInstance();

    @Override
    public ExtentTest resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", EXTENT_TEST);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final TestData testData = store.get(TEST_DATA, TestData.class);
        final ExtentTest extentTest = extentReporter.createExtentTestFrom(testData);
        final Video video = store.get(CONFIGURATION, Configuration.class).getVideo();
        final Video.ExtentTest videoExtentTest = video.getExtentTest();

        if (!video.isDisabled() && videoExtentTest.isAttach()) {
            extentReporter.attachVideo(extentTest, videoExtentTest, testData);
        }

        extentReporter.logTestStartOf(extentTest);
        store.put(EXTENT_TEST, extentTest);

        return extentTest;
    }
}
