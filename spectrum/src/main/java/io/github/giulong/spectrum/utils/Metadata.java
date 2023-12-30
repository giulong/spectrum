package io.github.giulong.spectrum.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.giulong.spectrum.interfaces.SessionHook;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Queue;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
@Slf4j
@SuppressWarnings("unused")
public class Metadata implements SessionHook {

    public static final String FILE_NAME = "metadata.json";
    private static final Metadata INSTANCE = new Metadata();

    @JsonIgnore
    private final JsonUtils jsonUtils = JsonUtils.getInstance();

    @JsonIgnore
    private final FileUtils fileUtils = FileUtils.getInstance();

    private Execution execution;

    public static Metadata getInstance() {
        return INSTANCE;
    }

    @Override
    public void sessionClosedFrom(final Configuration configuration) {
        log.debug("Session closed hook");

        final Path path = Path.of(configuration.getRuntime().getCacheFolder()).resolve(FILE_NAME);
        fileUtils.write(path, jsonUtils.write(this));
    }

    public static class Execution {
        private Queue<String> successful;
    }
}
