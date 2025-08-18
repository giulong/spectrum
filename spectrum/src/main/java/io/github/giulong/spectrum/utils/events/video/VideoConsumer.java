package io.github.giulong.spectrum.utils.events.video;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.ORIGINAL_DRIVER;
import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.utils.FileUtils.HASH_ALGORITHM;
import static io.github.giulong.spectrum.utils.web_driver_events.VideoAutoScreenshotProducer.SCREENSHOT;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
@JsonView(Internal.class)
public class VideoConsumer extends VideoBaseConsumer {

    private final MessageDigest messageDigest;

    @SneakyThrows
    public VideoConsumer() {
        this.messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
    }

    @Override
    @SneakyThrows
    public void accept(final Event event) {
        final Video video = configuration.getVideo();
        final Map<String, Object> payload = event.getPayload();
        final ExtensionContext.Store store = ((ExtensionContext) payload.get(EXTENSION_CONTEXT)).getStore(GLOBAL);
        final TestData testData = store.get(TEST_DATA, TestData.class);
        final byte[] screenshot = (byte[]) payload.get(SCREENSHOT);

        if (video.isSkipDuplicateFrames() && !isNewFrame(screenshot, testData)) {
            return;
        }

        final Path videoPath = getVideoPathFrom(testData);
        final AWTSequenceEncoder encoder = testData.getEncoders().get(videoPath);
        final Dimension dimension = chooseDimensionFor(store.get(ORIGINAL_DRIVER, WebDriver.class), video);

        log.debug("Adding screenshot to video {}", videoPath.getFileName());
        encoder.encodeImage(resize(ImageIO.read(new ByteArrayInputStream(screenshot)), dimension));
    }

    @SneakyThrows
    protected boolean isNewFrame(final byte[] screenshot, final TestData testData) {
        final byte[] digest = messageDigest.digest(screenshot);

        if (!Arrays.equals(testData.getLastFrameDigest(), digest)) {
            testData.setLastFrameDigest(digest);
            return true;
        }

        log.trace("Discarding duplicate frame");
        return false;
    }

    Dimension chooseDimensionFor(final WebDriver driver, final Video video) {
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

    int makeItEven(final int i) {
        return i % 2 == 0 ? i : i + 1;
    }

    BufferedImage resize(final BufferedImage bufferedImage, final Dimension dimension) {
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
}
