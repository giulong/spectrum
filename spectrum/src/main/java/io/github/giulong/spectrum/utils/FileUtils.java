package io.github.giulong.spectrum.utils;

import static java.util.Comparator.reverseOrder;
import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class FileUtils {

    private static final FileUtils INSTANCE = new FileUtils();
    public static final String HASH_ALGORITHM = "SHA-256";
    private static final String DEFAULT_TIMESTAMP_PATTERN = "dd-MM-yyyy_HH-mm-ss";
    private static final String TIMESTAMP_TO_REPLACE = "\\$\\{timestamp:?(?<pattern>.*)}";
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile(".*\\$\\{timestamp:(?<pattern>.*)}.*");
    private static final int[] ILLEGAL_CHARS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 34, 42, 47,
            58, 60, 62, 63, 92, 124};

    public static FileUtils getInstance() {
        return INSTANCE;
    }

    public String read(final String file) {
        return getInputStreamOf(file, inputStream -> {
            if (inputStream == null) {
                return "";
            }

            try (Scanner scanner = new Scanner(inputStream)) {
                return scanner.useDelimiter("\\Z").next();
            }
        });
    }

    public String readTemplate(final String file) {
        return read(String.format("templates/%s", file));
    }

    public byte[] readBytesOf(final String file) {
        return getInputStreamOf(file, inputStream -> {
            if (inputStream == null) {
                return new byte[]{};
            }

            return readAllBytesOf(inputStream);
        });
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
    public Path delete(final Path path) {
        if (Files.notExists(path)) {
            log.debug("Avoid deleting non-existing path '{}'", path);
            return path;
        }

        log.debug("About to delete path '{}'", path);

        try (Stream<Path> files = Files.walk(path)) {
            files
                    .sorted(reverseOrder())
                    .map(Path::toFile)
                    .forEach(f -> log.trace("File '{}' deleted? {}", f, f.delete()));
        }

        return path;
    }

    public Path delete(final File file) {
        return delete(file.toPath());
    }

    @SneakyThrows
    public Path deleteContentOf(final Path directory) {
        return Files.createDirectories(delete(directory));
    }

    @SneakyThrows
    public void write(final Path path, final byte[] content) {
        final boolean foldersCreated = path.getParent().toFile().mkdirs();
        log.trace("Folders created? {}. Writing {} to file {}", foldersCreated, content, path);

        Files.write(path, content);
    }

    public void write(final Path path, final String content) {
        write(path, content.getBytes());
    }

    public void write(final String path, final String content) {
        write(Path.of(path), content.getBytes());
    }

    public String sanitize(final String name) {
        final StringBuilder stringBuilder = new StringBuilder();
        final int charLength = name.codePointCount(0, name.length());

        for (int i = 0; i < charLength; i++) {
            final int c = name.codePointAt(i);

            if (Arrays.binarySearch(ILLEGAL_CHARS, c) < 0) {
                stringBuilder.appendCodePoint(c);
            }
        }

        return stringBuilder.toString();
    }

    @SneakyThrows
    public FileTime getCreationTimeOf(final File file) {
        return Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime();
    }

    @SneakyThrows
    public Path createTempFile(final String prefix, final String suffix) {
        final Path path = Files.createTempFile(prefix, suffix);
        path.toFile().deleteOnExit();

        log.debug("Created file {}", path);
        return path;
    }

    public String getScreenshotNameFrom(final TestData testData) {
        return String.format("screenshot-%d.png", testData.getScreenshotNumber());
    }

    public String getFailedScreenshotNameFrom(final TestData testData) {
        return String.format("screenshot-%d-failed.png", testData.getScreenshotNumber());
    }

    @SneakyThrows
    public byte[] checksumOf(final byte[] bytes) {
        final byte[] digest = MessageDigest.getInstance(HASH_ALGORITHM).digest(bytes);

        log.trace("{} is '{}'", HASH_ALGORITHM, Arrays.toString(digest));
        return digest;
    }

    public boolean compare(final byte[] bytes1, final byte[] bytes2) {
        return Arrays.equals(checksumOf(bytes1), checksumOf(bytes2));
    }

    @SneakyThrows
    public boolean compare(final Path path1, final Path path2) {
        log.info("""
                 Checking if these files are the same:
                 {}
                 {}
                 """, path1, path2);

        return compare(Files.readAllBytes(path1), Files.readAllBytes(path2));
    }

    @SneakyThrows
    public boolean compare(final Path path, final byte[] bytes) {
        return compare(Files.readAllBytes(path), bytes);
    }

    @SneakyThrows
    <T> T getInputStreamOf(final String file, final Function<InputStream, T> function) {
        log.debug("Reading file {}", file);

        try (InputStream inputStream = FileUtils.class.getResourceAsStream(String.format("/%s", file))) {
            if (inputStream == null) {
                log.debug("File {} not found.", file);
            }

            return function.apply(inputStream);
        }
    }

    @SneakyThrows
    byte[] readAllBytesOf(final InputStream inputStream) {
        return inputStream.readAllBytes();
    }
}
