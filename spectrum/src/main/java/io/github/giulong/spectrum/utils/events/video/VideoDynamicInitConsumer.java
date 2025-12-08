package io.github.giulong.spectrum.utils.events.video;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonView;

import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.utils.TestData;

@JsonView(Internal.class)
public class VideoDynamicInitConsumer extends VideoInitConsumer {

    @Override
    protected Path getVideoPathFrom(final TestData testData) {
        return testData.getDynamicVideoPath();
    }
}
