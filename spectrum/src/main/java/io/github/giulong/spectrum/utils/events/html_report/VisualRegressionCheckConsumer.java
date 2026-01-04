package io.github.giulong.spectrum.utils.events.html_report;

import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonView;

import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.visual_regression.ImageDiff.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonView(Internal.class)
public class VisualRegressionCheckConsumer extends VisualRegressionConsumer {

    @Override
    protected boolean shouldAccept(final Event event) {
        return super.shouldAccept(event) && Files.exists(referencePath) && !shouldOverrideSnapshots();
    }

    @Override
    public void accept(final Event event) {
        runChecksOn(event);

        if (fileUtils.compare(referencePath, screenshot)) {
            log.debug("Screenshot matches with its reference {}", referencePath);

            addScreenshot(referencePath);
            return;
        }

        final Path failedScreenshotPath = regressionPath.resolve(fileUtils.getFailedScreenshotNameFrom(testData));
        addScreenshot(failedScreenshotPath);

        final Result result = visualRegression.getDiff().buildBetween(referencePath, failedScreenshotPath, regressionPath, fileUtils.getScreenshotsDiffNameFrom(testData));

        if (!result.isRegressionConfirmed()) {
            log.debug("ImageDiff doesn't confirm the regression for {}. Returning.", referencePath);
            return;
        }

        final String visualRegressionTag = htmlUtils.buildVisualRegressionTagFor(testData.getFrameNumber(), testData, fileUtils.readBytesOf(referencePath), screenshot, result);

        log.error("Visual regression: screenshot not matching with its reference {}", referencePath);
        testData.registerFailedVisualRegression();
        currentNode.fail(visualRegressionTag);

        if (visualRegression.isFailFast()) {
            log.error("Failing fast due to first visual regression found!");
            throw testData.getTestFailedException();
        }
    }
}
