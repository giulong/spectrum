package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.stream.Stream;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class StatefulExtentTestResolverTest {

    private static MockedStatic<ExtentReporter> extentReporterMockedStatic;
    private static MockedStatic<StatefulExtentTest> statefulExtentTestMockedStatic;

    @Mock
    private StatefulExtentTest.StatefulExtentTestBuilder statefulExtentTestBuilder;

    @Mock
    private ContextManager contextManager;

    @Mock
    private ExtentReporter extentReporter;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private Configuration configuration;

    @Mock
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private Video.ExtentTest videoExtentTest;

    @InjectMocks
    private StatefulExtentTestResolver statefulExtentTestResolver;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("extentReporter", statefulExtentTestResolver, extentReporter);
        Reflections.setField("contextManager", statefulExtentTestResolver, contextManager);

        extentReporterMockedStatic = mockStatic(ExtentReporter.class);
        statefulExtentTestMockedStatic = mockStatic(StatefulExtentTest.class);
    }

    @AfterEach
    void afterEach() {
        extentReporterMockedStatic.close();
        statefulExtentTestMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return the initialized ExtentTest adding the video")
    void testResolveParameter() {
        when(context.getStore(GLOBAL)).thenReturn(store);

        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);
        when(video.getExtentTest()).thenReturn(videoExtentTest);
        when(videoExtentTest.isAttach()).thenReturn(true);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);

        when(ExtentReporter.getInstance()).thenReturn(extentReporter);
        when(extentReporter.createExtentTestFrom(context)).thenReturn(extentTest);

        when(StatefulExtentTest.builder()).thenReturn(statefulExtentTestBuilder);
        when(statefulExtentTestBuilder.currentNode(extentTest)).thenReturn(statefulExtentTestBuilder);
        when(statefulExtentTestBuilder.build()).thenReturn(statefulExtentTest);

        StatefulExtentTest actual = statefulExtentTestResolver.resolveParameter(parameterContext, context);

        verify(extentReporter).logTestStartOf(extentTest);
        verify(store).put(STATEFUL_EXTENT_TEST, actual);
        verify(contextManager).put(context, STATEFUL_EXTENT_TEST, actual);
        assertEquals(statefulExtentTest, actual);
    }

    @DisplayName("resolveParameter should return the initialized ExtentTest without adding the video")
    @ParameterizedTest(name = "with video disabled {0} and video attach {1}")
    @MethodSource("noVideoValuesProvider")
    void testResolveParameterNoVideo(final boolean disabled, final boolean attach) {
        when(context.getStore(GLOBAL)).thenReturn(store);

        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(disabled);
        when(video.getExtentTest()).thenReturn(videoExtentTest);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);

        when(ExtentReporter.getInstance()).thenReturn(extentReporter);
        when(extentReporter.createExtentTestFrom(context)).thenReturn(extentTest);

        when(StatefulExtentTest.builder()).thenReturn(statefulExtentTestBuilder);
        when(statefulExtentTestBuilder.currentNode(extentTest)).thenReturn(statefulExtentTestBuilder);
        when(statefulExtentTestBuilder.build()).thenReturn(statefulExtentTest);

        lenient().when(videoExtentTest.isAttach()).thenReturn(attach);

        StatefulExtentTest actual = statefulExtentTestResolver.resolveParameter(parameterContext, context);

        verifyNoMoreInteractions(extentTest);
        verify(store).put(STATEFUL_EXTENT_TEST, actual);
        verify(contextManager).put(context, STATEFUL_EXTENT_TEST, actual);
        assertEquals(statefulExtentTest, actual);
    }

    public static Stream<Arguments> noVideoValuesProvider() {
        return Stream.of(
                arguments(false, false),
                arguments(true, false),
                arguments(true, true)
        );
    }
}
