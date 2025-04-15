package io.github.giulong.spectrum.extensions.resolvers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ActionsResolver extends TypeBasedParameterResolver<Actions> {

    public static final String ACTIONS = "actions";

    @Override
    public Actions resolveParameter(final ParameterContext arg0, final ExtensionContext context) {
        log.debug("Resolving {}", ACTIONS);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final Actions actions = new Actions(store.get(DRIVER, WebDriver.class));
        store.put(ACTIONS, actions);
        return actions;
    }
}
