package com.giuliolongfils.agitation.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.giuliolongfils.agitation.browsers.Browser;
import lombok.Getter;
import org.slf4j.event.Level;

@SuppressWarnings("unused")
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SystemProperties {
	private String reportsFolder;
    private String reportName;
    private String env;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Browser<?> browser;
    private String downloadsFolder;
    private String filesFolder;
    private boolean grid;
    private String logFilePath;
    private Level logLevel;
    private boolean parallel;
    private String parallelMode;
    private String parallelModeClasses;
    private boolean downloadWebDriver;
    private boolean logColors;
}
