package io.github.giulong.spectrum.utils.events.video;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.utils.TestData;

import java.nio.file.Path;

@JsonView(Internal.class)
public class VideoDynamicFinalizer extends VideoFinalizer {

    @Override
    protected Path getVideoPathFrom(final TestData testData) {
        return testData.getDynamicVideoPath();
    }
}
