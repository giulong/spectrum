package io.github.giulong.spectrum.utils.events.video;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.jcodec.api.awt.AWTSequenceEncoder.createSequenceEncoder;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
@JsonView(Internal.class)
public class VideoInitConsumer extends VideoBaseConsumer {

    @Override
    @SneakyThrows
    public void accept(final Event event) {
        final TestData testData = event.getContext().getStore(GLOBAL).get(TEST_DATA, TestData.class);
        final Path videoPath = getVideoPathFrom(testData);

        log.info("Generating video for test {}", videoPath.getFileName());

        testData.getEncoders().put(videoPath, createSequenceEncoder(videoPath.toFile(), 1));
    }
}
