package io.github.giulong.spectrum.utils.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.enums.Frame;
import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
public class Video {

    @JsonPropertyDescription("Kind of frames to be added to the video. By default, nothing is recorded")
    @SuppressWarnings("unused")
    private List<Frame> frames;

    @JsonPropertyDescription("width of the video. A value of 0 means the actual browser size will be used")
    @SuppressWarnings("unused")
    private int width;

    @JsonPropertyDescription("height of the video. A value of 0 means the actual browser size will be used")
    @SuppressWarnings("unused")
    private int height;

    @JsonPropertyDescription("Browser's menu bars height. When using the browser size (with height: 0), this is used to reduce the screenshots size to avoid stretching them")
    @SuppressWarnings("unused")
    private int menuBarsHeight;

    @JsonPropertyDescription("If true, consecutive duplicate frames are not included in the generated video")
    @SuppressWarnings("unused")
    private boolean skipDuplicateFrames;

    @JsonPropertyDescription("Properties of the video tag inside the extent report")
    @SuppressWarnings("unused")
    private ExtentTest extentTest;

    @JsonIgnore
    @SuppressWarnings("unused")
    private int frameNumber;

    public boolean isDisabled() {
        return frames.isEmpty();
    }

    public boolean shouldRecord(final Frame frame) {
        return frames.contains(frame);
    }

    public int getFrameNumberFor(final Frame frame) {
        log.trace("Current frame number: {}", frameNumber);
        return shouldRecord(frame) ? frameNumber++ : -1;
    }

    public void resetFrameNumber() {
        frameNumber = 0;
    }

    @Getter
    @Generated
    public static class ExtentTest {

        @JsonPropertyDescription("Whether to attach the video or not to the extent report. True by default")
        @SuppressWarnings("unused")
        private boolean attach;

        @JsonPropertyDescription("width of the video in the extent report")
        @SuppressWarnings("unused")
        private int width;

        @JsonPropertyDescription("height of the video in the extent report")
        @SuppressWarnings("unused")
        private int height;
    }
}
