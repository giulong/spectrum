package io.github.giulong.spectrum.utils.events.html_report;

import static com.aventstack.extentreports.Status.FAIL;
import static io.github.giulong.spectrum.enums.Frame.VISUAL_REGRESSION_MANUAL;
import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.ORIGINAL_DRIVER;
import static org.openqa.selenium.OutputType.BYTES;

import java.nio.file.Path;
import java.time.Duration;

import io.github.giulong.spectrum.exceptions.VisualRegressionException;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

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

    @SneakyThrows
    protected void runChecks() {
        final Configuration.VisualRegression.Checks checks = visualRegression.getChecks();
        final Duration interval = checks.getInterval();
        final int maxRetries = checks.getMaxRetries();
        final int count = checks.getCount();
        final WebDriver driver = store.get(ORIGINAL_DRIVER, WebDriver.class);
        final String time = String.format("%d.%d", interval.toSecondsPart(), interval.toMillisPart());

        nextRetry:
        for (int i = 0, j = 0; i < maxRetries; i++, j = 0) {
            while (j++ < count) {
                log.trace("Sleeping {}s before running the additional screenshot check", time);
                Thread.sleep(interval);

                log.debug("Running additional screenshot check number {} of retry {}", j, i + 1);
                final byte[] screenshotCheck = ((TakesScreenshot) driver).getScreenshotAs(BYTES);

                if (!fileUtils.compare(screenshot, screenshotCheck)) {
                    if (i == maxRetries - 1 && j == count) {
                        final String visualRegressionTag = htmlUtils.buildVisualRegressionTagFor(frameNumber, testData, screenshot, screenshotCheck);

                        addScreenshotToReport(referencePath, FAIL, visualRegressionTag, null);
                        throw new VisualRegressionException(String.format("All visual regression checks failed. Tried %d checks for %s times", count, maxRetries));
                    }

                    log.error("Additional screenshot check failed. Retrying...");
                    screenshot = screenshotCheck;

                    continue nextRetry;
                }
            }

            break;
        }

        log.debug("Additional checks passed");
    }
}
