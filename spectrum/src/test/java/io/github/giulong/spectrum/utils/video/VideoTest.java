package io.github.giulong.spectrum.utils.video;

import static io.github.giulong.spectrum.enums.Frame.AUTO_BEFORE;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Stream;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class VideoTest {

    @Mock
    private TestData testData;

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
                arguments(List.of(), true));
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
                arguments(List.of(), false));
    }

    @DisplayName("getAndIncrementFrameNumberFor should return the current frame number and increment it if the provided frame should be recorded, -1 otherwise")
    @ParameterizedTest(name = "with frames {0} we expect {1} and next frame number to be {2}")
    @MethodSource("getAndIncrementFrameNumberForValuesProvider")
    void getAndIncrementFrameNumberFor(final List<Frame> frames, final int expected, final int shouldRecordInvocations) {
        final int currentFrameNumber = 123;

        Reflections.setField("frames", video, frames);

        when(testData.getFrameNumber()).thenReturn(currentFrameNumber);

        assertEquals(expected, video.getAndIncrementFrameNumberFor(testData, AUTO_BEFORE));

        verify(testData, times(shouldRecordInvocations)).incrementFrameNumber();
    }

    static Stream<Arguments> getAndIncrementFrameNumberForValuesProvider() {
        return Stream.of(
                arguments(List.of(AUTO_BEFORE), 123, 1),
                arguments(List.of(MANUAL, AUTO_BEFORE), 123, 1),
                arguments(List.of(MANUAL), -1, 0),
                arguments(List.of(), -1, 0));
    }
}
