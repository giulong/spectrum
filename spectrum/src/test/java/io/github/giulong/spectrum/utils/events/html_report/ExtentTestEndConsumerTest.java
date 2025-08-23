package io.github.giulong.spectrum.utils.events.html_report;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.ExtentReporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExtentTestEndConsumerTest {

    @MockSingleton
    @SuppressWarnings("unused")
    private ExtentReporter extentReporter;

    @Mock
    private ExtensionContext context;

    @Mock
    private Event event;

    @InjectMocks
    private ExtentTestEndConsumer extentTestEndConsumer;

    @Test
    @DisplayName("accept should add a log in the extent report by default")
    void accept() {
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(event.getContext()).thenReturn(context);

        extentTestEndConsumer.accept(event);

        verify(extentReporter).logTestEnd(context, SUCCESSFUL.getStatus());
    }
}
