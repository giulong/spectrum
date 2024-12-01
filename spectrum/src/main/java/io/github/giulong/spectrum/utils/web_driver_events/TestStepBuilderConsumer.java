package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.pojos.events.TestStep;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.time.temporal.ChronoUnit.MILLIS;

@Builder
public class TestStepBuilderConsumer implements Consumer<WebDriverEvent> {

    private static final String TAG = "<.*?>";

    @Getter
    private final List<TestStep> testSteps = new ArrayList<>();

    @Builder.Default
    private LocalDateTime lastTime = LocalDateTime.now();

    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        final LocalDateTime now = LocalDateTime.now();
        final Duration duration = Duration.ofMillis(lastTime.until(now, MILLIS));
        final String message = webDriverEvent.getMessage().replaceAll(TAG, "");
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
