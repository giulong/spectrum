package io.github.giulong.spectrum.utils.video;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import static io.github.giulong.spectrum.SpectrumEntity.HASH_ALGORITHM;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Slf4j
public class ScreenshotWatcher extends Thread {

    private final BlockingQueue<File> blockingQueue;
    private final WatchService watchService;
    private final Path screenshotFolderPath;
    private final Video video;

    private byte[] lastFrameDigest;

    @SneakyThrows
    public ScreenshotWatcher(final BlockingQueue<File> blockingQueue, final Path screenshotFolderPath, final WatchService watchService, final Video video) {
        this.blockingQueue = blockingQueue;
        this.screenshotFolderPath = screenshotFolderPath;
        this.watchService = watchService;
        this.video = video;

        log.debug("Registering watcher for ENTRY_CREATE at {}", screenshotFolderPath);
        screenshotFolderPath.register(watchService, ENTRY_CREATE);
    }

    @SneakyThrows
    @SuppressWarnings("checkstyle:InnerAssignment")
    @Override
    public void run() {
        WatchKey watchKey;

        while ((watchKey = watchService.take()).isValid()) {
            for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                final File screenshot = screenshotFolderPath.resolve(watchEvent.context().toString()).toFile();

                if (video.shouldRecord(screenshot.getName()) && isNewFrame(screenshot)) {
                    blockingQueue.add(screenshot);
                    log.debug("Produced: {}", screenshot);
                }
            }

            watchKey.reset();
        }
    }

    @SneakyThrows
    protected boolean isNewFrame(final File screenshot) {
        final byte[] digest = MessageDigest.getInstance(HASH_ALGORITHM).digest(Files.readAllBytes(screenshot.toPath()));

        if (!Arrays.equals(digest, lastFrameDigest)) {
            lastFrameDigest = digest;
            return true;
        }

        log.trace("Skipping duplicate frame");
        return false;
    }
}
