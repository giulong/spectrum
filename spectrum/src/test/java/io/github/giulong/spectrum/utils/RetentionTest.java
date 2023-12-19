package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import static org.jcodec.codecs.mjpeg.tools.Asserts.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Retention")
class RetentionTest {

    @Mock
    private File file1;

    @Mock
    private File file2;

    @Mock
    private File file3;

    @InjectMocks
    private Retention retention;

    @Test
    @DisplayName("deleteOldArtifactsFrom should delete files if there are more reports than the total allowed")
    public void deleteOldArtifactsFrom() {
        final int total = 2;
        final List<File> files = List.of(file1, file2, file3);

        ReflectionUtils.setField("total", retention, total);

        assertEquals(2, retention.deleteOldArtifactsFrom(files));
        verify(file1).delete();
        verify(file2).delete();
        verify(file3, never()).delete();
    }

    @Test
    @DisplayName("deleteOldArtifactsFrom should delete no file if there are less reports than the total allowed")
    public void deleteOldArtifactsFromMinimum() {
        final int total = 5;
        final List<File> files = List.of(file1, file2, file3);

        ReflectionUtils.setField("total", retention, total);

        assertEquals(0, retention.deleteOldArtifactsFrom(files));
        verify(file1, never()).delete();
        verify(file2, never()).delete();
        verify(file3, never()).delete();
    }

    @DisplayName("clamp should return the value clamped between the provided minimum and maximum")
    @ParameterizedTest(name = "with value {0}, min {1}, and max {2} we expect {3}")
    @MethodSource("valuesProvider")
    public void clamp(final int value, final int min, final int max, final int expected) {
        assertEquals(expected, retention.clamp(value, min, max));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(3, 0, 10, 3),
                arguments(-3, 0, 10, 0),
                arguments(30, 0, 10, 10),
                arguments(3, -20, -10, -10)
        );
    }
}
