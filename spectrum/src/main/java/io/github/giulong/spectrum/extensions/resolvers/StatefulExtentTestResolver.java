package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.ExtentReporter;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class StatefulExtentTestResolver extends TypeBasedParameterResolver<StatefulExtentTest> {

    public static final String STATEFUL_EXTENT_TEST = "statefulExtentTest";

    private final ExtentReporter extentReporter = ExtentReporter.getInstance();
    private final ContextManager contextManager = ContextManager.getInstance();

    @Override
    public StatefulExtentTest resolveParameter(@NonNull final ParameterContext parameterContext, final ExtensionContext context) {
        log.debug("Resolving {}", STATEFUL_EXTENT_TEST);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final TestData testData = store.get(TEST_DATA, TestData.class);
        final ExtentTest extentTest = extentReporter.createExtentTestFrom(context);
        final Video video = store.get(CONFIGURATION, Configuration.class).getVideo();
        final Video.ExtentTest videoExtentTest = video.getExtentTest();
        final StatefulExtentTest statefulExtentTest = StatefulExtentTest
                .builder()
                .currentNode(extentTest)
                .build();

        if (!video.isDisabled() && videoExtentTest.isAttach()) {
            extentReporter.attachVideo(extentTest, videoExtentTest, testData.getTestId(), testData.getVideoPath());
        }

        extentReporter.logTestStartOf(extentTest);
        store.put(STATEFUL_EXTENT_TEST, statefulExtentTest);
        contextManager.put(context, STATEFUL_EXTENT_TEST, statefulExtentTest);

        return statefulExtentTest;
    }
}
