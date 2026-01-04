package io.github.giulong.spectrum.utils.events.video;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import io.github.giulong.spectrum.utils.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class VideoDynamicFinalizerTest {

    @Mock
    private TestData testData;

    @Mock
    private Path path;

    @InjectMocks
    private VideoDynamicFinalizer videoDynamicFinalizer;

    @Test
    @DisplayName("getVideoPathFrom should return the dynamic video path from test data")
    void getVideoPathFrom() {
        when(testData.getDynamicVideoPath()).thenReturn(path);

        assertEquals(path, videoDynamicFinalizer.getVideoPathFrom(testData));
    }
}
