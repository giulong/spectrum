package io.github.giulong.spectrum.utils.events.video;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Path;

import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@JsonView(Internal.class)
public class VideoDynamicConsumer extends VideoConsumer {

    @Override
    protected boolean shouldAccept(final Event event) {
        final boolean superCondition = super.shouldAccept(event);
        if (!superCondition) {
            return false;
        }

        final ExtensionContext context = event.getContext();
        if (context == null) {
            return false;
        }

        final TestData testData = context.getStore(GLOBAL).get(TEST_DATA, TestData.class);

        return testData != null && testData.isDynamic();
    }

    @Override
    protected Path getVideoPathFrom(final TestData testData) {
        return testData.getDynamicVideoPath();
    }

    @Override
    protected boolean isNewFrame(final byte[] screenshot, final TestData testData) {
        final String displayName = testData.getDisplayName();

        if (!displayName.equals(testData.getLastFrameDisplayName())) {
            testData.setLastFrameDisplayName(displayName);
            return true;
        }

        return super.isNewFrame(screenshot, testData);
    }
}
