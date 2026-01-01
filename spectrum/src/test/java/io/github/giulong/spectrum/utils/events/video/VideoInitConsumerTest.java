package io.github.giulong.spectrum.utils.events.video;

import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.util.Map;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class VideoInitConsumerTest {

    private MockedStatic<AWTSequenceEncoder> awtSequenceEncoderMockedStatic;

    @Mock
    private Event event;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private Path path;

    @Mock
    private AWTSequenceEncoder encoder;

    @Mock
    private Map<Path, AWTSequenceEncoder> encoders;

    @Mock
    private TestData testData;

    @InjectMocks
    private VideoInitConsumer videoInitConsumer;

    @BeforeEach
    void beforeEach() {
        awtSequenceEncoderMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        awtSequenceEncoderMockedStatic.close();
    }

    @Test
    @DisplayName("init should init the needed fields")
    void init() {
        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getVideoPath()).thenReturn(path);
        when(testData.getEncoders()).thenReturn(encoders);

        awtSequenceEncoderMockedStatic.when(() -> AWTSequenceEncoder.createSequenceEncoder(path.toFile(), 1)).thenReturn(encoder);

        videoInitConsumer.accept(event);

        verify(encoders).put(path, encoder);
    }
}
