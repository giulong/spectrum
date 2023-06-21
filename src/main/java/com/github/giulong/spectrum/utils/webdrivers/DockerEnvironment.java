package com.github.giulong.spectrum.utils.webdrivers;

import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.pojos.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

@Slf4j
public class DockerEnvironment extends Environment {

    @Override
    public WebDriver buildFrom(final Configuration configuration, final Browser<? extends MutableCapabilities> browser) {
        log.info("Running in Docker");

        browser
                .getWebDriverManager()
                .browserInDocker()
                .setup();

        return browser.buildWebDriver();
    }
}
