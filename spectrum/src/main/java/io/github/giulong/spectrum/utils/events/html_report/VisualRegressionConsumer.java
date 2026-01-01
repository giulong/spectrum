package io.github.giulong.spectrum.utils.events.html_report;

import static org.openqa.selenium.OutputType.BYTES;

import java.nio.file.Path;
import java.time.Duration;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.exceptions.VisualRegressionException;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.TakesScreenshot;

@Slf4j
public abstract class VisualRegressionConsumer extends ScreenshotConsumer {

    protected Configuration.VisualRegression visualRegression;
    protected Path referencePath;
    protected Path regressionPath;

    @Override
    protected boolean shouldAccept(final Event event) {
        super.shouldAccept(event);

        this.visualRegression = configuration.getVisualRegression();

        if (visualRegression.isEnabled() && visualRegression.shouldCheck(Frame.from(event.getPrimaryId()))) {
            this.regressionPath = testData.isDynamic() ? testData.getVisualRegression().getDynamicPath() : testData.getVisualRegression().getPath();
            this.referencePath = regressionPath.resolve(fileUtils.getScreenshotNameFrom(testData));

            return true;
        }

        return false;
    }

    protected boolean shouldOverrideSnapshots() {
        return visualRegression.getSnapshots().isOverride();
    }

    @SneakyThrows
    protected void runChecksOn(final Event event) {
        final TakesScreenshot takesScreenshot = event.getPayload().getTakesScreenshot();
        final Configuration.VisualRegression.Checks checks = visualRegression.getChecks();
        final Duration interval = checks.getInterval();
        final int maxRetries = checks.getMaxRetries();
        final int count = checks.getCount();
        final String time = String.format("%d.%d", interval.toSecondsPart(), interval.toMillisPart());

        nextRetry:
        for (int i = 0, j = 0; i < maxRetries; i++, j = 0) {
            while (j++ < count) {
                log.trace("Sleeping {}s before running the additional screenshot check", time);
                Thread.sleep(interval);

                log.debug("Running additional screenshot check number {} of retry {}", j, i + 1);
                final byte[] screenshotCheck = takesScreenshot.getScreenshotAs(BYTES);

                if (!fileUtils.compare(screenshot, screenshotCheck)) {
                    if (i == maxRetries - 1 && j == count) {
                        final String visualRegressionTag = htmlUtils.buildVisualRegressionTagFor(testData.getFrameNumber(), testData, screenshot, screenshotCheck);

                        currentNode.fail(visualRegressionTag);
                        addScreenshot(referencePath);
                        throw new VisualRegressionException(String.format("Unable to get a stable screenshot. Tried %d checks for %s times", count, maxRetries));
                    }

                    log.warn("Additional screenshot check number {} of retry {} failed. Retrying...", j, i + 1);
                    screenshot = screenshotCheck;

                    continue nextRetry;
                }
            }

            break;
        }

        log.debug("Additional checks passed");
    }
}
