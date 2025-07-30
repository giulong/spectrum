package io.github.giulong.spectrum.extensions.interceptors;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.DynamicTestInvocationContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;

import java.nio.file.Path;
import java.util.Set;

import static io.github.giulong.spectrum.enums.Result.FAILED;
import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class SpectrumInterceptor implements InvocationInterceptor {

    private final EventsDispatcher eventsDispatcher = EventsDispatcher.getInstance();
    private final ExtentReporter extentReporter = ExtentReporter.getInstance();
    private final FileUtils fileUtils = FileUtils.getInstance();
    private final ContextManager contextManager = ContextManager.getInstance();

    @SuppressWarnings("checkstyle:IllegalCatch")
    @Override
    public void interceptDynamicTest(final Invocation<Void> invocation, final DynamicTestInvocationContext invocationContext, final ExtensionContext context) throws Throwable {
        log.debug("Intercepting dynamic test invocation");

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final TestData testData = store.get(TEST_DATA, TestData.class);
        final StatefulExtentTest statefulExtentTest = store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class);
        final Video video = store.get(CONFIGURATION, Configuration.class).getVideo();
        final Video.ExtentTest videoExtentTest = video.getExtentTest();
        final String className = context.getParent().orElse(context.getRoot()).getDisplayName();
        final String testName = context.getDisplayName();
        final Path dynamicVideoPath = Path.of(String.format("%s-%s.mp4", fileUtils.removeExtensionFrom(testData.getVideoPath().toString()), testName));
        final ExtentTest currentNode = statefulExtentTest.createNode(testName);
        final Set<String> tags = Set.of(DYNAMIC_TEST);

        testData.setDynamic(true);
        testData.setFrameNumber(0);
        testData.setDisplayName(testName);
        testData.setDynamicVideoPath(dynamicVideoPath);
        statefulExtentTest.setDisplayName(testName);
        contextManager.initWithParentFor(context);

        if (!video.isDisabled() && videoExtentTest.isAttach()) {
            final String fullId = String.format("%s-%s", testData.getTestId(), testName);
            extentReporter.attachVideo(currentNode, videoExtentTest, fullId, dynamicVideoPath);
        }

        try {
            eventsDispatcher.fire(className, testName, BEFORE_EXECUTION, null, tags, context);
            eventsDispatcher.fire(className, testName, BEFORE, null, tags, context);
            invocation.proceed();
            eventsDispatcher.fire(className, testName, AFTER, SUCCESSFUL, tags, context);
        } catch (Throwable t) {
            currentNode.fail(t);
            eventsDispatcher.fire(className, testName, AFTER, FAILED, tags, context);

            throw t;
        } finally {
            statefulExtentTest.closeNode();
        }
    }
}
