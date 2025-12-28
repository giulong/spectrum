package io.github.giulong.spectrum.utils.events.html_report;

import static com.aventstack.extentreports.Status.FAIL;
import static io.github.giulong.spectrum.enums.Frame.AUTO_BEFORE;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.utils.web_driver_events.VideoAutoScreenshotProducer.SCREENSHOT;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.OutputType.BYTES;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

import com.aventstack.extentreports.ExtentTest;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.exceptions.VisualRegressionException;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.video.Video;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.*;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

class VisualRegressionCheckConsumerTest {

    private final byte[] screenshot = new byte[]{1, 2, 3};
    private final byte[] checksum = new byte[]{4, 5, 6};
    private final byte[] screenshot2 = new byte[]{4};
    private final int frameNumber = 123;
    private final String primaryId = "autoBefore";

    private MockedStatic<Files> filesMockedStatic;

    @MockSingleton
    @SuppressWarnings("unused")
    private Configuration configuration;

    @MockSingleton
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @MockSingleton
    @SuppressWarnings("unused")
    private HtmlUtils htmlUtils;

    @MockSingleton
    @SuppressWarnings("unused")
    private ContextManager contextManager;

    @Mock
    private Path regressionPath;

    @Mock
    private Path referencePath;

    @Mock
    private ExtentTest currentNode;

    @Mock
    private Event event;

    @Mock
    private Configuration.VisualRegression visualRegressionConfiguration;

    @Mock
    private Configuration.VisualRegression.Snapshots snapshots;

    @Mock
    private Map<String, byte[]> screenshots;

    @Mock
    private Map<String, Object> payload;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private TestData testData;

    @Mock
    private VisualRegressionException visualRegressionException;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private Video video;

    @Mock
    private TestData.VisualRegression visualRegression;

    @Mock
    private Configuration.VisualRegression.Checks checks;

    @Mock
    private Duration interval;

    @Mock(extraInterfaces = TakesScreenshot.class)
    private WebDriver driver;

    @Captor
    private ArgumentCaptor<byte[]> byteArrayArgumentCaptor;

    @InjectMocks
    private VisualRegressionCheckConsumer consumer;

    @BeforeEach
    void beforeEach() {
        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    void afterEach() {
        filesMockedStatic.close();
    }

    @Test
    @DisplayName("shouldAccept should return false if the super method does so")
    void shouldAcceptFalseSuper() {
        superShouldAcceptStubs();

        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.isEnabled()).thenReturn(false);

        assertFalse(consumer.shouldAccept(event));

        verifyNoMoreInteractions(event);
    }

    @Test
    @DisplayName("shouldAccept should call the parent and return false if the references have not been generated yet")
    void shouldAcceptFalse() {
        superShouldAcceptStubs();

        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(event.getPrimaryId()).thenReturn(primaryId);
        when(visualRegressionConfiguration.shouldCheck(AUTO_BEFORE)).thenReturn(true);
        when(Files.exists(referencePath)).thenReturn(false);

        assertFalse(consumer.shouldAccept(event));

        // super
        assertEquals(referencePath, Reflections.getFieldValue("referencePath", consumer));
    }

    @Test
    @DisplayName("shouldAccept should call the parent and return false if snapshots should be overridden")
    void shouldAcceptFalseOverride() {
        superShouldAcceptStubs();

        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(event.getPrimaryId()).thenReturn(primaryId);
        when(visualRegressionConfiguration.shouldCheck(AUTO_BEFORE)).thenReturn(true);
        when(Files.exists(referencePath)).thenReturn(true);

        when(visualRegressionConfiguration.getSnapshots()).thenReturn(snapshots);
        when(snapshots.isOverride()).thenReturn(true);

        assertFalse(consumer.shouldAccept(event));

        // super
        assertEquals(referencePath, Reflections.getFieldValue("referencePath", consumer));
    }

    @Test
    @DisplayName("shouldAccept should call the parent and return true if the references have already been generated and override is false")
    void shouldAcceptTrue() {
        superShouldAcceptStubs();

        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(event.getPrimaryId()).thenReturn(primaryId);
        when(visualRegressionConfiguration.shouldCheck(AUTO_BEFORE)).thenReturn(true);
        when(visualRegressionConfiguration.getSnapshots()).thenReturn(snapshots);
        when(snapshots.isOverride()).thenReturn(false);

        when(Files.exists(referencePath)).thenReturn(true);

        assertTrue(consumer.shouldAccept(event));

        // super
        assertEquals(referencePath, Reflections.getFieldValue("referencePath", consumer));
    }

    @Test
    @DisplayName("accept should delegate to generateAndAddScreenshotFrom when the screenshot taken matches with its reference")
    void accept() {
        runChecksStubs();

        when(fileUtils.compare(referencePath, screenshot)).thenReturn(true);

        // addScreenshot
        Reflections.setField("screenshot", consumer, screenshot);
        when(contextManager.getScreenshots()).thenReturn(screenshots);

        consumer.accept(event);

        // addScreenshot
        verify(screenshots).put(referencePath.toString(), screenshot);
        verify(fileUtils).write(referencePath, screenshot);

        runChecksAssertions();
    }

