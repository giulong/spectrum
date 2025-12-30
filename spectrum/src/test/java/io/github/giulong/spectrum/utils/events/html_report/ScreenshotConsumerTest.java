package io.github.giulong.spectrum.utils.events.html_report;

import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Map;

import com.aventstack.extentreports.ExtentTest;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ScreenshotConsumerTest {

    private final byte[] screenshot = new byte[]{1, 2, 3};

    @MockSingleton
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private StatefulExtentTest statefulExtentTest;

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
    private Map<String, byte[]> screenshots;

    @Mock
    private ExtentTest currentNode;

    @Mock
    private Event event;

    @Mock
    private Map<String, Object> payload;

    @Mock
    private TestData testData;

    @InjectMocks
    private DummyScreenshotConsumer consumer;

    @Test
    @DisplayName("shouldAccept should set a bunch of state variables")
    void shouldAccept() {
        when(event.getPayload()).thenReturn(payload);
        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)).thenReturn(statefulExtentTest);
        when(statefulExtentTest.getCurrentNode()).thenReturn(currentNode);

        assertTrue(consumer.shouldAccept(event));
    }

    @Test
    @DisplayName("addScreenshot should generate the screenshot")
    void addScreenshot() {
        Reflections.setField("screenshot", consumer, screenshot);

        when(contextManager.getScreenshots()).thenReturn(screenshots);

        consumer.addScreenshot(referencePath);

        verify(screenshots).put(referencePath.toString(), screenshot);
        verify(fileUtils).write(referencePath, screenshot);
    }

    private static final class DummyScreenshotConsumer extends ScreenshotConsumer {

        @Override
        public void accept(Event event) {
        }
    }
}
