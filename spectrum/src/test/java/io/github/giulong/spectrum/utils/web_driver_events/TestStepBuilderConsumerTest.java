package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.pojos.events.TestStep;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class TestStepBuilderConsumerTest {

    private MockedStatic<LocalDateTime> localDateTimeMockedStatic;
    private MockedStatic<Duration> durationMockedStatic;
    private MockedStatic<TestStep> testStepMockedStatic;

    @Mock
    private List<TestStep> testSteps;

    @Mock
    private TestStep.TestStepBuilder testStepBuilder;

    @Mock
    private TestStep testStep;

    @Mock
    private WebDriverEvent webDriverEvent;

    @Mock
    private LocalDateTime now;

    @Mock
    private LocalDateTime lastTime;

    @Mock
    private Duration duration;

    @InjectMocks
    private TestStepBuilderConsumer testStepBuilderConsumer = new TestStepBuilderConsumer(TestStepBuilderConsumer.builder());

    @BeforeEach
    void beforeEach() {
        localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
        durationMockedStatic = mockStatic(Duration.class);
        testStepMockedStatic = mockStatic(TestStep.class);
    }

    @AfterEach
    void afterEach() {
        localDateTimeMockedStatic.close();
        durationMockedStatic.close();
        testStepMockedStatic.close();
    }

    @DisplayName("accept should build a TestStep from the provided WebDriverEvent, add it to the list, and assign the new lastTime")
    @ParameterizedTest(name = "with millisPart {0} we expect the zeros-right-padded delta millis part {1}")
    @MethodSource("valuesProvider")
    void accept(final int millisPart, final String paddedMillisPart) {
        final String removeTagsFromMessage = "removeTagsFromMessage";
        final long lastTimeDelta = 123L;
        final int secondsPart = 456;
        final String delta = "456." + paddedMillisPart;

        Reflections.setField("testSteps", testStepBuilderConsumer, testSteps);
        Reflections.setField("lastTime", testStepBuilderConsumer, lastTime);

        when(LocalDateTime.now()).thenReturn(now);
        when(lastTime.until(now, MILLIS)).thenReturn(lastTimeDelta);
        when(Duration.ofMillis(lastTimeDelta)).thenReturn(duration);
        when(duration.toSecondsPart()).thenReturn(secondsPart);
        when(duration.toMillisPart()).thenReturn(millisPart);
        when(webDriverEvent.removeTagsFromMessage()).thenReturn(removeTagsFromMessage);

        when(TestStep.builder()).thenReturn(testStepBuilder);
        when(testStepBuilder.time(now)).thenReturn(testStepBuilder);
        when(testStepBuilder.delta(delta)).thenReturn(testStepBuilder);
        when(testStepBuilder.message(removeTagsFromMessage)).thenReturn(testStepBuilder);
        when(testStepBuilder.build()).thenReturn(testStep);

        testStepBuilderConsumer.accept(webDriverEvent);

        assertEquals(now, Reflections.getFieldValue("lastTime", testStepBuilderConsumer));
        verify(testSteps).add(testStep);
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(789, "789"),
                arguments(7, "700")
        );
    }
}
