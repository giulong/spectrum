package io.github.giulong.spectrum.utils.file_providers;

import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public final class InternalFileProvider implements FileProvider {

    @Override
    public Class<?> getViews() {
        return Internal.class;
    }

    @Override
    public String find(final String file) {
        log.debug("Returning internal file '{}'", file);
        return file;
    }
}
