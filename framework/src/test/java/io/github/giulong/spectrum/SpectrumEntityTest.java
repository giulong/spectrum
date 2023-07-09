package io.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import io.github.giulong.spectrum.pojos.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.aventstack.extentreports.Status.*;
import static io.github.giulong.spectrum.SpectrumEntity.SCREEN_SHOT_FOLDER;
import static java.util.Comparator.reverseOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.OutputType.BYTES;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpectrumEntity")
class SpectrumEntityTest {

    private static final String UUID_REGEX = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\\.png";
    private static final List<Path> REPORTS_FOLDERS = new ArrayList<>();

    @Mock
    private WebDriverWait downloadWait;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Configuration.Extent extent;

    @Mock
    private ExtentTest extentTest;

    @Mock(extraInterfaces = TakesScreenshot.class)
    private WebDriver webDriver;

    @Mock
    private WebElement webElement;

    @Mock
    private Actions actions;

    @Mock
    private By by;

    @Captor
    private ArgumentCaptor<Function<WebDriver, Boolean>> functionArgumentCaptor;

    @InjectMocks
    private DummySpectrumEntity<?> spectrumEntity;

    @AfterAll
    public static void afterAll() {
        REPORTS_FOLDERS.forEach(folder -> {
            try (Stream<Path> files = Files.walk(folder)) {
                //noinspection ResultOfMethodCallIgnored
                files
                        .sorted(reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Path addScreenshotToReportStubs() throws IOException {
        final Path path = Files.createTempDirectory("reportsFolder");
        REPORTS_FOLDERS.add(path);

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(path.toString());
        when(webDriver.findElement(By.tagName("body"))).thenReturn(webElement);
        when(webElement.getScreenshotAs(BYTES)).thenReturn(new byte[]{1, 2, 3});

        return path;
    }

    @Test
    @DisplayName("getSharedFields should return the list of fields of SpectrumEntity.class that are annotated with @Shared")
    public void getSharedFields() {
        final List<Field> actual = spectrumEntity.getSharedFields();
        final List<String> sharedFieldsNames = actual
                .stream()
                .map(Field::getName)
                .toList();

        // we're checking real size and names here, no mocks
        assertEquals(12, actual.size());
        assertTrue(sharedFieldsNames.containsAll(List.of(
                "configuration",
                "extentReports",
                "extentTest",
                "actions",
                "eventsListener",
                "eventsDispatcher",
                "webDriver",
                "implicitWait",
                "pageLoadWait",
                "scriptWait",
                "downloadWait",
                "data"
        )));
    }

    @Test
    @DisplayName("hover should move the pointer over the provided webElement")
    public void hover() {
        when(actions.moveToElement(webElement)).thenReturn(actions);
        assertEquals(spectrumEntity, spectrumEntity.hover(webElement));
        verify(actions).perform();
    }

    @Test
    @DisplayName("screenshot should delegate to addScreenshotToReport")
    public void screenshot() throws IOException {
        addScreenshotToReportStubs();

        assertEquals(spectrumEntity, spectrumEntity.screenshot());

        verify(webElement).getScreenshotAs(BYTES);
        verify(extentTest).log(eq(INFO), (String) eq(null), any());
    }

    @Test
    @DisplayName("infoWithScreenshot should delegate to addScreenshotToReport")
    public void infoWithScreenshot() throws IOException {
        final String msg = "msg";
        addScreenshotToReportStubs();

        assertEquals(spectrumEntity, spectrumEntity.screenshotInfo(msg));

        verify(webElement).getScreenshotAs(BYTES);
        verify(extentTest).log(eq(INFO), eq("<div class=\"screenshot-container\">" + msg + "</div>"), any());
    }

    @Test
    @DisplayName("warningWithScreenshot should delegate to addScreenshotToReport")
    public void warningWithScreenshot() throws IOException {
        final String msg = "msg";
        addScreenshotToReportStubs();

        assertEquals(spectrumEntity, spectrumEntity.screenshotWarning(msg));

        verify(webElement).getScreenshotAs(BYTES);
        verify(extentTest).log(eq(WARNING), eq("<div class=\"screenshot-container\">" + msg + "</div>"), any());
    }

    @Test
    @DisplayName("failWithScreenshot should delegate to addScreenshotToReport")
    public void failWithScreenshot() throws IOException {
        final String msg = "msg";
        addScreenshotToReportStubs();

        assertEquals(spectrumEntity, spectrumEntity.screenshotFail(msg));

        verify(webElement).getScreenshotAs(BYTES);
        verify(extentTest).log(eq(FAIL), eq("<div class=\"screenshot-container\">" + msg + "</div>"), any());
    }

    @Test
    @DisplayName("addScreenshotToReport should add the provided message to the report, at the provided status level and attaching a screenshot")
    public void addScreenshotToReport() throws IOException {
        final Path reportsFolder = addScreenshotToReportStubs();
        final String msg = "msg";
        final Status status = INFO;
        final Media screenShot = spectrumEntity.addScreenshotToReport(msg, status);

        assertNotNull(screenShot);

        final String screenShotName = screenShot.getPath();
        final Path screenshotPath = Path.of(screenShotName);
        final Path fullScreenShotPath = Path.of(reportsFolder.toString(), screenShotName);

        assertTrue(Files.exists(fullScreenShotPath));
        assertEquals(screenshotPath.getParent().toString(), SCREEN_SHOT_FOLDER);
        assertThat(screenshotPath.getFileName().toString(), matchesPattern(UUID_REGEX));
        verify(webElement).getScreenshotAs(BYTES);
        verify(extentTest).log(status, "<div class=\"screenshot-container\">" + msg + "</div>", screenShot);
    }

    @DisplayName("deleteDownloadsFolder should delete and recreate the downloads folder")
    @ParameterizedTest(name = "with value {0}")
    @MethodSource("valuesProvider")
    public void deleteDownloadsFolder(final Path downloadsFolder) {
        downloadsFolder.toFile().deleteOnExit();

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getDownloadsFolder()).thenReturn(downloadsFolder.toString());

        spectrumEntity.deleteDownloadsFolder();

        assertNotNull(downloadsFolder.toFile());
        assertEquals(0, Objects.requireNonNull(downloadsFolder.toFile().list()).length);
    }

    public static Stream<Arguments> valuesProvider() throws IOException {
        return Stream.of(
                arguments(Path.of("abc not existing")),
                arguments(Files.createTempDirectory("downloadsFolder")));
    }

    @Test
    @DisplayName("waitForDownloadOf should return true if the provided file is fully downloaded")
    public void waitForDownloadOf() throws IOException {
        final Path path = Files.createTempFile("fakeFile", ".txt");
        Files.writeString(path, "I'm an airplane!!!");

        assertEquals(spectrumEntity, spectrumEntity.waitForDownloadOf(path));

        verify(downloadWait).until(functionArgumentCaptor.capture());
        final Function<WebDriver, Boolean> function = functionArgumentCaptor.getValue();

        assertTrue(function.apply(webDriver));
    }

    @Test
    @DisplayName("waitForDownloadOf should wait until the provided file is fully downloaded")
    public void waitForDownloadOfNotYetDone() throws IOException {
        final Path path = Files.createTempFile("fakeFile", ".txt");

        spectrumEntity.waitForDownloadOf(path);

        verify(downloadWait).until(functionArgumentCaptor.capture());
        final Function<WebDriver, Boolean> function = functionArgumentCaptor.getValue();

        assertFalse(function.apply(webDriver));
    }

    @Test
    @DisplayName("waitForDownloadOf should wait until the provided file exist")
    public void waitForDownloadOfNotYetCreated() {
        spectrumEntity.waitForDownloadOf(Path.of("not existing"));

        verify(downloadWait).until(functionArgumentCaptor.capture());
        final Function<WebDriver, Boolean> function = functionArgumentCaptor.getValue();

        assertFalse(function.apply(webDriver));
    }

    @Test
    @DisplayName("checkDownloadedFile should ")
    public void checkDownloadedFile() throws IOException {
        final Path downloadsFolder = Files.createTempDirectory("downloadsFolder");
        final Path filesFolder = Files.createTempDirectory("filesFolder");
        final Path downloadedFile = Files.createFile(Path.of(downloadsFolder + "/fakeFile.txt"));
        final Path fileToCheck = Files.createFile(Path.of(filesFolder + "/fakeFile.txt"));
        final Path wrongDownloadedFile = Files.createFile(Path.of(downloadsFolder + "/wrongFakeFile.txt"));
        final Path wrongFileToCheck = Files.createFile(Path.of(filesFolder + "/wrongFakeFile.txt"));
        Files.writeString(downloadedFile, "I'm an airplane!!!");
        Files.writeString(fileToCheck, "I'm an airplane!!!");
        Files.writeString(wrongDownloadedFile, "I'm a teapot...");
        Files.writeString(wrongFileToCheck, "I should have been an airplane...");

        downloadedFile.toFile().deleteOnExit();
        fileToCheck.toFile().deleteOnExit();
        wrongDownloadedFile.toFile().deleteOnExit();
        wrongFileToCheck.toFile().deleteOnExit();
        downloadsFolder.toFile().deleteOnExit();
        filesFolder.toFile().deleteOnExit();

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getDownloadsFolder()).thenReturn(downloadsFolder.toString());
        when(runtime.getFilesFolder()).thenReturn(filesFolder.toString());

        assertTrue(spectrumEntity.checkDownloadedFile(downloadedFile.getFileName().toString()));
        assertFalse(spectrumEntity.checkDownloadedFile(wrongDownloadedFile.getFileName().toString()));
    }

    @Test
    @DisplayName("sha256Of should return the byte array of the sha digest of the provided file")
    public void sha256Of() throws IOException {
        final Path path = Files.createTempFile("fakeFile", ".txt");
        Files.writeString(path, "I'm an airplane!!!");
        path.toFile().deleteOnExit();
        assertArrayEquals(new byte[]{-84, -101, -4, -117, -46, -98, 10, -68, -51, 127, 64, -87, 51, 9, -1, 13, -39, 103,
                -126, 71, -121, -84, -51, 110, 113, -124, 119, -71, -51, 73, -75, 100}, SpectrumEntity.sha256Of(path));
    }

    @DisplayName("clearAndSendKeys should clear the provided webElement and send the provided keys to it")
    @ParameterizedTest(name = "with keys {0}")
    @ValueSource(strings = {"string", "another"})
    public void clearAndSendKeys(final String string) {
        assertEquals(webElement, spectrumEntity.clearAndSendKeys(webElement, string));

        verify(webElement).clear();
        verify(webElement).sendKeys(string);
    }

    @Test
    @DisplayName("upload should send the full path of the provided file to the provided webElement and return the SpectrumEntity instance")
    public void upload() {
        final String filesFolder = "filesFolder";
        final String fileName = "fileName";

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getFilesFolder()).thenReturn(filesFolder);

        assertEquals(spectrumEntity, spectrumEntity.upload(webElement, fileName));

        verify(webElement).sendKeys(Path.of(System.getProperty("user.dir"), filesFolder, fileName).toString());
    }

    @DisplayName("isNotPresent should return true if the element located by the provided By is not in the dom")
    @ParameterizedTest(name = "with list {0} we expect {1}")
    @MethodSource("isNotPresentProvider")
    public void isNotPresent(final List<WebElement> webElements, final boolean expected) {
        when(webDriver.findElements(by)).thenReturn(webElements);

        assertEquals(expected, spectrumEntity.isNotPresent(by));
    }

    public static Stream<Arguments> isNotPresentProvider() {
        return Stream.of(
                arguments(List.of(), true),
                arguments(List.of(mock(WebElement.class)), false)
        );
    }

    private static class DummySpectrumEntity<T> extends SpectrumEntity<DummySpectrumEntity<T>, T> {
    }
}
