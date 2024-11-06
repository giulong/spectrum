package io.github.giulong.spectrum.extensions.interceptors;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.DynamicTestInvocationContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.enums.Result.FAILED;
import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class SpectrumInterceptorTest {

    private final String className = "className";
    private final String displayName = "displayName";
    private final String fileName = "fileName";
    private final Path dynamicVideoPath = Path.of(String.format("%s-%s.mp4", fileName, displayName));

    @Mock
    private ContextManager contextManager;

    @Mock
    private InvocationInterceptor.Invocation<Void> invocation;

    @Mock
    private DynamicTestInvocationContext invocationContext;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext parentContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private TestData testData;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private Configuration configuration;

    @Mock
    private Video video;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private Path videoPath;

    @Mock
    private EventsDispatcher eventsDispatcher;

    @Mock
    private ExtentReporter extentReporter;

    @Mock
    private Video.ExtentTest videoExtentTest;

    @InjectMocks
    private SpectrumInterceptor spectrumInterceptor;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("eventsDispatcher", spectrumInterceptor, eventsDispatcher);
        Reflections.setField("extentReporter", spectrumInterceptor, extentReporter);
        Reflections.setField("fileUtils", spectrumInterceptor, fileUtils);
        Reflections.setField("contextManager", spectrumInterceptor, contextManager);
    }

    private void commonStubs() {
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)).thenReturn(statefulExtentTest);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.getExtentTest()).thenReturn(videoExtentTest);
        when(testData.getVideoPath()).thenReturn(videoPath);

        when(context.getRoot()).thenReturn(rootContext);
        when(context.getDisplayName()).thenReturn(displayName);
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);
        when(statefulExtentTest.createNode(displayName)).thenReturn(extentTest);

        when(fileUtils.removeExtensionFrom(videoPath.toString())).thenReturn(fileName);
    }

    @SuppressWarnings("checkstyle:IllegalThrows")
    private void commonVerifications() throws Throwable {
        verify(testData).setDisplayName(displayName);
        verify(testData).setDynamicVideoPath(dynamicVideoPath);
        verify(statefulExtentTest).setDisplayName(displayName);
        verify(statefulExtentTest).createNode(displayName);
        verify(statefulExtentTest).closeNode();

        verify(eventsDispatcher).fire(className, displayName, BEFORE, null, Set.of(DYNAMIC_TEST), context);
        verify(invocation).proceed();
        verify(contextManager).initWithParentFor(context);
    }

    @DisplayName("interceptDynamicTest should fire the proper events and create nodes in the current extent test")
    @ParameterizedTest(name = "with video disabled {0} and attach video {1}")
    @MethodSource("valuesProvider")
    @SuppressWarnings("checkstyle:IllegalThrows")
    void interceptDynamicTest(final boolean videoDisabled, final boolean attach) throws Throwable {
        commonStubs();

        when(video.isDisabled()).thenReturn(videoDisabled);
        lenient().when(videoExtentTest.isAttach()).thenReturn(attach);

        spectrumInterceptor.interceptDynamicTest(invocation, invocationContext, context);

        commonVerifications();
        verify(eventsDispatcher).fire(className, displayName, AFTER, SUCCESSFUL, Set.of(DYNAMIC_TEST), context);
        verifyNoInteractions(extentReporter);
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(true, true),
                arguments(true, false),
                arguments(false, false)
        );
    }

    @Test
    @DisplayName("interceptDynamicTest should fire the proper events and create nodes in the current extent test attaching the video")
    @SuppressWarnings("checkstyle:IllegalThrows")
    void interceptDynamicTestAttachVideo() throws Throwable {
        final String testId = "testId";

        commonStubs();

        when(video.isDisabled()).thenReturn(false);
        when(videoExtentTest.isAttach()).thenReturn(true);
        when(testData.getTestId()).thenReturn(testId);

        spectrumInterceptor.interceptDynamicTest(invocation, invocationContext, context);

        commonVerifications();
        verify(eventsDispatcher).fire(className, displayName, AFTER, SUCCESSFUL, Set.of(DYNAMIC_TEST), context);
        verify(extentReporter).attachVideo(extentTest, videoExtentTest, String.format("%s-%s", testId, displayName), dynamicVideoPath);
    }

    @Test
    @DisplayName("interceptDynamicTest should catch the invocation's exception, log it in extent test and fire the failed event")
    @SuppressWarnings("checkstyle:IllegalThrows")
    void interceptDynamicTestThrow() throws Throwable {
        commonStubs();

        when(invocation.proceed()).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> spectrumInterceptor.interceptDynamicTest(invocation, invocationContext, context));

        commonVerifications();
        verify(eventsDispatcher).fire(className, displayName, AFTER, FAILED, Set.of(DYNAMIC_TEST), context);
        verifyNoInteractions(extentReporter);
    }
}
