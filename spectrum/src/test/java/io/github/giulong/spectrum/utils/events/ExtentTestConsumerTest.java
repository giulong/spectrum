package io.github.giulong.spectrum.utils.events;

import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static org.mockito.Mockito.*;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.ExtentReporter;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class ExtentTestConsumerTest {

    private static MockedStatic<ExtentReporter> extentReporterMockedStatic;

    @Mock
    private ExtentReporter extentReporter;

    @Mock
    private ExtensionContext context;

    @Mock
    private Event event;

    @InjectMocks
    private ExtentTestConsumer extentTestConsumer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("extentReporter", extentTestConsumer, extentReporter);
        extentReporterMockedStatic = mockStatic(ExtentReporter.class);
    }

    @AfterEach
    void afterEach() {
        extentReporterMockedStatic.close();
    }

    @Test
    @DisplayName("accept should add a log in the extent report by default")
    void accept() {
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(event.getContext()).thenReturn(context);

        extentTestConsumer.accept(event);

        verify(extentReporter).logTestEnd(context, SUCCESSFUL.getStatus());
    }
}
