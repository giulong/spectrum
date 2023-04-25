package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.client.Data;
import com.giuliolongfils.agitation.config.YamlParser;
import com.giuliolongfils.agitation.config.YamlWriter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class DataResolver extends TypeBasedParameterResolver<Data> {

	private final Data data;

	@SneakyThrows
	public DataResolver() {
		log.debug("Parsing Data");
		data = YamlParser.getInstance().read("data/data.yaml", Data.class);

		log.trace("Data:\n{}", YamlWriter.getInstance().write(data));
	}

	@Override
	public Data resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
		return data;
	}
}
