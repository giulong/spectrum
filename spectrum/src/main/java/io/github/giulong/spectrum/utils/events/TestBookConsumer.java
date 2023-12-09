package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import org.junit.jupiter.api.extension.ExtensionContext;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@JsonView(Internal.class)
public class TestBookConsumer extends EventsConsumer {

    public void consumes(final Event event) {
        final ExtensionContext context = event.getContext();
        final TestBook testBook = context.getRoot().getStore(GLOBAL).get(CONFIGURATION, Configuration.class).getTestBook();
        final String className = context.getParent().orElseThrow().getDisplayName();
        final String testName = context.getDisplayName();

        testBook.updateWithResult(className, testName, event.getResult());
    }
}
