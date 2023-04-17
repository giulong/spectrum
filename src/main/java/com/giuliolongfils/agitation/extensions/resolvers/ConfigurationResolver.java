package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.internal.ContextManager;
import com.giuliolongfils.agitation.pojos.Configuration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

public class ConfigurationResolver extends TypeBasedParameterResolver<Configuration> {

	public static final String CONFIGURATION = "configuration";

	@Override
	public Configuration resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
		return ContextManager.getInstance().getConfiguration(context);
	}
}
