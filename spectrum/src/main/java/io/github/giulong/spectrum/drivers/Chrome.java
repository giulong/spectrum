package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Drivers;

import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeDriverService.Builder;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.service.DriverService;

public class Chrome extends Chromium<ChromeOptions, ChromeDriverService, ChromeDriverService.Builder> {

    @Override
    public DriverService.Builder<ChromeDriverService, ChromeDriverService.Builder> getDriverServiceBuilder() {
        final Configuration.Drivers.Chrome.Service service = configuration.getDrivers().getChrome().getService();

        return new ChromeDriverService.Builder()
                .withBuildCheckDisabled(service.isBuildCheckDisabled())
                .withAppendLog(service.isAppendLog())
                .withReadableTimestamp(service.isReadableTimestamp())
                .withLogLevel(service.getLogLevel())
                .withSilent(service.isSilent())
                .withVerbose(service.isVerbose())
                .withAllowedListIps(service.getAllowedListIps());
    }

    @Override
    public Chrome buildCapabilities() {
        capabilities = new ChromeOptions();

        return this;
    }

    @Override
    public Driver<ChromeOptions, ChromeDriverService, Builder> mergeCapabilitiesWith(final Drivers drivers) {
        final Configuration.Drivers.Chrome chrome = drivers.getChrome();

        capabilities.addArguments(chrome.getArgs());

        chrome.getCapabilities().forEach(capabilities::setCapability);
        chrome.getExperimentalOptions().forEach(capabilities::setExperimentalOption);
        setLoggingPreferencesFrom(drivers.getLogs());
        activateBiDi(capabilities, drivers, chrome);

        return this;
    }
}
