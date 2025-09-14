package io.github.giulong.spectrum;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import io.github.giulong.spectrum.interfaces.Shared;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.TestContext;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.openqa.selenium.By;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.aventstack.extentreports.Status.INFO;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.ORIGINAL_DRIVER;
import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.utils.web_driver_events.VideoAutoScreenshotProducer.SCREENSHOT;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.OutputType.BYTES;

class SpectrumEntityTest {

    private MockedStatic<Path> pathMockedStatic;
    private MockedStatic<Files> filesMockedStatic;
    private MockedStatic<MediaEntityBuilder> mediaEntityBuilderMockedStatic;
    private MockedStatic<MessageDigest> messageDigestMockedStatic;

    private final String msg = "msg";
    private final byte[] bytes = new byte[]{1, 2, 3};

    @Mock
    private WebDriverWait downloadWait;

    @MockSingleton
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private WebElement webElement;

    @Mock
    private Actions actions;

    @Mock
    private By by;

    @MockSingleton
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @Mock
    private Path path;

    @Mock
    private Path downloadsFolderPath;

    @Mock
    private Path filesFolderPath;

    @Mock
    private File file;

    @MockSingleton
    @SuppressWarnings("unused")
    private EventsDispatcher eventsDispatcher;

    @Mock
    private TestContext testContext;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock(extraInterfaces = TakesScreenshot.class)
    private WebDriver driver;

    @Captor
    private ArgumentCaptor<Function<WebDriver, Boolean>> functionArgumentCaptor;

    @InjectMocks
    private DummySpectrumEntity<?> spectrumEntity;

    @BeforeEach
    void beforeEach() {
        pathMockedStatic = mockStatic(Path.class);
        filesMockedStatic = mockStatic(Files.class);
        mediaEntityBuilderMockedStatic = mockStatic(MediaEntityBuilder.class);
        messageDigestMockedStatic = mockStatic(MessageDigest.class);
    }

    @AfterEach
    void afterEach() {
        pathMockedStatic.close();
        filesMockedStatic.close();
        mediaEntityBuilderMockedStatic.close();
        messageDigestMockedStatic.close();
    }

    @SneakyThrows
    private void addScreenshotToReportStubs() {
        when(testContext.get(EXTENSION_CONTEXT, ExtensionContext.class)).thenReturn(context);

        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(ORIGINAL_DRIVER, WebDriver.class)).thenReturn(driver);

