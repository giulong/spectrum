package io.github.giulong.spectrum.utils.events.video;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Map;

import static io.github.giulong.spectrum.enums.Result.DISABLED;
import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class VideoDynamicConsumerTest {

    @MockSingleton
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private Path dynamicVideoPath;

    @Mock
    private MessageDigest messageDigest;

    @Mock
    private Event event;

    @Mock
    private Map<String, Object> payload;

    @Captor
    private ArgumentCaptor<byte[]> byteArrayArgumentCaptor;

    @InjectMocks
    private VideoDynamicConsumer videoDynamicConsumer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("messageDigest", videoDynamicConsumer, messageDigest);
    }

    @Test
    @DisplayName("shouldAccept should return true only if the test is dynamic")
    void shouldAccept() {
        // super.shouldAccept
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);

        when(event.getPayload()).thenReturn(payload);
        when(payload.get(EXTENSION_CONTEXT)).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);

        when(testData.isDynamic()).thenReturn(true);

        assertTrue(videoDynamicConsumer.shouldAccept(event));
    }

    @Test
    @DisplayName("shouldAccept should return false when the super condition doesn't match")
    void shouldAcceptFalseSuperCondition() {
        // super.shouldAccept
        when(event.getResult()).thenReturn(DISABLED);

        assertFalse(videoDynamicConsumer.shouldAccept(event));
    }

    @Test
    @DisplayName("shouldAccept should return false when the context is null")
    void shouldAcceptFalseContextNull() {
        // super.shouldAccept
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);

        when(event.getPayload()).thenReturn(payload);
        when(payload.get(EXTENSION_CONTEXT)).thenReturn(null);

        assertFalse(videoDynamicConsumer.shouldAccept(event));
    }

    @Test
    @DisplayName("shouldAccept should return false when test data is null")
    void shouldAcceptFalseTestDataNull() {
        // super.shouldAccept
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);

        when(event.getPayload()).thenReturn(payload);
        when(payload.get(EXTENSION_CONTEXT)).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(null);

        assertFalse(videoDynamicConsumer.shouldAccept(event));
    }

    @Test
    @DisplayName("shouldAccept should return false when test data is not null but dynamic is false")
    void shouldAcceptFalseTestDataNotDynamic() {
        // super.shouldAccept
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);

        when(event.getPayload()).thenReturn(payload);
        when(payload.get(EXTENSION_CONTEXT)).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.isDynamic()).thenReturn(false);

        assertFalse(videoDynamicConsumer.shouldAccept(event));
    }

    @Test
    @DisplayName("getVideoPathFrom should return the dynamic video path from the provided testData")
    void getVideoPathFrom() {
        when(testData.getDynamicVideoPath()).thenReturn(dynamicVideoPath);

        assertEquals(dynamicVideoPath, videoDynamicConsumer.getVideoPathFrom(testData));
    }

    @Test
    @DisplayName("isNewFrame should return true if the display name of the provided testData is new")
    void isNewFrame() {
        final String displayName = "displayName";
        final byte[] screenshotBytes = new byte[]{1, 2, 3};

        when(testData.getDisplayName()).thenReturn(displayName);

        assertTrue(videoDynamicConsumer.isNewFrame(screenshotBytes, testData));
    }

    @Test
    @DisplayName("isNewFrame should delegate to the parent's implementation when the displayName of the provided testData is not new")
    void isNewFrameOld() {
        final String displayName = "displayName";

        when(testData.getDisplayName()).thenReturn(displayName);
        when(testData.getLastFrameDisplayName()).thenReturn(displayName);

        // super.isNewFrame
        final byte[] lastFrameDigest = new byte[]{1, 2, 3};
        final byte[] screenshotBytes = new byte[]{4, 5, 6};
        final byte[] newFrameDigest = new byte[]{7, 8, 9};
        when(testData.getLastFrameDigest()).thenReturn(lastFrameDigest);
        when(messageDigest.digest(byteArrayArgumentCaptor.capture())).thenReturn(newFrameDigest);

        assertTrue(videoDynamicConsumer.isNewFrame(screenshotBytes, testData));

        verify(testData, never()).setLastFrameDisplayName(anyString());

        // super.isNewFrame
        assertArrayEquals(screenshotBytes, byteArrayArgumentCaptor.getValue());
        verify(testData).setLastFrameDigest(newFrameDigest);
    }
}
