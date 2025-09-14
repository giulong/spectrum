package io.github.giulong.spectrum.utils.events.html_report;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

import static io.github.giulong.spectrum.enums.Frame.VISUAL_REGRESSION_MANUAL;

@Slf4j
public abstract class VisualRegressionConsumer extends ScreenshotConsumer {

    protected Configuration.VisualRegression visualRegression;
    protected Path referencePath;
    protected Path regressionPath;

    @Override
    protected boolean shouldAccept(final Event event) {
        super.shouldAccept(event);

        this.visualRegression = configuration.getVisualRegression();

        if (visualRegression.isEnabled()) {
            this.regressionPath = testData.getVisualRegression().getPath();
            this.referencePath = regressionPath.resolve(fileUtils.getScreenshotNameFrom(testData));
            this.frameNumber = configuration.getVideo().getAndIncrementFrameNumberFor(testData, VISUAL_REGRESSION_MANUAL);

            return true;
        }

        return false;
    }

    protected boolean shouldOverrideSnapshots() {
        return configuration.getVisualRegression().getSnapshots().isOverride();
    }
}
