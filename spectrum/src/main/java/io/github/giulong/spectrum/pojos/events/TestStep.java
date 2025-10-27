package io.github.giulong.spectrum.pojos.events;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestStep {
    private LocalDateTime time;
    private String delta;
    private String message;
}
