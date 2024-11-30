package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.pojos.events.TestStep;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.TEST_STEP_BUILDER_CONSUMER;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class TestStepsConsumerTest {

    private static MockedStatic<Path> pathMockedStatic;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private TestData testData;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private Path path;

    @Mock
    private TestStep testStep1;

    @Mock
    private TestStep testStep2;

    @Mock
    private TestStepBuilderConsumer testStepBuilderConsumer;

    @Mock
    private Event event;

    @InjectMocks
    private TestStepsConsumer testStepsConsumer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("fileUtils", testStepsConsumer, fileUtils);
        Reflections.setField("freeMarkerWrapper", testStepsConsumer, freeMarkerWrapper);

        pathMockedStatic = mockStatic(Path.class);
    }

    @AfterEach
    void afterEach() {
        pathMockedStatic.close();
    }

    @Test
    @DisplayName("accept should write the template with the data in the provided Event")
    void accept() {
        final String testId = "testId";
        final String extension = "extension";
        final String output = "target/spectrum/tests-steps";
        final String template = "templates/test-steps.txt";
        final String readTemplate = "readTemplate";
        final String interpolatedTemplate = "interpolatedTemplate";
        final String fileName = String.format("%s.%s", testId, extension);
        final List<TestStep> testSteps = List.of(testStep1, testStep2);

        when(event.getContext()).thenReturn(extensionContext);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(store.get(TEST_STEP_BUILDER_CONSUMER, TestStepBuilderConsumer.class)).thenReturn(testStepBuilderConsumer);
        when(testData.getTestId()).thenReturn(testId);
        when(fileUtils.getExtensionOf(template)).thenReturn(extension);
        when(Path.of(output, fileName)).thenReturn(path);
        when(fileUtils.read(template)).thenReturn(readTemplate);
        when(testStepBuilderConsumer.getTestSteps()).thenReturn(testSteps);
        when(freeMarkerWrapper.interpolate(readTemplate, Map.of("steps", testSteps))).thenReturn(interpolatedTemplate);

        testStepsConsumer.accept(event);

        verify(fileUtils).write(path, interpolatedTemplate);
    }
}
