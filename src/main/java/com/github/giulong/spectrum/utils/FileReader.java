package com.github.giulong.spectrum.utils;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class FileReader {

    private static final FileReader INSTANCE = new FileReader();

    public static FileReader getInstance() {
        return INSTANCE;
    }

    public String read(final String file) {
        log.debug("Reading file {}", file);
        InputStream inputStream = FileReader.class.getResourceAsStream(file);

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
        properties.load(FileReader.class.getResourceAsStream(file));
        return properties;
    }

    public String interpolate(final String file, final Map<String, String> vars) {
        String source = read(file);
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            source = source.replace(entry.getKey(), entry.getValue());
        }

        return source;
    }
}
