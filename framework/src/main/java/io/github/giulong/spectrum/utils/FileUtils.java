package io.github.giulong.spectrum.utils;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class FileUtils {

    private static final FileUtils INSTANCE = new FileUtils();
    private static final String DEFAULT_TIMESTAMP_PATTERN = "dd-MM-yyyy_HH-mm-ss";
    private static final String TIMESTAMP_TO_REPLACE = "\\{timestamp:?(?<pattern>.*)}";
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile(".*\\{timestamp:(?<pattern>.*)}.*");

    public static FileUtils getInstance() {
        return INSTANCE;
    }

    public String read(final String file) {
        log.debug("Reading file {}", file);
        InputStream inputStream = FileUtils.class.getResourceAsStream(file);

        if (inputStream == null) {
            log.warn("File {} not found.", file);
            return "";
        }

        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    @SneakyThrows
    public Properties readProperties(final String file) {
        log.debug("Reading properties file {}", file);
        final Properties properties = new Properties();
        properties.load(FileUtils.class.getResourceAsStream(file));
        return properties;
    }

    public String interpolate(final String file, final Map<String, String> vars) {
        String source = read(file);
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            source = source.replace(entry.getKey(), entry.getValue());
        }

        return source;
    }

    public String interpolateTimestampFrom(final String fileName) {
        final Matcher matcher = TIMESTAMP_PATTERN.matcher(fileName);
        final String pattern = matcher.matches() ? matcher.group("pattern") : DEFAULT_TIMESTAMP_PATTERN;
        return fileName.replaceAll(TIMESTAMP_TO_REPLACE, LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)));
    }
}
