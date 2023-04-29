package com.giuliolongfils.spectrum.util;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityModelProvider;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.SystemProperties;
import com.giuliolongfils.spectrum.pojos.WebDriverWaits;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromPath;
import static java.lang.System.lineSeparator;
import static org.openqa.selenium.OutputType.BYTES;

@Slf4j
@Builder
public class SpectrumUtil {

    public static final String SCREEN_SHOT_FOLDER = "screenshots";
    public static final String HASH_ALGORITHM = "SHA-256";
    private final SystemProperties systemProperties;
    private final Configuration.Application application;
    private final Configuration.Extent extent;

    @SneakyThrows
    public MediaEntityModelProvider addScreenshotToReport(final WebDriver webDriver, final ExtentTest extentTest, final String msg, final Status status) {
        try {
            final String fileName = String.format("%s.png", UUID.randomUUID()).replaceAll("[\\\\/]", "");
            final Path screenShotPath = Paths.get(extent.getReportFolder(), SCREEN_SHOT_FOLDER, fileName).toAbsolutePath();
            final byte[] screenShotBytes = !systemProperties.getBrowser().takesPartialScreenshots()
                    ? ((TakesScreenshot) webDriver).getScreenshotAs(BYTES)
                    : webDriver.findElement(By.tagName("body")).getScreenshotAs(BYTES);

            Files.createDirectories(screenShotPath.getParent());
            Files.write(screenShotPath, screenShotBytes);

            final MediaEntityModelProvider screenShot = createScreenCaptureFromPath(SCREEN_SHOT_FOLDER + "/" + screenShotPath.getFileName().toString()).build();
            extentTest.log(status, "<div class=\"step-text\">" + msg + "</div>", screenShot);

            return screenShot;
        } catch (WebDriverException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @SneakyThrows
    public void deleteDownloadsFolder() {
        final String downloadFolder = application.getDownloadsFolder();
        final Path downloadPath = Paths.get(downloadFolder);

        if (Files.exists(downloadPath)) {
            log.info("About to delete downloads folder '{}'", downloadFolder);

            try (Stream<Path> files = Files.walk(downloadPath)) {
                //noinspection ResultOfMethodCallIgnored
                files.map(Path::toFile).forEach(File::delete);
            }
        }

        Files.createDirectories(downloadPath);
    }

    public void waitForDownloadOf(final WebDriverWaits webDriverWaits, final Path path) {
        webDriverWaits
                .getDownloadWait()
                .until((ExpectedCondition<Boolean>) driver -> Files.exists(path) && path.toFile().length() > 0);
    }

    public boolean logBrowserConsoleOutput(final WebDriver driver, final ExtentTest extentTest) {
        if (!systemProperties.getBrowser().exposesConsole()) {
            return false;
        }

        final List<LogEntry> logs = driver.manage().logs().get(LogType.BROWSER).getAll();

        if (logs.isEmpty()) {
            extentTest.info("<b>BROWSER CONSOLE IS EMPTY</b>");
            return false;
        }

        final Markup m = MarkupHelper.createCodeBlock(logs.stream()
                .map(l -> String.format("%s: %s", l.getLevel(), l.getMessage()))
                .collect(Collectors.joining(lineSeparator() + lineSeparator())));

        extentTest.info("<b>BROWSER CONSOLE:</b><br/>" + m.getMarkup());
        return true;
    }

    public boolean checkDownloadedFile(final WebDriverWaits webDriverWaits, final String file) {
        final Path downloadedFile = Paths.get(application.getDownloadsFolder(), file);
        final Path fileToCheck = Paths.get(application.getFilesFolder(), file);

        waitForDownloadOf(webDriverWaits, downloadedFile);

        return Arrays.equals(sha256Of(downloadedFile), sha256Of(fileToCheck));
    }

    @SneakyThrows
    protected static byte[] sha256Of(final Path file) {
        final byte[] digest = MessageDigest.getInstance(HASH_ALGORITHM).digest(Files.readAllBytes(file));

        log.debug("{} of file '{}' is '{}'", HASH_ALGORITHM, file, Arrays.toString(digest));
        return digest;
    }
}
