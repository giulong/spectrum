package io.github.giulong.spectrum.utils.events.video;

import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.annotation.JsonView;

import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.types.TestData;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.jcodec.api.awt.AWTSequenceEncoder;

@Slf4j
@JsonView(Internal.class)
public class VideoFinalizer extends VideoBaseConsumer {

    private final ClassLoader classLoader = VideoFinalizer.class.getClassLoader();

    @Override
    @SneakyThrows
    public void accept(final Event event) {
        final TestData testData = event.getContext().getStore(GLOBAL).get(TEST_DATA, TestData.class);
        final Path videoPath = getVideoPathFrom(testData);
        final AWTSequenceEncoder encoder = testData.getEncoders().get(videoPath);

        log.debug("Finalizing video {}", videoPath.getFileName());

        if (testData.getFrameNumber() == 0) {
            log.debug("No frames were added to the video. Adding 'no-video.png'");
            final URL noVideoPng = Objects.requireNonNull(classLoader.getResource("no-video.png"));
            encoder.encodeImage(ImageIO.read(noVideoPng));
        }

        encoder.finish();
    }
}
