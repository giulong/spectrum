package io.github.giulong.spectrum.extensions.resolvers.bidi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.LogInspector;

class LogInspectorResolverTest {

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private LogInspectorResolver logInspectorResolver;

    @Test
    @DisplayName("resolveParameterFor should return an instance of LogInspector for the provided WebDriver")
    void resolveParameterFor() {
        final MockedConstruction<LogInspector> mockedConstruction = mockConstruction(LogInspector.class,
                (mock, context) -> assertEquals(webDriver, context.arguments().getFirst()));

        final LogInspector actual = logInspectorResolver.resolveParameterFor(webDriver);

        assertEquals(mockedConstruction.constructed().getFirst(), actual);

        mockedConstruction.close();
    }
}
