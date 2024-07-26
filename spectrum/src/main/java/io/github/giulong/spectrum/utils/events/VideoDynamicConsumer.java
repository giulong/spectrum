package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.types.TestData;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonView(Internal.class)
public class VideoDynamicConsumer extends VideoConsumer {

    private static final Pattern PATTERN = Pattern.compile("[A-Za-z]+-(?<displayName>.*)-([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\\.png");

    @Override
    protected Path getVideoPathFrom(final TestData testData) {
        return testData.getDynamicVideoPath();
    }

    @Override
    protected boolean filter(final File file, final TestData testData) {
        final Matcher matcher = PATTERN.matcher(file.getName());

        return matcher.find() && testData.getDisplayName().equals(matcher.group("displayName"));
    }
}
