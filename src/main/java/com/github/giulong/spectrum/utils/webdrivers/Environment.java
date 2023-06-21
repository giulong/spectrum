package com.github.giulong.spectrum.utils.webdrivers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.pojos.Configuration;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @Type(value = LocalEnvironment.class, name = "local"),
        @Type(value = GridEnvironment.class, name = "grid"),
        @Type(value = DockerEnvironment.class, name = "docker"),
})
public abstract class Environment {
    public abstract void buildFrom(Configuration configuration, Browser<? extends MutableCapabilities> browser, RemoteWebDriverBuilder webDriverBuilder);
}
