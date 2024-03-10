package io.github.giulong.spectrum.extensions.resolvers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActionsResolver")
class ActionsResolverTest {

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private ActionsResolver actionsResolver;

    @Test
    @DisplayName("resolveParameter should return an instance of Actions on the current stored WebDriver")
    public void testResolveParameter() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(DRIVER, WebDriver.class)).thenReturn(webDriver);

        MockedConstruction<Actions> mockedConstruction = mockConstruction(Actions.class);
        Actions actual = actionsResolver.resolveParameter(parameterContext, extensionContext);
        Actions actions = mockedConstruction.constructed().getFirst();
        verify(store).put(ActionsResolver.ACTIONS, actions);
        assertEquals(actions, actual);

        mockedConstruction.close();
    }
}
