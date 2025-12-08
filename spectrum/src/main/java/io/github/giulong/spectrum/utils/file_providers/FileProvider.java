package io.github.giulong.spectrum.utils.file_providers;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import io.github.giulong.spectrum.internals.jackson.views.Views;

public interface FileProvider {
    default ObjectReader augment(final ObjectMapper mapper) {
        return mapper
                .setInjectableValues(getInjectableValues())
                .reader()
                .withView(getViews());
    }

    Class<? extends Views> getViews();

    InjectableValues getInjectableValues();

    String find(String file);
}
