package io.github.giulong.spectrum.pojos.events;

import java.util.Set;

import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.giulong.spectrum.enums.Result;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.TakesScreenshot;

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
    private Payload payload;

    @Getter
    @Builder
    @EqualsAndHashCode
    public static class Payload {
        private String message;
        private Status status;
        private byte[] screenshot;
        private TakesScreenshot takesScreenshot;
    }
}
