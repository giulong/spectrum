package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import com.giuliolongfils.agitation.util.AgitationUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class AgitationUtilResolver extends TypeBasedParameterResolver<AgitationUtil> {

	@Getter
	private final AgitationUtil agitationUtil;

	public AgitationUtilResolver(final SystemProperties systemProperties, final Configuration configuration) {
		log.debug("Building AgitationUtil");
		agitationUtil = AgitationUtil.builder()
				.systemProperties(systemProperties)
				.application(configuration.getApplication())
				.extent(configuration.getExtent())
				.build();
		agitationUtil.deleteDownloadsFolder();
	}

	@Override
	public AgitationUtil resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
		return agitationUtil;
	}
}
