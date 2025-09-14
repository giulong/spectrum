package io.github.giulong.spectrum.utils.events.html_report;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.nio.file.Path;
import java.util.Map;

import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class ScreenshotConsumerTest {

    private final byte[] screenshot = new byte[]{1, 2, 3};
    private final String tag = "tag";
    private final int frameNumber = 123;

    private MockedStatic<MediaEntityBuilder> mediaEntityBuilderMockedStatic;

    @MockSingleton
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private MediaEntityBuilder mediaEntityBuilder;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private StatefulExtentTest statefulExtentTest;

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
    private Status status;

    @Mock
    private Media media;

    @Mock
    private Path referencePath;

    @Mock
    private Path regressionPath;

    @Mock
    private Map<String, byte[]> screenshots;

    @Mock
    private ExtentTest currentNode;

    @Mock
    private Event event;

    @Mock
    private Map<String, Object> payload;

    @Mock
    private TestData testData;

    @InjectMocks
    private DummyScreenshotConsumer consumer;

    @BeforeEach
    void beforeEach() {
        mediaEntityBuilderMockedStatic = mockStatic(MediaEntityBuilder.class);
    }

    @AfterEach
    void afterEach() {
        mediaEntityBuilderMockedStatic.close();
    }

    @Test
    @DisplayName("shouldAccept should set a bunch of state variables")
    void shouldAccept() {
        when(event.getPayload()).thenReturn(payload);
        when(payload.get(EXTENSION_CONTEXT)).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)).thenReturn(statefulExtentTest);
        when(statefulExtentTest.getCurrentNode()).thenReturn(currentNode);

        assertTrue(consumer.shouldAccept(event));
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

        consumer.generateAndAddScreenshotFrom(event, referencePath);

        verify(currentNode).log(status, tag, media);
        verify(screenshots).put(referencePath.toString(), screenshot);
        verify(fileUtils).write(referencePath, screenshot);
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

    private static final class DummyScreenshotConsumer extends ScreenshotConsumer {

        @Override
        public void accept(Event event) {
        }
    }
}
