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

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.exceptions.VisualRegressionException;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

class VisualRegressionConsumerTest {

    private final byte[] screenshot = new byte[]{1, 2, 3};
    private final byte[] screenshot2 = new byte[]{4};
    private final byte[] screenshot3 = new byte[]{5};
    private final String primaryId = "autoBefore";
    private final int count = 2;

    private MockedStatic<MediaEntityBuilder> mediaEntityBuilderMockedStatic;

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
    private Path referencePath;

    @Mock
    private Path regressionPath;

    @Mock
    private ExtentTest currentNode;

    @Mock
    private Event event;

    @Mock
    private Configuration.VisualRegression visualRegressionConfiguration;

    @Mock
    private Configuration.VisualRegression.Snapshots snapshots;

    @Mock
    private Map<String, Object> payload;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private TestData testData;

    @Mock
    private TestData.VisualRegression visualRegression;

    @Mock
    private Configuration.VisualRegression.Checks checks;

    @Mock
    private Duration interval;

    @Mock(extraInterfaces = TakesScreenshot.class)
    private WebDriver driver;

    @Mock
    private Map<String, byte[]> screenshots;

    @Captor
    private ArgumentCaptor<byte[]> byteArrayArgumentCaptor;

    @InjectMocks
    private DummyVisualRegressionConsumer consumer;

    @BeforeEach
    void beforeEach() {
        mediaEntityBuilderMockedStatic = mockStatic(MediaEntityBuilder.class);
    }

    @AfterEach
    void afterEach() {
        mediaEntityBuilderMockedStatic.close();
    }

    @Test
    @DisplayName("shouldAccept should do nothing if visual regression is disabled")
    void shouldAcceptFalse() {
        superShouldAcceptStubs();

        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.isEnabled()).thenReturn(false);

        assertFalse(consumer.shouldAccept(event));

