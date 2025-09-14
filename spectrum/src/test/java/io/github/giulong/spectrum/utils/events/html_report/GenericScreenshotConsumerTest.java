package io.github.giulong.spectrum.utils.events.html_report;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;

import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenericScreenshotConsumerTest {

    @Mock
    private Event event;

    @Mock
    private TestData testData;

    @Mock
    private Map<String, Object> payload;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @InjectMocks
    private GenericScreenshotConsumer consumer;

    @Test
    @DisplayName("accept should do the common operations for the provided screenshot")
    void accept() {
        when(event.getPayload()).thenReturn(payload);
        when(payload.get(EXTENSION_CONTEXT)).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);

        consumer.accept(event);

        verify(testData).incrementScreenshotNumber();
    }
}
