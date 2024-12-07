package io.github.giulong.spectrum.utils.file_providers;

public interface FileProvider {
    Class<?> getViews();

    String find(String file);
}
