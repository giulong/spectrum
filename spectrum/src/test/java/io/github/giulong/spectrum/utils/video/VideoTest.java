package io.github.giulong.spectrum.utils.video;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.enums.Frame.AUTO_BEFORE;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class VideoTest {

    @InjectMocks
    private Video video;

    @DisplayName("isDisabled should check if video is disabled")
    @ParameterizedTest(name = "with frames {0} we expect {1}")
    @MethodSource("valuesProvider")
    void isDisabled(final List<Frame> frames, final boolean expected) {
        Reflections.setField("frames", video, frames);
        assertEquals(expected, video.isDisabled());
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(List.of(AUTO_BEFORE), false),
                arguments(List.of(), true)
        );
    }

    @DisplayName("shouldRecord should check if the provided frame name should be recorded")
    @ParameterizedTest(name = "with frames {0} we expect {1}")
    @MethodSource("shouldRecordValuesProvider")
    void shouldRecord(final List<Frame> frames, final boolean expected) {
        Reflections.setField("frames", video, frames);
        assertEquals(expected, video.shouldRecord(AUTO_BEFORE));
    }

    static Stream<Arguments> shouldRecordValuesProvider() {
        return Stream.of(
                arguments(List.of(AUTO_BEFORE), true),
                arguments(List.of(MANUAL, AUTO_BEFORE), true),
                arguments(List.of(MANUAL), false),
                arguments(List.of(), false)
        );
    }

    @DisplayName("getFrameNumberFor should return the current frame number and increment it if the provided frame should be recorded, -1 otherwise")
    @ParameterizedTest(name = "with frames {0} we expect {1} and next frame number to be {2}")
    @MethodSource("getFrameNumberForValuesProvider")
    void getFrameNumberFor(final List<Frame> frames, final int expected, final int nextFrameNumber) {
        Reflections.setField("frames", video, frames);
        Reflections.setField("frameNumber", video, 123);

        assertEquals(expected, video.getFrameNumberFor(AUTO_BEFORE));

        assertEquals(nextFrameNumber, Reflections.getFieldValue("frameNumber", video));
    }

    static Stream<Arguments> getFrameNumberForValuesProvider() {
        return Stream.of(
                arguments(List.of(AUTO_BEFORE), 123, 124),
                arguments(List.of(MANUAL, AUTO_BEFORE), 123, 124),
                arguments(List.of(MANUAL), -1, 123),
                arguments(List.of(), -1, 123)
        );
    }

    @Test
    @DisplayName("resetFrameNumber should set the frame number to 0")
    void resetFrameNumber() {
        Reflections.setField("frameNumber", video, 123);

        video.resetFrameNumber();

        assertEquals(0, Reflections.getFieldValue("frameNumber", video));
    }
}
