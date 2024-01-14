package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static io.github.giulong.spectrum.enums.Result.FAILED;
import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestBookConsumer")
class TestBookConsumerTest {

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext parentContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private Configuration configuration;

    @Mock
    private TestBook testBook;

    @Mock
    private Event event;

    @InjectMocks
    private TestBookConsumer testBookConsumer;

    @Test
    @DisplayName("consume should tell the testbook to update")
    public void consume() {
        final Result result = FAILED;

        when(event.getContext()).thenReturn(context);
        when(event.getResult()).thenReturn(result);
        when(context.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getTestBook()).thenReturn(testBook);
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn("className");
        when(context.getDisplayName()).thenReturn("testName");

        testBookConsumer.consumes(event);

        verify(testBook).updateWithResult("className", "testName", result);
    }
}
