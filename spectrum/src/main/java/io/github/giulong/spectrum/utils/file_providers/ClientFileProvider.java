package io.github.giulong.spectrum.utils.file_providers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.InjectableValues;

import io.github.giulong.spectrum.internals.jackson.views.Views;
import io.github.giulong.spectrum.internals.jackson.views.Views.Client;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public final class ClientFileProvider implements FileProvider {

    private static final Path RESOURCES = Path.of("src", "test", "resources");
    private static final List<String> EXTENSIONS = List.of(".yaml", ".yml");

    @Override
    public Class<? extends Views> getViews() {
        return Client.class;
    }

    @Override
    public InjectableValues getInjectableValues() {
        return new InjectableValues.Std()
                .addValue("enabledFromClient", true);
    }

    @Override
    public String find(final String file) {
        final List<Path> paths = findValidPathsFor(file);

        if (paths
                .stream()
                .peek(f -> log.debug("Checking if file {} exists", f))
                .noneMatch(Files::exists)) {
            log.warn("File {} not found.", file);
            return null;
        }

        final String fileWithExtension = findTheFirstValidFileFrom(paths);
        final Path directory = Path.of(file).getParent();

        return directory != null
                ? directory.resolve(fileWithExtension).toString()
                : fileWithExtension;
    }

    List<Path> findValidPathsFor(final String file) {
        return Stream
                .concat(Stream.of(file), EXTENSIONS
                        .stream()
                        .map(e -> String.format("%s%s", file, e)))
                .map(RESOURCES::resolve)
                .toList();
    }

    String findTheFirstValidFileFrom(final List<Path> paths) {
        return paths
                .stream()
                .peek(f -> log.debug("Looking for file {}", f))
                .filter(Files::exists)
                .peek(f -> log.debug("Found file {}", f))
                .findFirst()
                .orElseThrow()
                .getFileName()
                .toString();
    }
}
