package io.github.giulong.spectrum.utils.events.html_report;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.nio.file.Path;
import java.util.Map;

import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.utils.web_driver_events.VideoAutoScreenshotProducer.SCREENSHOT;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class VisualRegressionConsumerTest {

    private final byte[] screenshot = new byte[]{1, 2, 3};
    private final String tag = "tag";
    private final int frameNumber = 123;

    private MockedStatic<MediaEntityBuilder> mediaEntityBuilderMockedStatic;

    @Mock
    private MediaEntityBuilder mediaEntityBuilder;

    @MockSingleton
    @SuppressWarnings("unused")
    private Configuration configuration;

    @MockSingleton
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @MockSingleton
    @SuppressWarnings("unused")
    private HtmlUtils htmlUtils;

    @MockSingleton
    @SuppressWarnings("unused")
    private ContextManager contextManager;

    @Mock
    private Path referencePath;

    @Mock
    private Path regressionPath;

    @Mock
    private ExtentTest currentNode;

    @Mock
    private Status status;

    @Mock
    private Media media;

    @Mock
    private Event event;

    @Mock
    private Configuration.VisualRegression visualRegressionConfiguration;

    @Mock
    private Configuration.VisualRegression.Snapshots snapshots;

    @Mock
    private Map<String, byte[]> screenshots;

    @Mock
    private Video video;

    @Mock
    private Map<String, Object> payload;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private TestData testData;

    @Mock
    private TestData.VisualRegression visualRegression;

    @InjectMocks
    private DummyVisualRegressionConsumer consumer;

    @BeforeEach
    void beforeEach() {
        mediaEntityBuilderMockedStatic = mockStatic(MediaEntityBuilder.class);
    }

    @AfterEach
    void afterEach() {
        mediaEntityBuilderMockedStatic.close();
    }

    @Test
    @DisplayName("shouldAccept should do nothing if visual regression is disabled")
    void shouldAcceptFalse() {
        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.isEnabled()).thenReturn(false);

        assertFalse(consumer.shouldAccept(event));

        verifyNoInteractions(event);
    }

    @Test
    @DisplayName("shouldAccept should set the reference path when visual regression is enabled")
    void shouldAcceptTrue() {
        final String screenshotName = "screenshotName";

        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(event.getPayload()).thenReturn(payload);
        when(payload.get(EXTENSION_CONTEXT)).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getVisualRegression()).thenReturn(visualRegression);
        when(visualRegression.getPath()).thenReturn(regressionPath);
        when(fileUtils.getScreenshotNameFrom(testData)).thenReturn(screenshotName);
        when(regressionPath.resolve(screenshotName)).thenReturn(referencePath);
        when(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)).thenReturn(statefulExtentTest);
        when(statefulExtentTest.getCurrentNode()).thenReturn(currentNode);
        when(configuration.getVideo()).thenReturn(video);
        when(video.getAndIncrementFrameNumberFor(testData, MANUAL)).thenReturn(frameNumber);
        when(payload.get(SCREENSHOT)).thenReturn(screenshot);

        Reflections.setField("referencePath", consumer, null);
        assertTrue(consumer.shouldAccept(event));
        assertEquals(visualRegressionConfiguration, Reflections.getFieldValue("visualRegression", consumer));
        assertEquals(referencePath, Reflections.getFieldValue("referencePath", consumer));
        assertEquals(regressionPath, Reflections.getFieldValue("regressionPath", consumer));
        assertEquals(testData, Reflections.getFieldValue("testData", consumer));
        assertEquals(currentNode, Reflections.getFieldValue("currentNode", consumer));
        assertEquals(screenshot, Reflections.getFieldValue("screenshot", consumer));
        assertEquals(frameNumber, Reflections.getFieldValue("frameNumber", consumer));
    }

    @DisplayName("shouldOverrideSnapshots should check if snapshots override is configured")
    @ParameterizedTest(name = "with override {0} we expect {0}")
    @ValueSource(booleans = {true, false})
    void shouldOverrideSnapshots(final boolean expected) {
        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.getSnapshots()).thenReturn(snapshots);
        when(snapshots.isOverride()).thenReturn(expected);

        assertEquals(expected, consumer.shouldOverrideSnapshots());
    }

    @Test
    @DisplayName("generateAndAddScreenshotFrom should generate the screenshot and delegate to addScreenshot")
    void generateAndAddScreenshotFrom() {
        final String message = "message";

        Reflections.setField("screenshot", consumer, screenshot);
        Reflections.setField("frameNumber", consumer, frameNumber);

        when(event.getPayload()).thenReturn(payload);
        when(payload.get("message")).thenReturn(message);
        when(payload.get("status")).thenReturn(status);
        when(htmlUtils.buildFrameTagFor(frameNumber, message, testData, "screenshot-message")).thenReturn(tag);

        when(MediaEntityBuilder.createScreenCaptureFromPath(referencePath.toString())).thenReturn(mediaEntityBuilder);
        when(mediaEntityBuilder.build()).thenReturn(media);

        when(contextManager.getScreenshots()).thenReturn(screenshots);

        consumer.generateAndAddScreenshotFrom(event);

        verify(currentNode).log(status, tag, media);
        verify(screenshots).put(referencePath.toString(), screenshot);
        verify(fileUtils).write(referencePath, screenshot);
        verify(testData).incrementScreenshotNumber();
    }

    @Test
    @DisplayName("addScreenshot should add the provided screenshot to the html report and to the screenshot map")
    void addScreenshot() {
        Reflections.setField("screenshot", consumer, screenshot);
        Reflections.setField("frameNumber", consumer, frameNumber);

        when(contextManager.getScreenshots()).thenReturn(screenshots);

        consumer.addScreenshot(regressionPath, status, tag, media);

        verify(currentNode).log(status, tag, media);
        verify(screenshots).put(regressionPath.toString(), screenshot);
        verify(fileUtils).write(regressionPath, screenshot);
    }

    private static final class DummyVisualRegressionConsumer extends VisualRegressionConsumer {

        @Override
        public void accept(final Event event) {
        }
    }
}
