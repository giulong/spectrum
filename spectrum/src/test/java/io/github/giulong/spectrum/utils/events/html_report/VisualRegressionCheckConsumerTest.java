package io.github.giulong.spectrum.utils.events.html_report;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import io.github.giulong.spectrum.exceptions.TestFailedException;
import io.github.giulong.spectrum.exceptions.VisualRegressionException;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

import static com.aventstack.extentreports.Status.FAIL;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.utils.web_driver_events.ScreenshotConsumer.SCREENSHOT;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class VisualRegressionCheckConsumerTest {

    private final byte[] screenshot = new byte[]{1, 2, 3};
    private final byte[] matchingChecksum = new byte[]{4, 5, 6};
    private final byte[] anotherChecksum = new byte[]{7, 8, 9};
    private final int frameNumber = 123;

    private MockedStatic<MediaEntityBuilder> mediaEntityBuilderMockedStatic;
    private MockedStatic<Files> filesMockedStatic;

    @Mock
    private MediaEntityBuilder mediaEntityBuilder;

    @Mock
    private Configuration configuration;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private HtmlUtils htmlUtils;

    @Mock
    private ContextManager contextManager;

    @Mock
    private Path regressionPath;

    @Mock
    private Path referencePath;

    @Mock
    private ExtentTest currentNode;

    @Mock
    private Status status;

    @Mock
    private Media media;

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
    private Supplier<TestFailedException> testFailedExceptionSupplier;

    @Mock
    private VisualRegressionException visualRegressionException;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private Video video;

    @Mock
    private TestData.VisualRegression visualRegression;

    @InjectMocks
    private VisualRegressionCheckConsumer consumer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", consumer, configuration);
        Reflections.setField("fileUtils", consumer, fileUtils);
        Reflections.setField("htmlUtils", consumer, htmlUtils);
        Reflections.setField("contextManager", consumer, contextManager);

        mediaEntityBuilderMockedStatic = mockStatic(MediaEntityBuilder.class);
        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    void afterEach() {
        mediaEntityBuilderMockedStatic.close();
        filesMockedStatic.close();
    }

    @Test
    @DisplayName("shouldAccept should return false if the super method does so")
    void shouldAcceptFalseSuper() {
        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.isEnabled()).thenReturn(false);

        assertFalse(consumer.shouldAccept(event));

        verifyNoInteractions(event);
    }

    @Test
    @DisplayName("shouldAccept should call the parent and return false if the references have not been generated yet")
    void shouldAcceptFalse() {
        superShouldAcceptStubs();

        when(Files.exists(referencePath)).thenReturn(false);

        assertFalse(consumer.shouldAccept(event));

        // super
        assertEquals(referencePath, Reflections.getFieldValue("referencePath", consumer));
    }

    @Test
    @DisplayName("shouldAccept should call the parent and return false if snapshots should be overridden")
    void shouldAcceptFalseOverride() {
        superShouldAcceptStubs();

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
        when(fileUtils.checksumOf(referencePath)).thenReturn(matchingChecksum);
        when(fileUtils.checksumOf(screenshot)).thenReturn(matchingChecksum);

        // generateAndAddScreenshotFrom
        final String tag = "tag";
        final String message = "message";
        Reflections.setField("screenshot", consumer, screenshot);
        Reflections.setField("frameNumber", consumer, frameNumber);
        when(event.getPayload()).thenReturn(payload);
        when(payload.get("message")).thenReturn(message);
        when(payload.get("status")).thenReturn(status);
        when(htmlUtils.buildFrameTagFor(frameNumber, message, testData, "screenshot-message")).thenReturn(tag);
        when(MediaEntityBuilder.createScreenCaptureFromPath(referencePath.toString())).thenReturn(mediaEntityBuilder);
        when(mediaEntityBuilder.build()).thenReturn(media);
        when(contextManager.getScreenshots()).thenReturn(screenshots);

        consumer.accept(event);

        // generateAndAddScreenshotFrom
        verify(currentNode).log(status, tag, media);
        verify(screenshots).put(referencePath.toString(), screenshot);
        verify(fileUtils).write(referencePath, screenshot);
        verify(testData).incrementScreenshotNumber();
    }

    @Test
    @DisplayName("accept should register the regression when the screenshot taken does not match with its reference")
    void acceptVisualRegression() {
        final Path failedScreenshotPath = mock();
        final String failedScreenshotName = "failedScreenshotName";
        final String visualRegressionTag = "visualRegressionTag";

        when(fileUtils.checksumOf(referencePath)).thenReturn(matchingChecksum);
        when(fileUtils.checksumOf(screenshot)).thenReturn(anotherChecksum);

        when(fileUtils.getFailedScreenshotNameFrom(testData)).thenReturn(failedScreenshotName);
        when(regressionPath.resolve(failedScreenshotName)).thenReturn(failedScreenshotPath);
        when(htmlUtils.buildVisualRegressionTagFor(frameNumber, testData, referencePath, failedScreenshotPath)).thenReturn(visualRegressionTag);

        // addScreenshot
        Reflections.setField("screenshot", consumer, screenshot);
        Reflections.setField("frameNumber", consumer, frameNumber);
        when(contextManager.getScreenshots()).thenReturn(screenshots);

        consumer.accept(event);

        verify(testData).registerFailedVisualRegression();
        verify(testData).incrementScreenshotNumber();

        // addScreenshot
        verify(currentNode).log(FAIL, visualRegressionTag, null);
        verify(screenshots).put(failedScreenshotPath.toString(), screenshot);
        verify(fileUtils).write(failedScreenshotPath, screenshot);
    }

    @Test
    @DisplayName("accept should register the regression when the screenshot taken does not match with its reference")
    void acceptVisualRegressionFailFast() {
        final String exceptionMessage = "exceptionMessage";
        final Path failedScreenshotPath = mock();
        final String failedScreenshotName = "failedScreenshotName";
        final String visualRegressionTag = "visualRegressionTag";

        when(fileUtils.checksumOf(referencePath)).thenReturn(matchingChecksum);
        when(fileUtils.checksumOf(screenshot)).thenReturn(anotherChecksum);

        when(fileUtils.getFailedScreenshotNameFrom(testData)).thenReturn(failedScreenshotName);
        when(regressionPath.resolve(failedScreenshotName)).thenReturn(failedScreenshotPath);
        when(htmlUtils.buildVisualRegressionTagFor(frameNumber, testData, referencePath, failedScreenshotPath)).thenReturn(visualRegressionTag);

        // addScreenshot
        Reflections.setField("screenshot", consumer, screenshot);
        Reflections.setField("frameNumber", consumer, frameNumber);
        when(contextManager.getScreenshots()).thenReturn(screenshots);

        when(visualRegressionConfiguration.isFailFast()).thenReturn(true);
        when(testData.getTestFailedException()).thenReturn(testFailedExceptionSupplier);
        when(testFailedExceptionSupplier.get()).thenReturn(visualRegressionException);
        when(visualRegressionException.getMessage()).thenReturn(exceptionMessage);

        final VisualRegressionException exception = assertThrows(VisualRegressionException.class, () -> consumer.accept(event));
        assertEquals(exceptionMessage, exception.getMessage());

        verify(testData).registerFailedVisualRegression();
        verify(testData, never()).incrementScreenshotNumber();

        // addScreenshot
        verify(currentNode).log(FAIL, visualRegressionTag, null);
        verify(screenshots).put(failedScreenshotPath.toString(), screenshot);
        verify(fileUtils).write(failedScreenshotPath, screenshot);
    }

    private void superShouldAcceptStubs() {
        final String screenshotName = "screenshotName";
        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(event.getPayload()).thenReturn(payload);
        when(payload.get(EXTENSION_CONTEXT)).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getVisualRegression()).thenReturn(visualRegression);
        when(visualRegression.getPath()).thenReturn(regressionPath);
        when(fileUtils.getScreenshotNameFrom(testData)).thenReturn(screenshotName);
        when(regressionPath.resolve(screenshotName)).thenReturn(referencePath);
        when(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)).thenReturn(statefulExtentTest);
        when(statefulExtentTest.getCurrentNode()).thenReturn(currentNode);
        when(configuration.getVideo()).thenReturn(video);
        when(video.getAndIncrementFrameNumberFor(testData, MANUAL)).thenReturn(frameNumber);
        when(payload.get(SCREENSHOT)).thenReturn(screenshot);
    }
}
