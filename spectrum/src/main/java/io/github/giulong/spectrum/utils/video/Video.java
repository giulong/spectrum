package io.github.giulong.spectrum.utils.video;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.interfaces.JsonSchemaTypes;
import io.github.giulong.spectrum.types.TestData;

import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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

    public boolean isDisabled() {
        return frames.isEmpty();
    }

    public boolean shouldRecord(final Frame frame) {
        return frames.contains(frame);
    }

    public int getAndIncrementFrameNumberFor(final TestData testData, final Frame frame) {
        final int frameNumber = testData.getFrameNumber();
        log.trace("Current frame number: {}", frameNumber);

        if (shouldRecord(frame)) {
            testData.setFrameNumber(frameNumber + 1);
            return frameNumber;
        }

        return -1;
    }

    @Getter
    @Generated
    public static class ExtentTest {

        @JsonPropertyDescription("Whether to attach the video or not to the extent report. True by default")
        @SuppressWarnings("unused")
        private boolean attach;

        @JsonPropertyDescription("width of the video in the extent report. Check https://developer.mozilla.org/en-US/docs/Web/CSS/width")
        @JsonSchemaTypes({String.class, int.class})
        @SuppressWarnings("unused")
        private String width;

        @JsonPropertyDescription("height of the video in the extent report. Check https://developer.mozilla.org/en-US/docs/Web/CSS/height")
        @JsonSchemaTypes({String.class, int.class})
        @SuppressWarnings("unused")
        private String height;
    }
}
