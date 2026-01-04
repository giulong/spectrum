package io.github.giulong.spectrum.utils.events.html_report;

import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import com.fasterxml.jackson.annotation.JsonView;

import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.events.EventsConsumer;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
@JsonView(Internal.class)
public class GenericScreenshotConsumer extends EventsConsumer {

    @Override
    public void accept(final Event event) {
        log.debug("Common screenshot operations");
        final ExtensionContext.Store store = event.getContext().getStore(GLOBAL);

        store.get(TEST_DATA, TestData.class).incrementScreenshotNumber();
    }
}
