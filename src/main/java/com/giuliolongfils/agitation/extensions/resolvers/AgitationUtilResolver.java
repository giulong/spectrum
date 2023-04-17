package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.internal.ContextManager;
import com.giuliolongfils.agitation.util.AgitationUtil;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

public class AgitationUtilResolver extends TypeBasedParameterResolver<AgitationUtil> {

	public static final String AGITATION_UTIL = "agitationUtil";

	@Override
	public AgitationUtil resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
		return ContextManager.getInstance().getAgitationUtil(context);
	}
}
