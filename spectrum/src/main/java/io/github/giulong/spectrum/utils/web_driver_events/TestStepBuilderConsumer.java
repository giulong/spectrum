package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.pojos.events.TestStep;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.MILLIS;

@SuperBuilder
public class TestStepBuilderConsumer extends WebDriverEventConsumer {

    @Getter
    private final List<TestStep> testSteps = new ArrayList<>();

    @Builder.Default
    private LocalDateTime lastTime = LocalDateTime.now();

    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        final LocalDateTime now = LocalDateTime.now();
        final Duration duration = Duration.ofMillis(lastTime.until(now, MILLIS));
        final String message = webDriverEvent.removeTagsFromMessage();
        final String delta = String.format("%d.%-3d", duration.toSecondsPart(), duration.toMillisPart()).replace(' ', '0');
        final TestStep testStep = TestStep
                .builder()
                .time(now)
                .delta(delta)
                .message(message)
                .build();

        testSteps.add(testStep);
        lastTime = now;
    }
}
