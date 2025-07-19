package io.github.giulong.spectrum.pojos;

import lombok.Builder;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@Builder
public class Screenshot {

    public static final String SCREENSHOT = "screenshot";

    private String name;
    private Path path;
    private byte[] data;
}
