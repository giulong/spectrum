package io.github.giulong.spectrum.utils.events.html_report;

import static com.aventstack.extentreports.Status.INFO;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.util.Map;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.model.Media;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.video.Video;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class ExtentScreenshotConsumerTest {

    private final byte[] screenshot = new byte[]{1, 2, 3};

    private MockedStatic<MediaEntityBuilder> mediaEntityBuilderMockedStatic;

    @Mock
    private MediaEntityBuilder mediaEntityBuilder;

    @Mock
    private Media media;

    @Mock
    private Event event;

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
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private Path path;

    @Mock
    private Map<String, Object> payload;

    @Mock
    private Map<String, byte[]> screenshots;

    @Mock
    private ExtentTest currentNode;

    @InjectMocks
    private ExtentScreenshotConsumer consumer;

    @BeforeEach
    void beforeEach() {
        mediaEntityBuilderMockedStatic = mockStatic(MediaEntityBuilder.class);
    }

    @AfterEach
    void afterEach() {
        mediaEntityBuilderMockedStatic.close();
    }

    @Test
    @DisplayName("accept should ")
    void accept() {
        final int frameNumber = 123;
        final String message = "message";
        final String tag = "tag";

        Reflections.setField("screenshot", consumer, screenshot);

        when(configuration.getVideo()).thenReturn(video);
        when(video.getAndIncrementFrameNumberFor(testData, MANUAL)).thenReturn(frameNumber);
        when(fileUtils.createTempFile("screenshot", ".png")).thenReturn(path);
        when(event.getPayload()).thenReturn(payload);
        when(payload.get("message")).thenReturn(message);
        when(payload.get("status")).thenReturn(INFO);
        when(htmlUtils.buildFrameTagFor(frameNumber, message, testData, "screenshot-message")).thenReturn(tag);
        when(MediaEntityBuilder.createScreenCaptureFromPath(path.toString())).thenReturn(mediaEntityBuilder);
        when(mediaEntityBuilder.build()).thenReturn(media);

        // addScreenshot
        when(contextManager.getScreenshots()).thenReturn(screenshots);

        consumer.accept(event);

        // addScreenshot
        verify(currentNode).log(INFO, tag, media);
        verify(screenshots).put(path.toString(), screenshot);
        verify(fileUtils).write(path, screenshot);
    }
}
