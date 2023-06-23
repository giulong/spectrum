package com.github.giulong.spectrum.utils.webdrivers;

import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.pojos.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;

@Slf4j
public class DockerEnvironment extends Environment {

    @Override
    public void buildFrom(final Configuration configuration, final Browser<? extends MutableCapabilities> browser, final RemoteWebDriverBuilder webDriverBuilder) {
        log.info("Running in Docker");
        browser.getWebDriverManager().browserInDocker().create();
    }
}
