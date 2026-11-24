package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;

import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.service.DriverService;

public class InternetExplorer extends Driver<InternetExplorerOptions, InternetExplorerDriverService, InternetExplorerDriverService.Builder> {

    @Override
    public DriverService.Builder<InternetExplorerDriverService, InternetExplorerDriverService.Builder> getDriverServiceBuilder() {
        final Configuration.Drivers.Ie.Service service = configuration.getDrivers().getIe().getService();

        return new InternetExplorerDriverService.Builder()
                .withLogLevel(service.getLogLevel())
                .withSilent(service.isSilent())
                .withHost(service.getHost())
                .withExtractPath(service.getExtractPath());
    }

    @Override
    void buildCapabilities() {
        final Configuration.Drivers.Ie ie = configuration.getDrivers().getIe();

        capabilities = new InternetExplorerOptions().attachToEdgeChrome();

        ie.getCapabilities().forEach(capabilities::setCapability);
        capabilities.addCommandSwitches(ie.getArgs().toArray(new String[0]));
    }
}
