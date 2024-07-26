package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.types.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

class VideoDynamicConsumerTest {

    @Mock
    private TestData testData;

    @Mock
    private Path dynamicVideoPath;

    @Mock
    private File file;

    @InjectMocks
    private VideoDynamicConsumer videoDynamicConsumer;

    @Test
    @DisplayName("getVideoPathFrom should return the dynamic video path from the provided testData")
    public void getVideoPathFrom() {
        when(testData.getDynamicVideoPath()).thenReturn(dynamicVideoPath);

        assertEquals(dynamicVideoPath, videoDynamicConsumer.getVideoPathFrom(testData));
    }

    @DisplayName("filter should return true if the provided file contains the displayName of the provided testData")
    @ParameterizedTest(name = "with file name {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void filter(final String fileName, final boolean expected) {
        final String displayName = "displayName";

        when(file.getName()).thenReturn(fileName);
        lenient().when(testData.getDisplayName()).thenReturn(displayName);

        assertEquals(expected, videoDynamicConsumer.filter(file, testData));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("abc-displayName-def12345-1234-1234-1234-123412345678.png", true),
                arguments("nope", false)
        );
    }
}