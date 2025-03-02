package io.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import io.github.giulong.spectrum.interfaces.Shared;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.HtmlUtils;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static java.util.Comparator.reverseOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.OutputType.BYTES;

class SpectrumEntityTest {

    private static final String DISPLAY_NAME = "displayName";
    private static final String UUID_REGEX = MANUAL.getValue() + "-" + DISPLAY_NAME + "-([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\\.png";
    private static final List<Path> REPORTS_FOLDERS = new ArrayList<>();

    private final String msg = "msg";
    private final String tag = "tag";
    private final int frameNumber = 123;

    @Mock
    private WebDriverWait downloadWait;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock(extraInterfaces = TakesScreenshot.class)
    private WebDriver webDriver;

    @Mock
    private WebElement webElement;

    @Mock
    private Actions actions;

    @Mock
    private By by;

    @Mock
    private Video video;

    @Mock
    private HtmlUtils htmlUtils;

    @Mock
    private TestData testData;

    @Captor
    private ArgumentCaptor<Function<WebDriver, Boolean>> functionArgumentCaptor;

    @InjectMocks
    private DummySpectrumEntity<?> spectrumEntity;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", spectrumEntity, configuration);
        Reflections.setField("htmlUtils", spectrumEntity, htmlUtils);
    }

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

    @SneakyThrows
    private void addScreenshotToReportStubs() {
        final Path path = Files.createTempDirectory("reportsFolder");
        REPORTS_FOLDERS.add(path);

        when(testData.getScreenshotFolderPath()).thenReturn(path);
        when(statefulExtentTest.getDisplayName()).thenReturn(DISPLAY_NAME);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);
        when(((TakesScreenshot) webDriver).getScreenshotAs(BYTES)).thenReturn(new byte[]{1, 2, 3});
    }

    @Test
    @DisplayName("checking shared fields")
    void getSharedFields() {
        final List<Field> actual = Reflections.getAnnotatedFields(SpectrumEntity.class, Shared.class);
        final List<String> sharedFieldsNames = actual
                .stream()
                .map(Field::getName)
                .toList();

        // we're checking real size and names here, no mocks
        assertEquals(17, actual.size());
        assertTrue(sharedFieldsNames.containsAll(List.of(
                "configuration",
                "extentReports",
                "extentTest",
                "actions",
                "eventsDispatcher",
                "driver",
                "implicitWait",
                "pageLoadWait",
                "scriptWait",
                "downloadWait",
                "js",
                "faker",
                "data",
                "statefulExtentTest",
                "testContext",
                "jsWebElementProxyBuilder"
        )));
    }

    @Test
    @DisplayName("hover should move the pointer over the provided webElement")
    void hover() {
        when(actions.moveToElement(webElement)).thenReturn(actions);
        assertEquals(spectrumEntity, spectrumEntity.hover(webElement));
        verify(actions).perform();
    }

    @Test
    @DisplayName("screenshot should delegate to addScreenshotToReport")
    void screenshot() {
        addScreenshotToReportStubs();

        assertEquals(spectrumEntity, spectrumEntity.screenshot());

        verify(extentTest).log(eq(INFO), (String) eq(null), any());
    }

    @Test
    @DisplayName("infoWithScreenshot should delegate to addScreenshotToReport")
    void infoWithScreenshot() {
        addScreenshotToReportStubs();

        when(configuration.getVideo()).thenReturn(video);
        when(video.getFrameNumberFor(MANUAL)).thenReturn(frameNumber);
        when(htmlUtils.buildFrameTagFor(frameNumber, msg, "screenshot-message")).thenReturn(tag);

        assertEquals(spectrumEntity, spectrumEntity.screenshotInfo(msg));

        verify(extentTest).log(eq(INFO), eq(tag), any());
    }

    @Test
    @DisplayName("warningWithScreenshot should delegate to addScreenshotToReport")
    void warningWithScreenshot() {
        addScreenshotToReportStubs();

        when(configuration.getVideo()).thenReturn(video);
        when(video.getFrameNumberFor(MANUAL)).thenReturn(frameNumber);
        when(htmlUtils.buildFrameTagFor(frameNumber, msg, "screenshot-message")).thenReturn(tag);

        assertEquals(spectrumEntity, spectrumEntity.screenshotWarning(msg));

        verify(extentTest).log(eq(WARNING), eq(tag), any());
    }

    @Test
    @DisplayName("failWithScreenshot should delegate to addScreenshotToReport")
    void failWithScreenshot() {
        addScreenshotToReportStubs();

        when(configuration.getVideo()).thenReturn(video);
        when(video.getFrameNumberFor(MANUAL)).thenReturn(frameNumber);
        when(htmlUtils.buildFrameTagFor(frameNumber, msg, "screenshot-message")).thenReturn(tag);

        assertEquals(spectrumEntity, spectrumEntity.screenshotFail(msg));

        verify(extentTest).log(eq(FAIL), eq(tag), any());
    }

    @Test
    @DisplayName("addScreenshotToReport should fall back to taking a screenshot of the visible page if an exception is thrown")
    void addScreenshotToReport() throws IOException {
        final Status status = INFO;
        final Path reportsFolder = Files.createTempDirectory("reportsFolder");
        REPORTS_FOLDERS.add(reportsFolder);

        when(testData.getScreenshotFolderPath()).thenReturn(reportsFolder);
        when(statefulExtentTest.getDisplayName()).thenReturn(DISPLAY_NAME);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);
        when(configuration.getVideo()).thenReturn(video);
        when(video.getFrameNumberFor(MANUAL)).thenReturn(frameNumber);
        when(htmlUtils.buildFrameTagFor(frameNumber, msg, "screenshot-message")).thenReturn(tag);

        when(((TakesScreenshot) webDriver).getScreenshotAs(BYTES)).thenReturn(new byte[]{1, 2, 3});

        final Media screenShot = spectrumEntity.addScreenshotToReport(msg, status);

        assertNotNull(screenShot);

        final String screenShotName = screenShot.getPath();
        final Path screenshotPath = Path.of(screenShotName);

        assertTrue(Files.exists(screenshotPath));
        assertEquals(reportsFolder, screenshotPath.getParent());
        assertThat(screenshotPath.getFileName().toString(), matchesPattern(UUID_REGEX));
        verify(extentTest).log(status, tag, screenShot);
    }

    @DisplayName("deleteDownloadsFolder should delete and recreate the downloads folder")
    @ParameterizedTest(name = "with value {0}")
    @MethodSource("valuesProvider")
    void deleteDownloadsFolder(final Path downloadsFolder) {
        Reflections.setField("configuration", spectrumEntity, configuration);

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getDownloadsFolder()).thenReturn(downloadsFolder.toString());

        spectrumEntity.deleteDownloadsFolder();

        assertNotNull(downloadsFolder.toFile());
        assertEquals(0, Objects.requireNonNull(downloadsFolder.toFile().list()).length);
        //noinspection ResultOfMethodCallIgnored
        downloadsFolder.toFile().delete();
    }

    static Stream<Arguments> valuesProvider() throws IOException {
        return Stream.of(
                arguments(Path.of("abc not existing")),
                arguments(Files.createTempDirectory("downloadsFolder")));
    }

    @Test
    @DisplayName("waitForDownloadOf should return true if the provided file is fully downloaded")
    void waitForDownloadOf() throws IOException {
        final Path path = Files.createTempFile("fakeFile", ".txt");
        Files.writeString(path, "I'm an airplane!!!");

        assertEquals(spectrumEntity, spectrumEntity.waitForDownloadOf(path));

        verify(downloadWait).until(functionArgumentCaptor.capture());
        final Function<WebDriver, Boolean> function = functionArgumentCaptor.getValue();

        assertTrue(function.apply(webDriver));
    }

    @Test
    @DisplayName("waitForDownloadOf should wait until the provided file is fully downloaded")
    void waitForDownloadOfNotYetDone() throws IOException {
        final Path path = Files.createTempFile("fakeFile", ".txt");

        spectrumEntity.waitForDownloadOf(path);

        verify(downloadWait).until(functionArgumentCaptor.capture());
        final Function<WebDriver, Boolean> function = functionArgumentCaptor.getValue();

        assertFalse(function.apply(webDriver));
    }

    @Test
    @DisplayName("waitForDownloadOf should wait until the provided file exist")
    void waitForDownloadOfNotYetCreated() {
        spectrumEntity.waitForDownloadOf(Path.of("not existing"));

        verify(downloadWait).until(functionArgumentCaptor.capture());
        final Function<WebDriver, Boolean> function = functionArgumentCaptor.getValue();

        assertFalse(function.apply(webDriver));
    }

    @Test
    @DisplayName("checkDownloadedFile should check if the file with the provided name matches the downloaded one")
    void checkDownloadedFile() throws IOException {
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

        Reflections.setField("configuration", spectrumEntity, configuration);

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
    @DisplayName("checkDownloadedFile should check if the file with the provided name matches the downloaded one, with different names")
    void checkDownloadedFileDifferentName() throws IOException {
        final Path downloadsFolder = Files.createTempDirectory("downloadsFolder");
        final Path filesFolder = Files.createTempDirectory("filesFolder");
        final Path downloadedFile = Files.createFile(Path.of(downloadsFolder + "/fakeFileDownloaded.txt"));
        final Path fileToCheck = Files.createFile(Path.of(filesFolder + "/fakeFile.txt"));
        Files.writeString(downloadedFile, "I'm an airplane!!!");
        Files.writeString(fileToCheck, "I'm an airplane!!!");

        Reflections.setField("configuration", spectrumEntity, configuration);

        downloadedFile.toFile().deleteOnExit();
        fileToCheck.toFile().deleteOnExit();
        downloadsFolder.toFile().deleteOnExit();
        filesFolder.toFile().deleteOnExit();

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getDownloadsFolder()).thenReturn(downloadsFolder.toString());
        when(runtime.getFilesFolder()).thenReturn(filesFolder.toString());

        assertTrue(spectrumEntity.checkDownloadedFile(downloadedFile.getFileName().toString(), fileToCheck.getFileName().toString()));
    }

    @Test
    @DisplayName("sha256Of should return the byte array of the sha digest of the provided file")
    void sha256Of() throws IOException {
        final Path path = Files.createTempFile("fakeFile", ".txt");
        Files.writeString(path, "I'm an airplane!!!");
        path.toFile().deleteOnExit();
        assertArrayEquals(new byte[]{-84, -101, -4, -117, -46, -98, 10, -68, -51, 127, 64, -87, 51, 9, -1, 13, -39, 103,
                -126, 71, -121, -84, -51, 110, 113, -124, 119, -71, -51, 73, -75, 100}, SpectrumEntity.sha256Of(path));
    }

    @DisplayName("clearAndSendKeys should clear the provided webElement and send the provided keys to it")
    @ParameterizedTest(name = "with keys {0}")
    @ValueSource(strings = {"string", "another"})
    void clearAndSendKeys(final String string) {
        assertEquals(webElement, spectrumEntity.clearAndSendKeys(webElement, string));

        verify(webElement).clear();
        verify(webElement).sendKeys(string);
    }

    @Test
    @DisplayName("upload should send the full path of the provided file to the provided webElement and return the SpectrumEntity instance")
    void upload() {
        final String filesFolder = "filesFolder";
        final String fileName = "fileName";

        Reflections.setField("configuration", spectrumEntity, configuration);

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getFilesFolder()).thenReturn(filesFolder);

        assertEquals(spectrumEntity, spectrumEntity.upload(webElement, fileName));

        verify(webElement).sendKeys(Path.of(System.getProperty("user.dir"), filesFolder, fileName).toString());
    }

    @DisplayName("isPresent should return true if the element located by the provided By is in the dom")
    @ParameterizedTest(name = "with list {0} we expect {1}")
    @MethodSource("isPresentProvider")
    void isPresent(final List<WebElement> webElements, final boolean expected) {
        when(webDriver.findElements(by)).thenReturn(webElements);

        assertEquals(expected, spectrumEntity.isPresent(by));
    }

    static Stream<Arguments> isPresentProvider() {
        return Stream.of(
                arguments(List.of(), false),
                arguments(List.of(mock(WebElement.class)), true)
        );
    }

    @DisplayName("isNotPresent should return true if the element located by the provided By is not in the dom")
    @ParameterizedTest(name = "with list {0} we expect {1}")
    @MethodSource("isNotPresentProvider")
    void isNotPresent(final List<WebElement> webElements, final boolean expected) {
        when(webDriver.findElements(by)).thenReturn(webElements);

        assertEquals(expected, spectrumEntity.isNotPresent(by));
    }

    static Stream<Arguments> isNotPresentProvider() {
        return Stream.of(
                arguments(List.of(), true),
                arguments(List.of(mock(WebElement.class)), false)
        );
    }

    @DisplayName("hasClass should check if the provided webElement has the provided css class")
    @ParameterizedTest(name = "with class {0} we expect {1}")
    @MethodSource("hasClassProvider")
    void hasClass(final String classes, final boolean expected) {
        doReturn(classes).when(webElement).getDomAttribute("class");

        assertEquals(expected, spectrumEntity.hasClass(webElement, "cssClass"));
    }

    static Stream<Arguments> hasClassProvider() {
        return Stream.of(
                arguments(null, false),
                arguments("", false),
                arguments("cssClass", true),
                arguments("one cssClass another", true)
        );
    }

    @DisplayName("hasClasses should check if the provided webElement has all the provided css class")
    @ParameterizedTest(name = "with class {0} we expect {1}")
    @MethodSource("hasClassesProvider")
    void hasClasses(final String classes, final boolean expected) {
        doReturn(classes).when(webElement).getDomAttribute("class");

        assertEquals(expected, spectrumEntity.hasClasses(webElement, "one", "cssClass"));
    }

    static Stream<Arguments> hasClassesProvider() {
        return Stream.of(
                arguments(null, false),
                arguments("", false),
                arguments("cssClass", false),
                arguments("one cssClass another", true)
        );
    }

    private static final class DummySpectrumEntity<T> extends SpectrumEntity<DummySpectrumEntity<T>, T> {
    }
}
