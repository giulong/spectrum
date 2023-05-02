package com.giuliolongfils.spectrum.extensions.resolvers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.util.SpectrumUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class SpectrumUtilResolver extends TypeBasedParameterResolver<SpectrumUtil> {

	@Getter
	private final SpectrumUtil spectrumUtil;

	public SpectrumUtilResolver(final Configuration configuration) {
		log.debug("Building SpectrumUtil");
		spectrumUtil = SpectrumUtil.builder().configuration(configuration).build();
		spectrumUtil.deleteDownloadsFolder();
	}

	@Override
	public SpectrumUtil resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
		return spectrumUtil;
	}
}
