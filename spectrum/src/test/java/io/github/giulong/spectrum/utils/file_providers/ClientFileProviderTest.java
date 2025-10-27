package io.github.giulong.spectrum.utils.file_providers;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.InjectableValues;

import io.github.giulong.spectrum.internals.jackson.views.Views;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;

class ClientFileProviderTest {

    @InjectMocks
    private ClientFileProvider fileProvider;

    @Test
    @DisplayName("getViews should return Views.Client.class")
    void getViews() {
        assertEquals(Views.Client.class, fileProvider.getViews());
    }

    @Test
    @DisplayName("getInjectableValues should return the client injectables")
    void getInjectableValues() {
        final MockedConstruction<InjectableValues.Std> mockedConstruction = mockConstruction(InjectableValues.Std.class, (mock, context) ->
                when(mock.addValue("enabledFromClient", true)).thenReturn(mock));

        final InjectableValues actual = fileProvider.getInjectableValues();

        assertEquals(mockedConstruction.constructed().getFirst(), actual);

        mockedConstruction.close();
    }

    @Test
    @DisplayName("findFile should return null when the client file isn't found")
    void find() {
        assertNull(fileProvider.find("file"));
    }

    @Test
    @DisplayName("findFile should return the file with extension when it has no parent directory")
    void findFound() {
        final String file = "testbook";

        assertEquals("testbook.yaml", fileProvider.find(file));
    }

    @Test
    @DisplayName("findFile should return the file with extension and parent folder")
    void findFoundParent() {
        final String file = "data/data";

        assertEquals(Path.of("data", "data.yaml").toString(), fileProvider.find(file));
    }

    @DisplayName("findValidPathsFor should return the list of resources paths with valid extensions from the provided file")
    @ParameterizedTest(name = "with file {0} we expect {1}")
    @MethodSource("valuesProvider")
    void findValidPathsFor(final String file, final Stream<String> strings) {
        final List<Path> paths = strings
                .map(p -> Path.of("src", "test", "resources").resolve(p))
                .toList();

        assertEquals(paths, fileProvider.findValidPathsFor(file));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("file", Stream.of("file", "file.yaml", "file.yml")),
                arguments("folder/file", Stream.of("folder/file", "folder/file.yaml", "folder/file.yml"))
        );
    }

    @Test
    @DisplayName("findTheFirstValidFileFrom should return the first file that exists among the provided list")
    void findTheFirstValidFileFrom() throws IOException {
        final Path path = Files.createTempFile("prefix", "suffix");
        final List<Path> paths = List.of(Path.of("non existing"), path);

        path.toFile().deleteOnExit();

        assertEquals(path.getFileName().toString(), fileProvider.findTheFirstValidFileFrom(paths));
    }

    @Test
    @DisplayName("findTheFirstValidFileFrom should throw an exception if no file among the provided list exists")
    void findTheFirstValidFileFromThrows() {
        final List<Path> paths = List.of(Path.of("non existing"), Path.of("another non existing"));

        assertThrows(RuntimeException.class, () -> fileProvider.findTheFirstValidFileFrom(paths));
    }
}
