package io.github.giulong.spectrum.utils.file_providers;

import io.github.giulong.spectrum.internals.jackson.views.Views;

import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

public interface FileProvider {
    default ObjectReader augment(final ObjectMapper mapper) {
        return mapper
                .reader(getInjectableValues())
                .withView(getViews());
    }

    Class<? extends Views> getViews();

    InjectableValues getInjectableValues();

    String find(String file);
}