        when(((TakesScreenshot) driver).getScreenshotAs(BYTES)).thenReturn(bytes);
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
        assertEquals(21, actual.size());
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
                "logInspector",
                "browsingContext",
                "browsingContextInspector",
                "network",
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
    }

    @Test
    @DisplayName("infoWithScreenshot should delegate to addScreenshotToReport")
    void infoWithScreenshot() {
        addScreenshotToReportStubs();

        assertEquals(spectrumEntity, spectrumEntity.screenshotInfo(msg));
    }

    @Test
    @DisplayName("warningWithScreenshot should delegate to addScreenshotToReport")
    void warningWithScreenshot() {
        addScreenshotToReportStubs();

        assertEquals(spectrumEntity, spectrumEntity.screenshotWarning(msg));
    }

    @Test
    @DisplayName("failWithScreenshot should delegate to addScreenshotToReport")
    void failWithScreenshot() {
        addScreenshotToReportStubs();

        assertEquals(spectrumEntity, spectrumEntity.screenshotFail(msg));
    }

    @Test
    @DisplayName("addScreenshotToReport should fall back to taking a screenshot of the visible page if an exception is thrown")
    void addScreenshotToReport() {
        final Status status = INFO;

        addScreenshotToReportStubs();

        spectrumEntity.addScreenshotToReport(msg, status);

        verify(eventsDispatcher).fire(MANUAL.getValue(), SCREENSHOT, Map.of(EXTENSION_CONTEXT, context, SCREENSHOT, bytes, "message", msg, "status", status));
        verifyNoMoreInteractions(eventsDispatcher);
    }

    @Test
    @DisplayName("deleteDownloadsFolder should delete and recreate the downloads folder")
    void deleteDownloadsFolder() {
        final String downloadsFolder = "downloadsFolder";

        when(Path.of(downloadsFolder)).thenReturn(path);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getDownloadsFolder()).thenReturn(downloadsFolder);

        assertEquals(spectrumEntity, spectrumEntity.deleteDownloadsFolder());

        verify(fileUtils).deleteContentOf(path);
    }

    @Test
    @DisplayName("waitForDownloadOf should return true if the provided file is fully downloaded")
    void waitForDownloadOf() {
        when(Files.exists(path)).thenReturn(true);
        when(path.toFile()).thenReturn(file);
        when(file.length()).thenReturn(123L);

        assertEquals(spectrumEntity, spectrumEntity.waitForDownloadOf(path));

        verify(downloadWait).until(functionArgumentCaptor.capture());
        final Function<WebDriver, Boolean> function = functionArgumentCaptor.getValue();

        assertTrue(function.apply(driver));
    }

    @Test
    @DisplayName("waitForDownloadOf should wait until the provided file is fully downloaded")
    void waitForDownloadOfNotYetDone() {
        when(Files.exists(path)).thenReturn(true);
        when(path.toFile()).thenReturn(file);
        when(file.length()).thenReturn(0L);

        assertEquals(spectrumEntity, spectrumEntity.waitForDownloadOf(path));

        verify(downloadWait).until(functionArgumentCaptor.capture());
        final Function<WebDriver, Boolean> function = functionArgumentCaptor.getValue();

        assertFalse(function.apply(driver));
    }

    @Test
    @DisplayName("waitForDownloadOf should wait until the provided file exist")
    void waitForDownloadOfNotYetCreated() {
        when(Files.exists(path)).thenReturn(false);

        assertEquals(spectrumEntity, spectrumEntity.waitForDownloadOf(path));

        verify(downloadWait).until(functionArgumentCaptor.capture());
        final Function<WebDriver, Boolean> function = functionArgumentCaptor.getValue();

        assertFalse(function.apply(driver));
    }

    @Test
    @DisplayName("checkDownloadedFile should check if the file with the provided name matches the downloaded one, with different names")
    void checkDownloadedFileDifferentName() {
        final String downloadsFolder = "downloadsFolder";
        final String filesFolder = "filesFolder";
        final String fileToCheckName = "fileToCheckName";

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getDownloadsFolder()).thenReturn(downloadsFolder);
        when(Path.of(downloadsFolder, fileToCheckName)).thenReturn(downloadsFolderPath);
        when(downloadsFolderPath.toAbsolutePath()).thenReturn(downloadsFolderPath);
        when(runtime.getFilesFolder()).thenReturn(filesFolder);
        when(Path.of(filesFolder, fileToCheckName)).thenReturn(filesFolderPath);
        when(filesFolderPath.toAbsolutePath()).thenReturn(filesFolderPath);

        when(fileUtils.compare(downloadsFolderPath, filesFolderPath)).thenReturn(true);

        assertTrue(spectrumEntity.checkDownloadedFile(fileToCheckName));
    }

    @Test
    @DisplayName("checkDownloadedFile should check if the file with the provided name matches the downloaded one")
    void checkDownloadedFile() {
        final String downloadedFileName = "downloadedFileName";
        final String downloadsFolder = "downloadsFolder";
        final String filesFolder = "filesFolder";
        final String fileToCheckName = "fileToCheckName";

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getDownloadsFolder()).thenReturn(downloadsFolder);
        when(Path.of(downloadsFolder, downloadedFileName)).thenReturn(downloadsFolderPath);
        when(downloadsFolderPath.toAbsolutePath()).thenReturn(downloadsFolderPath);
        when(runtime.getFilesFolder()).thenReturn(filesFolder);
        when(Path.of(filesFolder, fileToCheckName)).thenReturn(filesFolderPath);
        when(filesFolderPath.toAbsolutePath()).thenReturn(filesFolderPath);

        when(fileUtils.compare(downloadsFolderPath, filesFolderPath)).thenReturn(true);

        assertTrue(spectrumEntity.checkDownloadedFile(downloadedFileName, fileToCheckName));
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

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getFilesFolder()).thenReturn(filesFolder);
        when(Path.of(System.getProperty("user.dir"), filesFolder, fileName)).thenReturn(path);

        assertEquals(spectrumEntity, spectrumEntity.upload(webElement, fileName));

        verify(webElement).sendKeys(path.toString());
    }

    @DisplayName("isPresent should return true if the element located by the provided By is in the dom")
    @ParameterizedTest(name = "with list {0} we expect {1}")
    @MethodSource("isPresentProvider")
    void isPresent(final List<WebElement> webElements, final boolean expected) {
        when(driver.findElements(by)).thenReturn(webElements);

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
        when(driver.findElements(by)).thenReturn(webElements);

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
