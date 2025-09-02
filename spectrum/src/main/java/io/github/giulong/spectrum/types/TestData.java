package io.github.giulong.spectrum.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jcodec.api.awt.AWTSequenceEncoder;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class TestData {

    private String className;
    private String methodName;
    private String classDisplayName;
    private String testId;
    private Path videoPath;

    @Builder.Default
    private Map<Path, AWTSequenceEncoder> encoders = new HashMap<>();

    @Setter
    private int frameNumber;

    @Setter
    private boolean dynamic;

    @Setter
    private byte[] lastFrameDigest;

    @Setter
    private String lastFrameDisplayName;

    @Setter
    private Path dynamicVideoPath;

    @Setter
    private String displayName;
}
