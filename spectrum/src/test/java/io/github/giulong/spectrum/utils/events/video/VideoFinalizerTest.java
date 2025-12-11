package io.github.giulong.spectrum.utils.events.video;

import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

import javax.imageio.ImageIO;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.*;

class VideoFinalizerTest {

    private MockedStatic<ImageIO> imageIOMockedStatic;

    @Mock
    private Event event;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private TestData testData;

    @Mock
    private BufferedImage bufferedImage;

    @Mock
    private Path path;

    @Mock
    private Map<Path, AWTSequenceEncoder> encoders;

    @Mock
    private AWTSequenceEncoder encoder;

    @Captor
    private ArgumentCaptor<URL> urlArgumentCaptor;

    @InjectMocks
    private VideoFinalizer videoFinalizer;

    @BeforeEach
    void beforeEach() {
        imageIOMockedStatic = mockStatic(ImageIO.class);
    }

    @AfterEach
    void afterEach() {
        imageIOMockedStatic.close();
    }

    @Test
    @DisplayName("accept should finalize the video")
    void accept() throws IOException {
        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getVideoPath()).thenReturn(path);
        when(testData.getEncoders()).thenReturn(encoders);
        when(encoders.get(path)).thenReturn(encoder);
        when(testData.getFrameNumber()).thenReturn(123);

        videoFinalizer.accept(event);

        verify(encoder).finish();
        verifyNoMoreInteractions(encoder);
    }

    @Test
    @DisplayName("accept should finalize the video adding the no video png if there are no frames")
    void acceptNoVideoPng() throws IOException, URISyntaxException {
        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getVideoPath()).thenReturn(path);
        when(testData.getEncoders()).thenReturn(encoders);
        when(encoders.get(path)).thenReturn(encoder);
        when(testData.getFrameNumber()).thenReturn(0);

        imageIOMockedStatic.when(() -> ImageIO.read(urlArgumentCaptor.capture())).thenReturn(bufferedImage);

        videoFinalizer.accept(event);

        assertEquals("no-video.png", Path.of(urlArgumentCaptor.getValue().toURI()).getFileName().toString());

        verify(encoder).encodeImage(bufferedImage);
        verify(encoder).finish();
        verifyNoMoreInteractions(encoder);
    }
}
