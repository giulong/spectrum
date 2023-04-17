package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.internal.ContextManager;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

public class SystemPropertiesResolver extends TypeBasedParameterResolver<SystemProperties> {

	public static final String SYSTEM_PROPERTIES = "systemProperties";

	@Override
	public SystemProperties resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
		return ContextManager.getInstance().getSystemProperties(context);
	}
}
