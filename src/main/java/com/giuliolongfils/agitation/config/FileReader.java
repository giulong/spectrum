package com.giuliolongfils.agitation.config;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

@Slf4j
@Builder
public class FileReader {

    public String read(final String file) {
        log.debug("Reading file {}", file);
        InputStream inputStream = FileReader.class.getResourceAsStream(file);

        if (inputStream == null) {
            log.warn("File {} not found.", file);
            return null;
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
}
