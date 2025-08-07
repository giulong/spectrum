package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static io.github.giulong.spectrum.enums.Result.FAILED;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestBookConsumerTest {

    @Mock
    private ExtensionContext context;

    @Mock
    private ContextManager contextManager;

    @Mock
    private Configuration configuration;

    @Mock
    private TestBook testBook;

    @Mock
    private TestData testData;

    @Mock
    private Event event;

    @InjectMocks
    private TestBookConsumer testBookConsumer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", testBookConsumer, configuration);
        Reflections.setField("contextManager", testBookConsumer, contextManager);
    }

    @Test
    @DisplayName("accept should tell the testbook to update")
    void accept() {
        final String displayName = "displayName";
        final String classDisplayName = "classDisplayName";
        final Result result = FAILED;

        when(contextManager.get(context, TEST_DATA, TestData.class)).thenReturn(testData);

        when(event.getContext()).thenReturn(context);
        when(event.getResult()).thenReturn(result);
        when(configuration.getTestBook()).thenReturn(testBook);
        when(testData.getClassDisplayName()).thenReturn(classDisplayName);
        when(testData.getDisplayName()).thenReturn(displayName);

        testBookConsumer.accept(event);

        verify(testBook).updateWithResult(classDisplayName, displayName, result);
    }
}
