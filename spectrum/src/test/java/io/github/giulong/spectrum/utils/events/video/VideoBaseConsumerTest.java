package io.github.giulong.spectrum.utils.events.video;

import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.nio.file.Path;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.enums.Result.DISABLED;
import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

class VideoBaseConsumerTest {

    @Mock
    private Configuration configuration;

    @Mock
    private Video video;

    @Mock
    private Event event;

    @Mock
    private TestData testData;

    @Mock
    private Path videoPath;

    @InjectMocks
    private DummyVideoBaseConsumer videoBaseConsumer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", videoBaseConsumer, configuration);
    }

    @DisplayName("shouldAccept should check if the test and the video are disabled")
    @ParameterizedTest(name = "with result {0} we expect {1}")
    @MethodSource("shouldAcceptValuesProvider")
    void shouldAccept(final Result result, final boolean videoDisabled, final boolean expected) {
        when(event.getResult()).thenReturn(result);
        lenient().when(configuration.getVideo()).thenReturn(video);
        lenient().when(video.isDisabled()).thenReturn(videoDisabled);

        assertEquals(expected, videoBaseConsumer.shouldAccept(event));
    }

    static Stream<Arguments> shouldAcceptValuesProvider() {
        return Stream.of(
                arguments(DISABLED, true, false),
                arguments(SUCCESSFUL, true, false),
                arguments(DISABLED, false, false),
                arguments(SUCCESSFUL, false, true)
        );
    }

    @Test
    @DisplayName("getVideoPathFrom should return the video path from the provided testData")
    void getVideoPathFrom() {
        when(testData.getVideoPath()).thenReturn(videoPath);

        assertEquals(videoPath, videoBaseConsumer.getVideoPathFrom(testData));
    }

    private static final class DummyVideoBaseConsumer extends VideoBaseConsumer {

        @Override
        public void accept(Event event) {
        }
    }
}
