package io.github.giulong.spectrum.utils.events.html_report;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;

@Slf4j
@JsonView(Internal.class)
public class VisualRegressionReferenceCreatorConsumer extends VisualRegressionConsumer {

    @Override
    protected boolean shouldAccept(final Event event) {
        return super.shouldAccept(event) && (Files.notExists(referencePath) || shouldOverrideSnapshots());
    }

    @Override
    public void accept(final Event event) {
        log.info("Generating visual regression reference {}", referencePath);
        generateAndAddScreenshotFrom(event);
    }
}
