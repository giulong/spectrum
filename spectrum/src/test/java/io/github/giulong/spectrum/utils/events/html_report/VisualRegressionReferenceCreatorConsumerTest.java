package io.github.giulong.spectrum.utils.events.html_report;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.MockSingleton;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.ORIGINAL_DRIVER;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.utils.web_driver_events.VideoAutoScreenshotProducer.SCREENSHOT;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.OutputType.BYTES;

class VisualRegressionReferenceCreatorConsumerTest {

    private final byte[] screenshot = new byte[]{1, 2, 3};
    private final byte[] screenshot2 = new byte[]{4};

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
    private VisualRegressionReferenceCreatorConsumer consumer;

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

        when(visualRegressionConfiguration.isEnabled()).thenReturn(false);

        assertFalse(consumer.shouldAccept(event));

        verifyNoMoreInteractions(event);
    }

    @Test
    @DisplayName("shouldAccept should call the parent and return false if the references have already been generated and snapshots override is false")
    void shouldAcceptFalse() {
        superShouldAcceptStubs();

        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(Files.notExists(referencePath)).thenReturn(false);

        when(visualRegressionConfiguration.getSnapshots()).thenReturn(snapshots);
        when(snapshots.isOverride()).thenReturn(false);

        assertFalse(consumer.shouldAccept(event));

        // super
        assertEquals(referencePath, Reflections.getFieldValue("referencePath", consumer));
    }

    @Test
    @DisplayName("shouldAccept should call the parent and return true if the references have not been generated yet")
    void shouldAcceptTrue() {
        superShouldAcceptStubs();

        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(Files.notExists(referencePath)).thenReturn(true);

        assertTrue(consumer.shouldAccept(event));

        // super
        assertEquals(referencePath, Reflections.getFieldValue("referencePath", consumer));
    }

    @Test
    @DisplayName("shouldAccept should call the parent and return true if the references have already been generated but snapshots override is true")
    void shouldAcceptTrueOverride() {
        superShouldAcceptStubs();

        when(visualRegressionConfiguration.isEnabled()).thenReturn(true);
        when(Files.notExists(referencePath)).thenReturn(false);

        when(visualRegressionConfiguration.getSnapshots()).thenReturn(snapshots);
        when(snapshots.isOverride()).thenReturn(true);

        assertTrue(consumer.shouldAccept(event));

        // super
        assertEquals(referencePath, Reflections.getFieldValue("referencePath", consumer));
    }

    @Test
    @DisplayName("accept should just delegate to generateAndAddScreenshotFrom")
    void accept() {
        // addScreenshot
        final int maxRetries = 1;
        final int count = 2;

        Reflections.setField("screenshot", consumer, screenshot);
        when(contextManager.getScreenshots()).thenReturn(screenshots);

        // runChecks
        Reflections.setField("screenshot", consumer, screenshot);
        when(visualRegressionConfiguration.getChecks()).thenReturn(checks);
        when(checks.getInterval()).thenReturn(interval);
        when(checks.getMaxRetries()).thenReturn(maxRetries);
        when(checks.getCount()).thenReturn(count);
        when(store.get(ORIGINAL_DRIVER, WebDriver.class)).thenReturn(driver);
        when(((TakesScreenshot) driver).getScreenshotAs(BYTES)).thenReturn(screenshot2);
        when(fileUtils.compare(eq(screenshot), byteArrayArgumentCaptor.capture())).thenReturn(true);

        consumer.accept(event);

        // addScreenshot
        verify(screenshots).put(referencePath.toString(), screenshot);
        verify(fileUtils).write(referencePath, screenshot);

        // runChecks
        assertArrayEquals(screenshot2, byteArrayArgumentCaptor.getAllValues().getFirst());
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
}
