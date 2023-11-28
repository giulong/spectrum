package io.github.giulong.spectrum.types;

import lombok.Builder;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@Builder
public class TestData {
    private String className;
    private String methodName;
    private Path screenshotFolderPath;
    private Path videoPath;
}
