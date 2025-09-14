package io.github.giulong.spectrum.utils.events.html_report;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.events.EventsConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Map;

import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
@JsonView(Internal.class)
public class GenericScreenshotConsumer extends EventsConsumer {

    @Override
    public void accept(final Event event) {
        log.debug("Common screenshot operations");
        final Map<String, Object> payload = event.getPayload();
        final ExtensionContext.Store store = ((ExtensionContext) payload.get(EXTENSION_CONTEXT)).getStore(GLOBAL);

        store.get(TEST_DATA, TestData.class).incrementScreenshotNumber();
    }
}
