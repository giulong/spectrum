package io.github.giulong.spectrum.utils.video;

import lombok.SneakyThrows;
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
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScreenshotWatcher")
class ScreenshotWatcherTest {

    @Mock
    private LinkedBlockingQueue<File> blockingQueue;

    @Mock
    private Path screenshotFolderPath;

    @Mock
    private WatchService watchService;

    @Mock
    private WatchKey watchKey;

    @Mock
    private WatchEvent<String> watchEvent1;

    @Mock
    private WatchEvent<String> watchEvent2;

    @Mock
    private File screenshot;

    @InjectMocks
    private ScreenshotWatcher screenshotWatcher;

    @SneakyThrows
    private Path isNewFrameStubs() {
        final Path path = Files.createTempFile("fakeFile", ".txt");
        Files.writeString(path, "I'm an airplane!!!");
        path.toFile().deleteOnExit();

        return path;
    }

    @Test
    @DisplayName("construction should create a new watchService and register it on the screenshots folder")
    public void construction() throws IOException {
        verify(screenshotFolderPath).register(watchService, ENTRY_CREATE);
    }

    @Test
    @DisplayName("run should add a new element to the blocking queue for every new screenshot")
    public void run() throws InterruptedException, IOException {
        final Path path1 = isNewFrameStubs();
        final Path path2 = Files.createTempFile("another", ".txt");
        final String context1 = "file1.jpg";
        final String context2 = "file2.jpg";
        Files.writeString(path2, "I'm an airplane!!!"); // same content
        path2.toFile().deleteOnExit();

        when(watchService.take()).thenReturn(watchKey);
        when(watchKey.isValid()).thenReturn(true).thenReturn(false);
        when(watchKey.pollEvents()).thenReturn(List.of(watchEvent1, watchEvent2));
        when(watchEvent1.context()).thenReturn(context1);
        when(watchEvent2.context()).thenReturn(context2);
        when(screenshotFolderPath.resolve(context1)).thenReturn(path1);
        when(screenshotFolderPath.resolve(context2)).thenReturn(path2);

        //noinspection CallToThreadRun
        screenshotWatcher.run();

        verify(blockingQueue).add(path1.toFile());
        verify(blockingQueue, never()).add(path2.toFile());
    }

    @DisplayName("isNewFrame should return if the provided screenshot is new")
    @ParameterizedTest(name = "with digest equals to last one {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void isNewFrame(final byte[] lastFrameDigest, final boolean expected) throws NoSuchFieldException, IllegalAccessException {
        final Path path = isNewFrameStubs();
        when(screenshot.toPath()).thenReturn(path);

        final Field lastFrameDigestField = ScreenshotWatcher.class.getDeclaredField("lastFrameDigest");
        lastFrameDigestField.setAccessible(true);
        lastFrameDigestField.set(screenshotWatcher, lastFrameDigest);

        assertEquals(expected, screenshotWatcher.isNewFrame(screenshot));
        assertArrayEquals(new byte[]{-84, -101, -4, -117, -46, -98, 10, -68, -51, 127, 64, -87, 51, 9, -1, 13, -39, 103, -126, 71, -121, -84, -51, 110, 113, -124, 119, -71, -51, 73, -75, 100}, (byte[]) lastFrameDigestField.get(screenshotWatcher));
    }

    public static Stream<Arguments> valuesProvider() throws IOException {
        return Stream.of(
                arguments(null, true),
                arguments(new byte[]{-84, -101, -4, -117, -46, -98, 10, -68, -51, 127, 64, -87, 51, 9, -1, 13, -39, 103, -126, 71, -121, -84, -51, 110, 113, -124, 119, -71, -51, 73, -75, 100}, false)
        );
    }
}