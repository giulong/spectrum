package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.testbook.TestBook;

import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;

@JsonView(Internal.class)
public class TestBookConsumer extends EventsConsumer {

    private final Configuration configuration = Configuration.getInstance();
    private final ContextManager contextManager = ContextManager.getInstance();

    @Override
    public void accept(final Event event) {
        final TestBook testBook = configuration.getTestBook();
        final TestData testData = contextManager.get(event.getContext().getUniqueId()).get(TEST_DATA, TestData.class);

        testBook.updateWithResult(testData.getClassDisplayName(), testData.getMethodDisplayName(), event.getResult());
    }
}
