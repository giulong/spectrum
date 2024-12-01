package io.github.giulong.spectrum.pojos.events;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TestStep {
    private LocalDateTime time;
    private String delta;
    private String message;
}
