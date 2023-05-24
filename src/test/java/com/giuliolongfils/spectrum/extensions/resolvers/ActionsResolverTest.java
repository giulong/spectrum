package com.giuliolongfils.spectrum.extensions.resolvers;

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

import static com.giuliolongfils.spectrum.extensions.resolvers.ActionsResolver.ACTIONS;
import static com.giuliolongfils.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
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
        when(store.get(WEB_DRIVER, WebDriver.class)).thenReturn(webDriver);

        MockedConstruction<Actions> mockedConstruction = mockConstruction(Actions.class);
        Actions actual = actionsResolver.resolveParameter(parameterContext, extensionContext);
        Actions actions = mockedConstruction.constructed().get(0);
        verify(store).put(ACTIONS, actions);
        assertEquals(actions, actual);

        mockedConstruction.close();
    }
}
