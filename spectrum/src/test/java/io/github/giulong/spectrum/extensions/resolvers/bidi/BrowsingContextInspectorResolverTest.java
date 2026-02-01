package io.github.giulong.spectrum.extensions.resolvers.bidi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.BrowsingContextInspector;

class BrowsingContextInspectorResolverTest {

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private BrowsingContextInspectorResolver browsingContextInspectorResolver;

    @Test
    @DisplayName("resolveParameterFor should return an instance of BrowsingContextInspector for the provided WebDriver")
    void resolveParameterFor() {
        final MockedConstruction<BrowsingContextInspector> mockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(webDriver, context.arguments().getFirst()));

        final BrowsingContextInspector actual = browsingContextInspectorResolver.resolveParameterFor(webDriver);

        assertEquals(mockedConstruction.constructed().getFirst(), actual);

        mockedConstruction.close();
    }
}
