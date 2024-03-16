package io.github.giulong.spectrum.utils.video;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class VideoEncoder extends Thread {

    private final ClassLoader classLoader = VideoEncoder.class.getClassLoader();
    private final BlockingQueue<File> blockingQueue;
    private final String className;
    private final String methodName;
    private final AWTSequenceEncoder encoder;
    private final Video video;
    private final Dimension dimension;

    private boolean framesAdded;
    private boolean stopSignal;

    @SneakyThrows
    public VideoEncoder(final BlockingQueue<File> blockingQueue, final String className, final String methodName,
                        final File file, final Video video, final WebDriver driver) {
        this.blockingQueue = blockingQueue;
        this.className = className;
        this.methodName = methodName;
        this.video = video;
        this.dimension = chooseDimensionFor(driver);
        this.encoder = AWTSequenceEncoder.createSequenceEncoder(file, 1);
    }

    protected Dimension chooseDimensionFor(final WebDriver driver) {
        int width = video.getWidth();
        int height = video.getHeight();

        if (video.getWidth() < 1 || video.getHeight() < 1) {
            final Dimension size = driver.manage().window().getSize();
            width = size.getWidth();
            height = size.getHeight() - video.getMenuBarsHeight();
        }

        final int evenWidth = makeItEven(width);
        final int evenHeight = makeItEven(height);

        log.debug("Video dimensions: {}x{}", evenWidth, evenHeight);
        return new Dimension(evenWidth, evenHeight);
    }

    protected int makeItEven(final int i) {
        return i % 2 == 0 ? i : i + 1;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (!stopSignal) {
            processNext();
        }

        log.debug("Still {} screenshots to consume for {}.{}", blockingQueue.size(), className, methodName);
        while (!blockingQueue.isEmpty()) {
            processNext();
        }

        if (!framesAdded) {
            log.debug("No frames were added to the video. Adding 'no-video.png'");
            final URL noVideoPng = Objects.requireNonNull(classLoader.getResource("no-video.png"));
            encoder.encodeImage(ImageIO.read(noVideoPng));
        }

        encoder.finish();
    }

    @SneakyThrows
    public void processNext() {
        final File screenshot = blockingQueue.poll(1, SECONDS);
        if (screenshot == null) {
            log.trace("Polled queue for 1s but no element was available. Returning to avoid thread locking");
            return;
        }

        log.debug("Consuming: {}", screenshot);
        encoder.encodeImage(resize(ImageIO.read(screenshot)));
        framesAdded = true;
    }

    protected BufferedImage resize(final BufferedImage bufferedImage) {
        final int width = dimension.getWidth();
        final int height = dimension.getHeight();
        final int minWidth = Math.min(width, bufferedImage.getWidth());
        final int minHeight = Math.min(height, bufferedImage.getHeight());
        final BufferedImage resizedImage = new BufferedImage(width, height, TYPE_INT_RGB);
        final Graphics2D graphics2D = resizedImage.createGraphics();

        log.trace("Resizing screenshot to {}x{}", minWidth, minHeight);
        graphics2D.drawImage(bufferedImage, 0, 0, minWidth, minHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }

    @SneakyThrows
    public void done() {
        stopSignal = true;
        join();
    }
}
