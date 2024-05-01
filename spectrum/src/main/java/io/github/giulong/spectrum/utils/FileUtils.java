package io.github.giulong.spectrum.utils;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Comparator.reverseOrder;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class FileUtils {

    private static final FileUtils INSTANCE = new FileUtils();
    private static final String DEFAULT_TIMESTAMP_PATTERN = "dd-MM-yyyy_HH-mm-ss";
    private static final String TIMESTAMP_TO_REPLACE = "\\$\\{timestamp:?(?<pattern>.*)}";
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile(".*\\$\\{timestamp:(?<pattern>.*)}.*");

    public static FileUtils getInstance() {
        return INSTANCE;
    }

    public String read(final String file) {
        log.debug("Reading file {}", file);
        final InputStream inputStream = FileUtils.class.getResourceAsStream(file);

        if (inputStream == null) {
            log.debug("File {} not found.", file);
            return "";
        }

        try (Scanner scanner = new Scanner(inputStream)) {
            return scanner.useDelimiter("\\Z").next();
        }
    }

    public String readTemplate(final String file) {
        return read(String.format("/templates/%s", file));
    }

    public String interpolate(final String file, final Map<String, String> vars) {
        String source = read(file);
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            source = source.replace(entry.getKey(), entry.getValue());
        }

        return source;
    }

    public String interpolateTimestampFrom(final String value) {
        final Matcher matcher = TIMESTAMP_PATTERN.matcher(value);
        final String pattern = matcher.matches() ? matcher.group("pattern") : DEFAULT_TIMESTAMP_PATTERN;
        return value.replaceAll(TIMESTAMP_TO_REPLACE, LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)));
    }

    public String getExtensionOf(final String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public String removeExtensionFrom(final String fileName) {
        return fileName.replaceFirst("\\.[^.]*$", "");
    }

    @SneakyThrows
    public Path deleteDirectory(final Path directory) {
        if (!Files.exists(directory)) {
            log.debug("Avoid deleting non-existing directory '{}'", directory);
            return directory;
        }

        log.debug("About to delete directory '{}'", directory);

        try (Stream<Path> files = Files.walk(directory)) {
            files
                    .sorted(reverseOrder())
                    .map(Path::toFile)
                    .forEach(f -> log.trace("File '{}' deleted? {}", f, f.delete()));
        }

        return directory;
    }

    @SneakyThrows
    public Path deleteContentOf(final Path directory) {
        return Files.createDirectories(deleteDirectory(directory));
    }

    @SneakyThrows
    public void write(final Path path, final String content) {
        final boolean foldersCreated = path.getParent().toFile().mkdirs();
        log.trace("Folders created? {}. Writing {} to file {}", foldersCreated, content, path);

        Files.write(path, content.getBytes());
    }
}
