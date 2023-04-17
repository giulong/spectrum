package com.giuliolongfils.agitation.extensions.resolvers;

import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.agitation.internal.ContextManager;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

public class ExtentTestResolver extends TypeBasedParameterResolver<ExtentTest> {

	public static final String EXTENT_TEST = "extentTest";

	@Override
	public ExtentTest resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
		return ContextManager.getInstance().getExtentTest(context);
	}
}