    @Test
    @DisplayName("accept should register the regression when the screenshot taken does not match with its reference")
    void acceptVisualRegression() throws IOException {
        final Path failedScreenshotPath = mock();
        final String failedScreenshotName = "failedScreenshotName";
        final String visualRegressionTag = "visualRegressionTag";

        runChecksStubs();

        when(fileUtils.compare(referencePath, screenshot)).thenReturn(false);

        when(fileUtils.getFailedScreenshotNameFrom(testData)).thenReturn(failedScreenshotName);
        when(regressionPath.resolve(failedScreenshotName)).thenReturn(failedScreenshotPath);
        when(Files.readAllBytes(referencePath)).thenReturn(checksum);
        when(testData.getFrameNumber()).thenReturn(frameNumber);
        when(htmlUtils.buildVisualRegressionTagFor(frameNumber, testData, checksum, screenshot)).thenReturn(visualRegressionTag);

        // addScreenshot
        Reflections.setField("screenshot", consumer, screenshot);
        when(contextManager.getScreenshots()).thenReturn(screenshots);

        consumer.accept(event);

        verify(testData).registerFailedVisualRegression();

        // addScreenshot
        verify(currentNode).log(FAIL, visualRegressionTag, null);
        verify(screenshots).put(failedScreenshotPath.toString(), screenshot);
        verify(fileUtils).write(failedScreenshotPath, screenshot);

        runChecksAssertions();
    }

    @Test
    @DisplayName("accept should register the regression when the screenshot taken does not match with its reference")
    void acceptVisualRegressionFailFast() throws IOException {
        final String exceptionMessage = "exceptionMessage";
        final Path failedScreenshotPath = mock();
        final String failedScreenshotName = "failedScreenshotName";
        final String visualRegressionTag = "visualRegressionTag";

        runChecksStubs();

        when(fileUtils.compare(referencePath, screenshot)).thenReturn(false);

        when(fileUtils.getFailedScreenshotNameFrom(testData)).thenReturn(failedScreenshotName);
        when(regressionPath.resolve(failedScreenshotName)).thenReturn(failedScreenshotPath);
        when(Files.readAllBytes(referencePath)).thenReturn(checksum);
        when(testData.getFrameNumber()).thenReturn(frameNumber);
        when(htmlUtils.buildVisualRegressionTagFor(frameNumber, testData, checksum, screenshot)).thenReturn(visualRegressionTag);

        // addScreenshot
        Reflections.setField("screenshot", consumer, screenshot);
        when(contextManager.getScreenshots()).thenReturn(screenshots);

        when(visualRegressionConfiguration.isFailFast()).thenReturn(true);
        when(testData.getTestFailedException()).thenReturn(visualRegressionException);
        when(visualRegressionException.getMessage()).thenReturn(exceptionMessage);

        final VisualRegressionException exception = assertThrows(VisualRegressionException.class, () -> consumer.accept(event));
        assertEquals(exceptionMessage, exception.getMessage());

        verify(testData).registerFailedVisualRegression();
        verify(testData, never()).incrementScreenshotNumber();

        // addScreenshot
        verify(currentNode).log(FAIL, visualRegressionTag, null);
        verify(screenshots).put(failedScreenshotPath.toString(), screenshot);
        verify(fileUtils).write(failedScreenshotPath, screenshot);

        runChecksAssertions();
    }

    private void superShouldAcceptStubs() {
        final String screenshotName = "screenshotName";
        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(event.getPayload()).thenReturn(payload);
        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        lenient().when(testData.getVisualRegression()).thenReturn(visualRegression);
        lenient().when(visualRegression.getPath()).thenReturn(regressionPath);
        lenient().when(fileUtils.getScreenshotNameFrom(testData)).thenReturn(screenshotName);
        lenient().when(regressionPath.resolve(screenshotName)).thenReturn(referencePath);
        when(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)).thenReturn(statefulExtentTest);
        when(statefulExtentTest.getCurrentNode()).thenReturn(currentNode);
        lenient().when(configuration.getVideo()).thenReturn(video);
        when(payload.get(SCREENSHOT)).thenReturn(screenshot);
    }

    private void runChecksStubs() {
        final int maxRetries = 1;
        final int count = 2;

        Reflections.setField("screenshot", consumer, screenshot);
        when(visualRegressionConfiguration.getChecks()).thenReturn(checks);
        when(checks.getInterval()).thenReturn(interval);
        when(checks.getMaxRetries()).thenReturn(maxRetries);
        when(checks.getCount()).thenReturn(count);
        when(event.getPayload()).thenReturn(Map.of("takesScreenshot", driver));
        when(((TakesScreenshot) driver).getScreenshotAs(BYTES)).thenReturn(screenshot2);
        when(fileUtils.compare(eq(screenshot), byteArrayArgumentCaptor.capture())).thenReturn(true);
    }

    private void runChecksAssertions() {
        assertArrayEquals(screenshot2, byteArrayArgumentCaptor.getAllValues().getFirst());
    }
}
