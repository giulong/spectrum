package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.ExtentReporter;
import io.github.giulong.spectrum.utils.ReflectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtentTestConsumer")
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
    public void beforeEach() {
        ReflectionUtils.setField("extentReporter", extentTestConsumer, extentReporter);
        extentReporterMockedStatic = mockStatic(ExtentReporter.class);
    }

    @AfterEach
    public void afterEach() {
        extentReporterMockedStatic.close();
    }

    @Test
    @DisplayName("consumes should add a log in the extent report by default")
    public void consumes() {
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(event.getContext()).thenReturn(context);

        extentTestConsumer.consumes(event);

        verify(extentReporter).logTestEnd(context, SUCCESSFUL.getStatus());
    }
}
