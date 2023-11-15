package io.github.giulong.spectrum.utils.video;

import io.github.giulong.spectrum.pojos.Configuration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.BlockingQueue;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class VideoEncoder extends Thread {

    private final BlockingQueue<File> blockingQueue;
    private final String className;
    private final String methodName;
    private final AWTSequenceEncoder encoder;
    private final Configuration.Extent.Video video;
    private final Dimension dimension;

    private boolean stopSignal;

    @SneakyThrows
    public VideoEncoder(final BlockingQueue<File> blockingQueue, final String className, final String methodName,
                        final File videoFile, final Configuration.Extent.Video video, final WebDriver webDriver) {
        this.blockingQueue = blockingQueue;
        this.className = className;
        this.methodName = methodName;
        this.encoder = AWTSequenceEncoder.createSequenceEncoder(videoFile, 1);
        this.video = video;
        this.dimension = chooseDimensionFor(webDriver);
    }

    protected Dimension chooseDimensionFor(final WebDriver webDriver) {
        if (video.getWidth() < 1 || video.getHeight() < 1) {
            final Dimension size = webDriver.manage().window().getSize();
            return new Dimension(makeItEven(size.getWidth()), makeItEven(size.getHeight()));
        }

        return new Dimension(makeItEven(video.getWidth()), makeItEven(video.getHeight()));
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
    }

    protected BufferedImage resize(final BufferedImage bufferedImage) {
        final BufferedImage resizedImage = new BufferedImage(dimension.getWidth(), dimension.getHeight(), TYPE_INT_RGB);

        final Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(bufferedImage, 0, 0, null);
        graphics2D.dispose();

        return resizedImage;
    }

    @SneakyThrows
    public void done() {
        stopSignal = true;
        join();
    }
}
