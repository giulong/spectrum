package io.github.giulong.spectrum.pojos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Screenshot {

    public static final String SCREENSHOT = "screenshot";

    private String name;
    private byte[] data;
}
