package io.github.giulong.spectrum.utils.events.html_report;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.aventstack.extentreports.Status.FAIL;

@Slf4j
@JsonView(Internal.class)
public class VisualRegressionCheckConsumer extends VisualRegressionConsumer {

    @Override
    protected boolean shouldAccept(final Event event) {
        return super.shouldAccept(event) && Files.exists(referencePath) && !shouldOverrideSnapshots();
    }

    @Override
    @SneakyThrows
    public void accept(final Event event) {
        if (fileUtils.compare(referencePath, screenshot)) {
            log.debug("Screenshot matches with its reference {}", referencePath);

            generateAndAddScreenshotFrom(event, referencePath);
            return;
        }

        log.error("Visual regression: screenshot not matching with its reference {}", referencePath);

        testData.registerFailedVisualRegression();
        final Path failedScreenshotPath = regressionPath.resolve(fileUtils.getFailedScreenshotNameFrom(testData));
        final String visualRegressionTag = htmlUtils.buildVisualRegressionTagFor(frameNumber, testData, Files.readAllBytes(referencePath), screenshot);

        addScreenshot(failedScreenshotPath, FAIL, visualRegressionTag, null);

        if (visualRegression.isFailFast()) {
            log.error("Failing fast due to first visual regression found!");
            throw testData.getTestFailedException().get();
        }
    }
}
