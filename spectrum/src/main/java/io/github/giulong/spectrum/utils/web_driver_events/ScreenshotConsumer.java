package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.TakesScreenshot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import static java.util.UUID.randomUUID;
import static org.openqa.selenium.OutputType.BYTES;

@Slf4j
@Builder
public class ScreenshotConsumer implements Consumer<WebDriverEvent> {

    private TakesScreenshot driver;
    private TestData testData;
    private Video video;

    @SneakyThrows
    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        final Frame frame = webDriverEvent.getFrame();
        final Path screenshotPath = testData.getScreenshotFolderPath().resolve(String.format("%s-%s.png", frame.getValue(), randomUUID()));

        if (video.shouldRecord(screenshotPath.getFileName().toString())) {
            final Path path = Files.write(screenshotPath, driver.getScreenshotAs(BYTES));
            log.trace("Recording frame {} at {}", frame, path);

            return;
        }

        log.trace("Not recording frame {}", frame);
    }
}
