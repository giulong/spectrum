package io.github.giulong.spectrum.extensions.resolvers.bidi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.Network;

class NetworkResolverTest {

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private NetworkResolver networkResolver;

    @Test
    @DisplayName("resolveParameterFor should return an instance of Network for the provided WebDriver")
    void resolveParameterFor() {
        final MockedConstruction<Network> mockedConstruction = mockConstruction(Network.class,
                (mock, context) -> assertEquals(webDriver, context.arguments().getFirst()));

        final Network actual = networkResolver.resolveParameterFor(webDriver);

        assertEquals(mockedConstruction.constructed().getFirst(), actual);

        mockedConstruction.close();
    }
}
