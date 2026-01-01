package io.github.giulong.spectrum.utils.web_driver_events;

import static io.github.giulong.spectrum.enums.Result.*;
import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.TEST_STEP_BUILDER_CONSUMER;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.pojos.events.TestStep;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.TestData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class TestStepsConsumerTest {

    private static MockedStatic<Path> pathMockedStatic;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private TestData testData;

    @MockFinal
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @MockFinal
    @SuppressWarnings("unused")
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
        pathMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        pathMockedStatic.close();
    }

    @DisplayName("shouldAccept should check if the test is disabled")
    @ParameterizedTest(name = "with result {0} we expect {1}")
    @MethodSource("valuesProvider")
    void shouldAccept(final Result result, final boolean expected) {
        when(event.getResult()).thenReturn(result);

        assertEquals(expected, testStepsConsumer.shouldAccept(event));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(NOT_RUN, true),
                arguments(SUCCESSFUL, true),
                arguments(FAILED, true),
                arguments(ABORTED, true),
                arguments(DISABLED, false));
    }

    @Test
    @DisplayName("accept should write the template with the data in the provided Event")
    void accept() {
        final String testId = "testId";
        final String extension = "extension";
        final String output = "target/spectrum/tests-steps";
        final String template = "test-steps.txt";
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
        when(testStepBuilderConsumer.getTestSteps()).thenReturn(testSteps);
        when(freeMarkerWrapper.interpolateTemplate(template, Map.of("steps", testSteps))).thenReturn(interpolatedTemplate);

        testStepsConsumer.accept(event);

        verify(fileUtils).write(path, interpolatedTemplate);
    }
}
