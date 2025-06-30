package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.TakesScreenshot;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.openqa.selenium.OutputType.BYTES;

@Slf4j
@SuperBuilder
public class ScreenshotConsumer extends WebDriverEventConsumer {

    private final FileUtils fileUtils = FileUtils.getInstance();

    private TakesScreenshot driver;
    private StatefulExtentTest statefulExtentTest;
    private TestData testData;
    private Video video;

    @SneakyThrows
    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        final Frame frame = webDriverEvent.getFrame();

        if (video.shouldRecord(frame)) {
            final String fileName = fileUtils.getScreenshotNameFrom(statefulExtentTest, testData);
            final Path screenshotPath = testData.getScreenshotFolderPath().resolve(fileName);
            final Path path = Files.write(screenshotPath, driver.getScreenshotAs(BYTES));
            log.trace("Recording frame {} for event '{}' at {}", frame, webDriverEvent.getMessage(), path);

            return;
        }

        log.trace("Not recording frame {}", frame);
    }
}
