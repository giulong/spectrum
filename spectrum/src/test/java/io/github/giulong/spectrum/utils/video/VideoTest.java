package io.github.giulong.spectrum.utils.video;

import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.enums.Frame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.enums.Frame.AUTO_BEFORE;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
class VideoTest {

    @InjectMocks
    private Video video;

    @DisplayName("isDisabled should check if video is disabled")
    @ParameterizedTest(name = "with frames {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void isDisabled(final List<Frame> frames, final boolean expected) {
        Reflections.setField("frames", video, frames);
        assertEquals(expected, video.isDisabled());
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(List.of(AUTO_BEFORE), false),
                arguments(List.of(), true)
        );
    }

    @DisplayName("shouldRecord should check if the provided frame name should be recorded")
    @ParameterizedTest(name = "with frames {0} we expect {1}")
    @MethodSource("shouldRecordValuesProvider")
    public void shouldRecord(final List<Frame> frames, final boolean expected) {
        final String frameName = "autoBefore-something";
        Reflections.setField("frames", video, frames);
        assertEquals(expected, video.shouldRecord(frameName));
    }

    public static Stream<Arguments> shouldRecordValuesProvider() {
        return Stream.of(
                arguments(List.of(AUTO_BEFORE), true),
                arguments(List.of(MANUAL, AUTO_BEFORE), true),
                arguments(List.of(MANUAL), false),
                arguments(List.of(), false)
        );
    }
}
