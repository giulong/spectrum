package io.github.giulong.spectrum.utils.video;

import io.github.giulong.spectrum.enums.Frame;
import lombok.Getter;

import java.util.List;

@SuppressWarnings("unused")
@Getter
public class Video {

    private List<Frame> frames;
    private int width;
    private int height;

    public boolean isDisabled() {
        return frames.isEmpty();
    }

    public boolean shouldRecord(final String frameName) {
        return frames
                .stream()
                .map(Frame::getValue)
                .anyMatch(frameName::startsWith);
    }
}
