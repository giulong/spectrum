package io.github.giulong.spectrum.pojos.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.giulong.spectrum.enums.Result;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Map;
import java.util.Set;

@Getter
@Builder
@ToString
@Jacksonized
public class Event {

    private String primaryId;
    private String secondaryId;
    private Set<String> tags;
    private String reason;
    private Result result;

    @JsonIgnore
    @ToString.Exclude
    private ExtensionContext context;

    @JsonIgnore
    @ToString.Exclude
    private Map<String, Object> payload;
}
