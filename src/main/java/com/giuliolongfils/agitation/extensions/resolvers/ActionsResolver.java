package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.internal.ContextManager;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.interactions.Actions;

public class ActionsResolver extends TypeBasedParameterResolver<Actions> {

    public static final String ACTIONS = "actions";

    @Override
    public Actions resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        final ContextManager contextManager = ContextManager.getInstance();
        final Actions actions = new Actions(contextManager.getWebDriver(context));

        contextManager.store(ACTIONS, actions, context);
        return actions;
    }
}
