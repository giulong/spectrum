package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.client.Data;
import com.giuliolongfils.agitation.internal.ContextManager;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

public class DataResolver extends TypeBasedParameterResolver<Data> {

	public static final String DATA = "data";

	@Override
	public Data resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
		return ContextManager.getInstance().getData(context);
	}
}