        verifyNoMoreInteractions(event);
    }

    @Test
    @DisplayName("shouldAccept should do nothing if visual regression is enabled but the frame should not be checked")
    void shouldAcceptShouldCheckFalse() {
        superShouldAcceptStubs();

        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(event.getPrimaryId()).thenReturn(primaryId);
        when(visualRegressionConfiguration.shouldCheck(AUTO_BEFORE)).thenReturn(false);

        assertFalse(consumer.shouldAccept(event));

        verifyNoMoreInteractions(event);
    }

    @Test
    @DisplayName("shouldAccept should set the reference path when visual regression is enabled")
    void shouldAcceptTrue() {
        final String screenshotName = "screenshotName";

        superShouldAcceptStubs();

        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(event.getPrimaryId()).thenReturn(primaryId);
        when(visualRegressionConfiguration.shouldCheck(AUTO_BEFORE)).thenReturn(true);

        when(testData.getVisualRegression()).thenReturn(visualRegression);
        when(testData.isDynamic()).thenReturn(false);
        when(visualRegression.getPath()).thenReturn(regressionPath);
        when(fileUtils.getScreenshotNameFrom(testData)).thenReturn(screenshotName);
        when(regressionPath.resolve(screenshotName)).thenReturn(referencePath);
        when(payload.get(SCREENSHOT)).thenReturn(screenshot);

        Reflections.setField("referencePath", consumer, null);
        assertTrue(consumer.shouldAccept(event));
        assertEquals(visualRegressionConfiguration, Reflections.getFieldValue("visualRegression", consumer));
        assertEquals(referencePath, Reflections.getFieldValue("referencePath", consumer));
        assertEquals(regressionPath, Reflections.getFieldValue("regressionPath", consumer));
        assertEquals(testData, Reflections.getFieldValue("testData", consumer));
        assertEquals(currentNode, Reflections.getFieldValue("currentNode", consumer));
        assertEquals(screenshot, Reflections.getFieldValue("screenshot", consumer));

        verify(visualRegression, never()).getDynamicPath();
    }

    @Test
    @DisplayName("shouldAccept should set the reference path for the dynamic test when visual regression is enabled")
    void shouldAcceptTrueDynamic() {
        final String screenshotName = "screenshotName";

        superShouldAcceptStubs();

        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(event.getPrimaryId()).thenReturn(primaryId);
        when(visualRegressionConfiguration.shouldCheck(AUTO_BEFORE)).thenReturn(true);

        when(testData.getVisualRegression()).thenReturn(visualRegression);
        when(testData.isDynamic()).thenReturn(true);
        when(visualRegression.getDynamicPath()).thenReturn(regressionPath);
        when(fileUtils.getScreenshotNameFrom(testData)).thenReturn(screenshotName);
        when(regressionPath.resolve(screenshotName)).thenReturn(referencePath);
        when(payload.get(SCREENSHOT)).thenReturn(screenshot);

        Reflections.setField("referencePath", consumer, null);
        assertTrue(consumer.shouldAccept(event));
        assertEquals(visualRegressionConfiguration, Reflections.getFieldValue("visualRegression", consumer));
        assertEquals(referencePath, Reflections.getFieldValue("referencePath", consumer));
        assertEquals(regressionPath, Reflections.getFieldValue("regressionPath", consumer));
        assertEquals(testData, Reflections.getFieldValue("testData", consumer));
        assertEquals(currentNode, Reflections.getFieldValue("currentNode", consumer));
        assertEquals(screenshot, Reflections.getFieldValue("screenshot", consumer));

        verify(visualRegression, never()).getPath();
    }

    @DisplayName("shouldOverrideSnapshots should check if snapshots override is configured")
    @ParameterizedTest(name = "with override {0} we expect {0}")
    @ValueSource(booleans = {true, false})
    void shouldOverrideSnapshots(final boolean expected) {
        when(visualRegressionConfiguration.getSnapshots()).thenReturn(snapshots);
        when(snapshots.isOverride()).thenReturn(expected);

        assertEquals(expected, consumer.shouldOverrideSnapshots());
    }

    private void superShouldAcceptStubs() {
        when(event.getPayload()).thenReturn(payload);
        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)).thenReturn(statefulExtentTest);
        when(statefulExtentTest.getCurrentNode()).thenReturn(currentNode);
    }

    @Test
    @DisplayName("runChecksOn should perform additional successful checks")
    void runChecksOn() {
        runChecksOnStubs(1);

        when(fileUtils.compare(eq(screenshot), byteArrayArgumentCaptor.capture())).thenReturn(true);

        consumer.runChecksOn(event);

        assertArrayEquals(screenshot2, byteArrayArgumentCaptor.getAllValues().getFirst());
        assertArrayEquals(screenshot3, byteArrayArgumentCaptor.getAllValues().get(1));
    }

    @Test
    @DisplayName("runChecksOn should perform additional checks and retry")
    void runChecksOnRetry() {
        final String visualRegressionTag = "visualRegressionTag";

        runChecksOnStubs(2);

        Reflections.setField("htmlUtils", consumer, htmlUtils);

        when(fileUtils.compare(eq(screenshot), byteArrayArgumentCaptor.capture()))
                .thenReturn(false)
                .thenReturn(true);

        consumer.runChecksOn(event);

        assertArrayEquals(screenshot2, byteArrayArgumentCaptor.getValue());
        assertEquals(screenshot3, Reflections.getFieldValue("screenshot", consumer));

        verifyNoInteractions(htmlUtils);

        // addScreenshot
        verify(currentNode, never()).log(FAIL, visualRegressionTag, null);
        verify(contextManager, never()).getScreenshots();
        verify(fileUtils, never()).write(any(), (byte[]) any());
    }

    @Test
    @DisplayName("runChecksOn should perform additional checks and throw a VisualRegressionCheckException when failed")
    void runChecksOnFailed() {
        final String visualRegressionTag = "visualRegressionTag";
        final int frameNumber = 123;

        runChecksOnStubs(1);

        Reflections.setField("htmlUtils", consumer, htmlUtils);

        try (MockedConstruction<VisualRegressionException> mockedConstruction = mockConstruction(VisualRegressionException.class, (mock, extensionContext) -> assertEquals(String
                .format("Unable to get a stable screenshot. Tried %d checks for %s times", count, 1), extensionContext.arguments().getFirst()))) {
            when(fileUtils.compare(eq(screenshot), byteArrayArgumentCaptor.capture()))
                    .thenReturn(true)
                    .thenReturn(false);

            when(testData.getFrameNumber()).thenReturn(frameNumber);
            when(htmlUtils.buildVisualRegressionTagFor(frameNumber, testData, screenshot, screenshot3)).thenReturn(visualRegressionTag);

            // addScreenshot
            when(contextManager.getScreenshots()).thenReturn(screenshots);

            final Exception exception = assertThrows(VisualRegressionException.class, () -> consumer.runChecksOn(event));

            assertEquals(mockedConstruction.constructed().getFirst(), exception);

            assertArrayEquals(screenshot2, byteArrayArgumentCaptor.getAllValues().getFirst());
            assertArrayEquals(screenshot3, byteArrayArgumentCaptor.getAllValues().get(1));

            // addScreenshot
            verify(currentNode).log(FAIL, visualRegressionTag, null);
            verify(screenshots).put(referencePath.toString(), screenshot);
            verify(fileUtils).write(referencePath, screenshot);
        }
    }

    private void runChecksOnStubs(final int maxRetries) {
        Reflections.setField("screenshot", consumer, screenshot);

        when(visualRegressionConfiguration.getChecks()).thenReturn(checks);
        when(checks.getInterval()).thenReturn(interval);
        when(checks.getMaxRetries()).thenReturn(maxRetries);
        when(checks.getCount()).thenReturn(count);
        when(event.getPayload()).thenReturn(Map.of("takesScreenshot", driver));

        when(((TakesScreenshot) driver).getScreenshotAs(BYTES))
                .thenReturn(screenshot2)
                .thenReturn(screenshot3);
    }

    private static final class DummyVisualRegressionConsumer extends VisualRegressionConsumer {

        @Override
        public void accept(final Event event) {
        }
    }
}
