package com.giuliolongfils.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Media;
import com.giuliolongfils.spectrum.internals.EventsListener;
import com.giuliolongfils.spectrum.pojos.Configuration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.ui.WebDriverWait;

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
import static com.aventstack.extentreports.Status.*;
import static java.lang.System.lineSeparator;
import static org.openqa.selenium.OutputType.BYTES;
import static org.openqa.selenium.logging.LogType.BROWSER;

@Slf4j
public abstract class SpectrumEntity<Data> {

    public static final String SCREEN_SHOT_FOLDER = "screenshots";
    public static final String HASH_ALGORITHM = "SHA-256";

    protected static Configuration configuration;
    protected static ExtentReports extentReports;

    protected ExtentTest extentTest;
    protected Actions actions;
    protected EventsListener eventsListener;
    protected WebDriver webDriver;
    protected WebDriverWait implicitWait;
    protected WebDriverWait pageLoadWait;
    protected WebDriverWait scriptWait;
    protected WebDriverWait downloadWait;
    protected Data data;

    public Media infoWithScreenshot(final String msg) {
        return addScreenshotToReport(msg, INFO);
    }

    public Media warningWithScreenshot(final String msg) {
        return addScreenshotToReport(msg, WARNING);
    }

    public Media failWithScreenshot(final String msg) {
        return addScreenshotToReport(msg, FAIL);
    }

    public void hover(final WebElement webElement) {
        actions.moveToElement(webElement).perform();
    }

    @SneakyThrows
    public Media addScreenshotToReport(final String msg, final Status status) {
        try {
            final String fileName = String.format("%s.png", UUID.randomUUID());
            final Path screenshotPath = Paths.get(configuration.getExtent().getReportFolder(), SCREEN_SHOT_FOLDER, fileName).toAbsolutePath();
            final TakesScreenshot takesScreenshot = configuration.getSystemProperties().getBrowser().takesPartialScreenshots()
                    ? webDriver.findElement(By.tagName("body"))
                    : ((TakesScreenshot) webDriver);

            Files.createDirectories(screenshotPath.getParent());
            Files.write(screenshotPath, takesScreenshot.getScreenshotAs(BYTES));

            final Media screenshot = createScreenCaptureFromPath(Paths.get(SCREEN_SHOT_FOLDER, fileName).toString()).build();
            extentTest.log(status, "<div class=\"screenshot-container\">" + msg + "</div>", screenshot);

            return screenshot;
        } catch (WebDriverException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @SneakyThrows
    public void deleteDownloadsFolder() {
        final String downloadFolder = configuration.getApplication().getDownloadsFolder();
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

    public void waitForDownloadOf(final Path path) {
        downloadWait.until(driver -> Files.exists(path) && path.toFile().length() > 0);
    }

    public boolean logBrowserConsoleOutput() {
        if (!configuration.getSystemProperties().getBrowser().exposesConsole()) {
            return false;
        }

        final List<LogEntry> logs = webDriver.manage().logs().get(BROWSER).getAll();

        if (logs.isEmpty()) {
            extentTest.info("<b>Browser console is empty...</b>");
            return false;
        }

        final Markup markup = MarkupHelper.createCodeBlock(
                logs
                        .stream()
                        .map(logEntry -> String.format("%s: %s", logEntry.getLevel(), logEntry.getMessage()))
                        .collect(Collectors.joining(lineSeparator() + lineSeparator())));

        extentTest.info("<b>Browser console:</b><br/>" + markup.getMarkup());
        return true;
    }

    public boolean checkDownloadedFile(final String file) {
        final Configuration.Application application = configuration.getApplication();
        final Path downloadedFile = Paths.get(application.getDownloadsFolder(), file);
        final Path fileToCheck = Paths.get(application.getFilesFolder(), file);

        waitForDownloadOf(downloadedFile);

        return Arrays.equals(sha256Of(downloadedFile), sha256Of(fileToCheck));
    }

    @SneakyThrows
    protected static byte[] sha256Of(final Path file) {
        final byte[] digest = MessageDigest.getInstance(HASH_ALGORITHM).digest(Files.readAllBytes(file));

        log.trace("{} of file '{}' is '{}'", HASH_ALGORITHM, file, Arrays.toString(digest));
        return digest;
    }
}
