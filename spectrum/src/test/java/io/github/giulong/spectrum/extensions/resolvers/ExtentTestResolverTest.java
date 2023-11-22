package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import io.github.giulong.spectrum.SpectrumSessionListener;
import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.types.TestData;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static com.aventstack.extentreports.Status.*;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtentTestResolver")
class ExtentTestResolverTest {

    private MockedStatic<SpectrumSessionListener> spectrumSessionListenerMockedStatic;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext parentContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ExtentReports extentReports;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private Configuration configuration;

    @Mock
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private Path videoPath;

    @Mock
    private Video.ExtentTest videoExtentTest;

    @InjectMocks
    private ExtentTestResolver extentTestResolver;

    private final String className = "className";
    private final String displayName = "displayName";
    private final String transformedTestName = "classname-displayname";

    @BeforeEach
    public void beforeEach() {
        spectrumSessionListenerMockedStatic = mockStatic(SpectrumSessionListener.class);
    }

    @AfterEach
    public void afterEach() {
        spectrumSessionListenerMockedStatic.close();
    }

    @Test
    @DisplayName("createExtentTestFrom should get ExtentReports from the provided context and create a test")
    public void createExtentTestFrom() {
        when(SpectrumSessionListener.getExtentReports()).thenReturn(extentReports);
        when(extensionContext.getDisplayName()).thenReturn(displayName);
        when(extensionContext.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);
        when(extentReports.createTest(String.format("<div id=\"%s\">%s</div>%s", transformedTestName, className, displayName))).thenReturn(extentTest);

        assertEquals(extentTest, ExtentTestResolver.createExtentTestFrom(extensionContext));
    }

    @Test
    @DisplayName("buildTestIdFrom should return the class and test names, taken from the provided context, combined in kebab case")
    public void buildTestIdFrom() {
        when(extensionContext.getDisplayName()).thenReturn(displayName);
        when(extensionContext.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);

        assertEquals(transformedTestName, ExtentTestResolver.buildTestIdFrom(extensionContext));
    }

    @Test
    @DisplayName("transformInKebabCase should return the provided string with spaces replaced by dashes and in lowercase")
    public void transformInKebabCase() {
        assertEquals("some-composite-string", ExtentTestResolver.transformInKebabCase("Some Composite STRING"));
    }

    @DisplayName("getColorOf should return the color corresponding to the provided status")
    @ParameterizedTest()
    @MethodSource("valuesProvider")
    public void getColorOf(final Status status, final ExtentColor expected) {
        assertEquals(expected, ExtentTestResolver.getColorOf(status));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(FAIL, RED),
                arguments(SKIP, AMBER),
                arguments(INFO, GREEN)
        );
    }

    @Test
    @DisplayName("resolveParameter should return the initialized ExtentTest adding the video")
    public void testResolveParameter() {
        final int width = 123;
        final int height = 456;

        // createExtentTestFrom
        when(SpectrumSessionListener.getExtentReports()).thenReturn(extentReports);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(extensionContext.getDisplayName()).thenReturn(displayName);
        when(extensionContext.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);
        when(extentReports.createTest(String.format("<div id=\"%s\">%s</div>%s", transformedTestName, className, displayName))).thenReturn(extentTest);

        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);
        when(video.getExtentTest()).thenReturn(videoExtentTest);
        when(videoExtentTest.isAttach()).thenReturn(true);
        when(videoExtentTest.getWidth()).thenReturn(width);
        when(videoExtentTest.getHeight()).thenReturn(height);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getVideoPath()).thenReturn(videoPath);

        ExtentTest actual = extentTestResolver.resolveParameter(parameterContext, extensionContext);

        verify(extentTest).info(String.format("<video id=\"video-%s\" controls width=\"%d\" height=\"%d\" src=\"%s\" type=\"video/mp4\"/>", transformedTestName, width, height, videoPath));

        ArgumentCaptor<Markup> markupArgumentCaptor = ArgumentCaptor.forClass(Markup.class);
        verify(extentTest).info(markupArgumentCaptor.capture());
        Markup markup = markupArgumentCaptor.getValue();
        assertEquals(createLabel("START TEST", GREEN).getMarkup(), markup.getMarkup());

        verify(store).put(ExtentTestResolver.EXTENT_TEST, actual);
        assertEquals(extentTest, actual);
    }

    @DisplayName("resolveParameter should return the initialized ExtentTest without adding the video")
    @ParameterizedTest(name = "with video disabled {0} and video attach {1}")
    @MethodSource("noVideoValuesProvider")
    public void testResolveParameterNoVideo(final boolean disabled, final boolean attach) {
        // createExtentTestFrom
        when(SpectrumSessionListener.getExtentReports()).thenReturn(extentReports);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(extensionContext.getDisplayName()).thenReturn(displayName);
        when(extensionContext.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);
        when(extentReports.createTest(String.format("<div id=\"%s\">%s</div>%s", transformedTestName, className, displayName))).thenReturn(extentTest);

        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(disabled);
        when(video.getExtentTest()).thenReturn(videoExtentTest);

        if (!disabled) {    // short-circuit
            when(videoExtentTest.isAttach()).thenReturn(attach);
        }

        ExtentTest actual = extentTestResolver.resolveParameter(parameterContext, extensionContext);

        ArgumentCaptor<Markup> markupArgumentCaptor = ArgumentCaptor.forClass(Markup.class);
        verify(extentTest).info(markupArgumentCaptor.capture());
        Markup markup = markupArgumentCaptor.getValue();
        assertEquals(createLabel("START TEST", GREEN).getMarkup(), markup.getMarkup());
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
