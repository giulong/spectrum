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
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
@DisplayName("FixedSizeQueue")
class FixedSizeQueueTest {

    @Mock
    private File file1;

    @Mock
    private File file2;

    @InjectMocks
    private FixedSizeQueue<File> fixedSizeQueue;

    @Test
    @DisplayName("add should add the element to the list only if it's new")
    public void add() {
        assertTrue(fixedSizeQueue.add(file1));
        assertTrue(fixedSizeQueue.add(file2));
        assertFalse(fixedSizeQueue.add(file1));
        assertEquals(2, fixedSizeQueue.size());
    }

    @DisplayName("shrinkTo should fluently poll elements while the queue is bigger than the provided size")
    @ParameterizedTest(name = "with currentSize {0} and maxSize {1} we expect {2}")
    @MethodSource("valuesProvider")
    public void shrinkTo(final int currentSize, final int maxSize, final int expected) throws IOException {
        for (int i = 0; i < currentSize; i++) {
            final File tempFile = Files.createTempFile("prefix", ".txt").toFile();
            tempFile.deleteOnExit();
            fixedSizeQueue.add(tempFile);
        }

        assertEquals(currentSize, fixedSizeQueue.size());
        assertEquals(fixedSizeQueue, fixedSizeQueue.shrinkTo(maxSize));
        assertEquals(expected, fixedSizeQueue.size());
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(3, 2, 2),
                arguments(1, 1, 1),
                arguments(1, 2, 1)
        );
    }
}
