package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.ExtentReporter;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtentTestResolverTest {

    private static MockedStatic<ExtentReporter> extentReporterMockedStatic;

    @Mock
    private ExtentReporter extentReporter;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private Configuration configuration;

    @Mock
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private Video.ExtentTest videoExtentTest;

    @InjectMocks
    private ExtentTestResolver extentTestResolver;

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("extentReporter", extentTestResolver, extentReporter);
        extentReporterMockedStatic = mockStatic(ExtentReporter.class);
    }

    @AfterEach
    public void afterEach() {
        extentReporterMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return the initialized ExtentTest adding the video")
    public void testResolveParameter() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);

        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);
        when(video.getExtentTest()).thenReturn(videoExtentTest);
        when(videoExtentTest.isAttach()).thenReturn(true);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);

        when(ExtentReporter.getInstance()).thenReturn(extentReporter);
        when(extentReporter.createExtentTestFrom(testData)).thenReturn(extentTest);

        ExtentTest actual = extentTestResolver.resolveParameter(parameterContext, extensionContext);

        verify(extentReporter).logTestStartOf(extentTest);
        verify(store).put(ExtentTestResolver.EXTENT_TEST, actual);
        assertEquals(extentTest, actual);
    }

    @DisplayName("resolveParameter should return the initialized ExtentTest without adding the video")
    @ParameterizedTest(name = "with video disabled {0} and video attach {1}")
    @MethodSource("noVideoValuesProvider")
    public void testResolveParameterNoVideo(final boolean disabled, final boolean attach) {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);

        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(disabled);
        when(video.getExtentTest()).thenReturn(videoExtentTest);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);

        when(ExtentReporter.getInstance()).thenReturn(extentReporter);
        when(extentReporter.createExtentTestFrom(testData)).thenReturn(extentTest);

        if (!disabled) {    // short-circuit
            when(videoExtentTest.isAttach()).thenReturn(attach);
        }

        ExtentTest actual = extentTestResolver.resolveParameter(parameterContext, extensionContext);

        verifyNoMoreInteractions(extentTest);
        verify(store).put(ExtentTestResolver.EXTENT_TEST, actual);
        assertEquals(extentTest, actual);
    }

    public static Stream<Arguments> noVideoValuesProvider() {
        return Stream.of(
                arguments(false, false),
                arguments(true, false),
                arguments(true, true)
        );
    }
}
