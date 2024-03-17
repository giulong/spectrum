package io.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import io.github.giulong.spectrum.interfaces.Shared;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import static com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromPath;
import static com.aventstack.extentreports.Status.*;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static java.util.UUID.randomUUID;
import static org.openqa.selenium.By.tagName;
import static org.openqa.selenium.OutputType.BYTES;

@Slf4j
public abstract class SpectrumEntity<T extends SpectrumEntity<T, Data>, Data> {

    public static final String HASH_ALGORITHM = "SHA-256";

    private final FileUtils fileUtils = FileUtils.getInstance();

    @Shared
    protected Configuration configuration;

    @Shared
    protected ExtentReports extentReports;

    @Shared
    protected ExtentTest extentTest;

    @Shared
    protected Actions actions;

    @Shared
    protected TestData testData;

    @Shared
    protected WebDriver driver;

    @Shared
    protected WebDriverWait implicitWait;

    @Shared
    protected WebDriverWait pageLoadWait;

    @Shared
    protected WebDriverWait scriptWait;

    @Shared
    protected WebDriverWait downloadWait;

    @Shared
    protected EventsDispatcher eventsDispatcher;

    @Shared
    protected Data data;

    protected List<Field> getSharedFields() {
        return Arrays
                .stream(SpectrumEntity.class.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Shared.class))
                .toList();
    }

    @SuppressWarnings("unchecked")
    public T hover(final WebElement webElement) {
        actions.moveToElement(webElement).perform();

        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T screenshot() {
        addScreenshotToReport(null, INFO);

        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T screenshotInfo(final String msg) {
        addScreenshotToReport(msg, INFO);

        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T screenshotWarning(final String msg) {
        addScreenshotToReport(msg, WARNING);

        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T screenshotFail(final String msg) {
        addScreenshotToReport(msg, FAIL);

        return (T) this;
    }

    @SneakyThrows
    public Media addScreenshotToReport(final String msg, final Status status) {
        final Path screenshotPath = testData.getScreenshotFolderPath().resolve(String.format("%s-%s.png", MANUAL.getValue(), randomUUID()));

        try {
            Files.write(screenshotPath, driver.findElement(tagName("body")).getScreenshotAs(BYTES));
        } catch (WebDriverException e) {
            log.debug("Falling back to non-element screenshot due to: {}", e.getMessage());
            Files.write(screenshotPath, ((TakesScreenshot) driver).getScreenshotAs(BYTES));
        }

        final Media screenshot = createScreenCaptureFromPath(screenshotPath.toString()).build();
        extentTest.log(status, msg == null ? null : "<div class=\"screenshot-container\">" + msg + "</div>", screenshot);

        return screenshot;
    }

    @SneakyThrows
    public void deleteDownloadsFolder() {
        fileUtils.deleteContentOf(Path.of(configuration.getRuntime().getDownloadsFolder()));
    }

    @SuppressWarnings("unchecked")
    public T waitForDownloadOf(final Path path) {
        downloadWait.until(webDriver -> {
            log.trace("Checking for download completion of file '{}'", path);
            return Files.exists(path) && path.toFile().length() > 0;
        });

        return (T) this;
    }

    public boolean checkDownloadedFile(final String downloadedFileName, final String fileToCheckName) {
        final Configuration.Runtime runtime = configuration.getRuntime();
        final Path downloadedFile = Path.of(runtime.getDownloadsFolder(), downloadedFileName).toAbsolutePath();
        final Path fileToCheck = Path.of(runtime.getFilesFolder(), fileToCheckName).toAbsolutePath();

        waitForDownloadOf(downloadedFile);

        log.info("""
                Checking if these files are the same:
                {}
                {}
                """, downloadedFile, fileToCheck);
        return Arrays.equals(sha256Of(downloadedFile), sha256Of(fileToCheck));
    }

    public boolean checkDownloadedFile(final String file) {
        return checkDownloadedFile(file, file);
    }

    @SneakyThrows
    protected static byte[] sha256Of(final Path file) {
        final byte[] digest = MessageDigest.getInstance(HASH_ALGORITHM).digest(Files.readAllBytes(file));

        log.trace("{} of file '{}' is '{}'", HASH_ALGORITHM, file, Arrays.toString(digest));
        return digest;
    }

    public WebElement clearAndSendKeys(final WebElement webElement, final CharSequence keysToSend) {
        webElement.clear();
        webElement.sendKeys(keysToSend);

        return webElement;
    }

    @SuppressWarnings("unchecked")
    public T upload(final WebElement webElement, final String fileName) {
        final String fullPath = Path.of(System.getProperty("user.dir"), configuration.getRuntime().getFilesFolder(), fileName).toString();
        log.info("Uploading file '{}'", fullPath);
        webElement.sendKeys(fullPath);

        return (T) this;
    }

    public boolean isPresent(final By by) {
        final int total = driver.findElements(by).size();
        log.debug("Found {} elements with By {}", total, by);
        return total > 0;
    }

    public boolean isNotPresent(final By by) {
        return !isPresent(by);
    }

    public boolean hasClass(final WebElement webElement, final String className) {
        return Arrays.asList(webElement.getAttribute("class").split(" ")).contains(className);
    }

    public boolean hasClasses(final WebElement webElement, final String... classes) {
        return Arrays
                .stream(classes)
                .allMatch(c -> hasClass(webElement, c));
    }
}
