package io.github.giulong.spectrum.extensions.resolvers.bidi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;

class BrowsingContextResolverTest {

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private BrowsingContextResolver browsingContextResolver;

    @Test
    @DisplayName("resolveParameterFor should return an instance of BrowsingContext for the provided WebDriver")
    void resolveParameterFor() {
        final String windowHandle = "windowHandle";
        when(webDriver.getWindowHandle()).thenReturn(windowHandle);

        final MockedConstruction<BrowsingContext> mockedConstruction = mockConstruction(BrowsingContext.class, (mock, context) -> {
            assertEquals(webDriver, context.arguments().getFirst());
            assertEquals(windowHandle, context.arguments().get(1));
        });

        final BrowsingContext actual = browsingContextResolver.resolveParameterFor(webDriver);

        assertEquals(mockedConstruction.constructed().getFirst(), actual);

        mockedConstruction.close();
    }
}
