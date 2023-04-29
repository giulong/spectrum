package com.giuliolongfils.agitation.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.giuliolongfils.agitation.browsers.Browser;
import lombok.Getter;
import org.slf4j.event.Level;

@SuppressWarnings("unused")
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SystemProperties {

    @JsonProperty("spectrum.env")
    private String env;

    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("spectrum.browser")
    private Browser<?> browser;

    @JsonProperty("spectrum.grid")
    private boolean grid;

    @JsonProperty("spectrum.download.webdriver")
    private boolean downloadWebDriver;

    // JUnit properties
    @JsonProperty("junit.jupiter.execution.parallel.enabled")
    private boolean parallel;

    @JsonProperty("junit.jupiter.execution.parallel.mode.default")
    private String parallelMode;

    @JsonProperty("junit.jupiter.execution.parallel.mode.classes.default")
    private String parallelModeClasses;
    // End JUnit properties

    // Log-related properties are here just for completeness.
    // They can't be set via the default yaml though, since logback reads them immediately
    // and expects their values to be injected in the logback.xml
    @JsonProperty("spectrum.log.path")
    private String logFilePath;

    @JsonProperty("spectrum.log.level")
    private Level logLevel;

    @JsonProperty("spectrum.log.colors")
    private boolean logColors;
    // End log-related properties
}
