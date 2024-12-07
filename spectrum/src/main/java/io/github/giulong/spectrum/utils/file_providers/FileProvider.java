package io.github.giulong.spectrum.utils.file_providers;

import io.github.giulong.spectrum.internals.jackson.views.Views;

public interface FileProvider {
    Class<? extends Views> getViews();

    String find(String file);
}
