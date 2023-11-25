package io.github.giulong.spectrum.utils.video;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.enums.Frame;
import lombok.Generated;
import lombok.Getter;

import java.util.List;

@SuppressWarnings("unused")
@Getter
public class Video {

    @JsonPropertyDescription("Kind of frames to be added to the video. By default, nothing is recorded")
    private List<Frame> frames;

    @JsonPropertyDescription("width of the video. A value of 0 means the actual browser size will be used")
    private int width;

    @JsonPropertyDescription("height of the video. A value of 0 means the actual browser size will be used")
    private int height;

    @JsonPropertyDescription("Properties of the video tag inside the extent report")
    private ExtentTest extentTest;

    public boolean isDisabled() {
        return frames.isEmpty();
    }

    public boolean shouldRecord(final String frameName) {
        return frames
                .stream()
                .map(Frame::getValue)
                .anyMatch(frameName::startsWith);
    }

    @Getter
    @Generated
    public static class ExtentTest {

        @JsonPropertyDescription("Whether to attach the video or not to the extent report. True by default")
        private boolean attach;

        @JsonPropertyDescription("width of the video in the extent report")
        private int width;

        @JsonPropertyDescription("height of the video in the extent report")
        private int height;
    }
}
