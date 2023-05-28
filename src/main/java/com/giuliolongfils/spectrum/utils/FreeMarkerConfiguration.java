package com.giuliolongfils.spectrum.utils;

import freemarker.template.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static freemarker.template.Configuration.VERSION_2_3_32;
import static java.util.Locale.US;

@Slf4j
@Getter
public final class FreeMarkerConfiguration {

    private static final FreeMarkerConfiguration INSTANCE = new FreeMarkerConfiguration();

    private final Configuration configuration;

    private FreeMarkerConfiguration() {
        log.debug("Configuring FreeMarker");
        this.configuration = new Configuration(VERSION_2_3_32);
        this.configuration.setLocale(US);
        this.configuration.setNumberFormat("0.##;; roundingMode=halfUp");
    }

    public static FreeMarkerConfiguration getInstance() {
        return INSTANCE;
    }
}
