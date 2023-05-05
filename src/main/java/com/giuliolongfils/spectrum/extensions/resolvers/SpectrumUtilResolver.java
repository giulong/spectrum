package com.giuliolongfils.spectrum.extensions.resolvers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.util.SpectrumUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class SpectrumUtilResolver extends TypeBasedParameterResolver<SpectrumUtil> {

	public static final String SPECTRUM_UTIL = "spectrumUtil";

	@Override
	public SpectrumUtil resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
		final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
		final SpectrumUtil spectrumUtil = SpectrumUtil.builder().configuration(rootStore.get(CONFIGURATION, Configuration.class)).build();
		rootStore.put(SPECTRUM_UTIL, spectrumUtil);

		// TODO
		//spectrumUtil.deleteDownloadsFolder();
		return spectrumUtil;
	}
}
