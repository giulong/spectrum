package io.github.giulong.spectrum.pojos.events;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class Attachment {

    @JsonPropertyDescription("Attachment name")
    private String name;

    @JsonPropertyDescription("File attached")
    private String file;
}
