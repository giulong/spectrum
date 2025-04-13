package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.service.DriverService;

public class Edge extends Chromium<EdgeOptions, EdgeDriverService, EdgeDriverService.Builder> {

    @Override
    public DriverService.Builder<EdgeDriverService, EdgeDriverService.Builder> getDriverServiceBuilder() {
        final Configuration.Drivers.Chrome.Service service = configuration.getDrivers().getEdge().getService();

        return new EdgeDriverService.Builder()
                .withBuildCheckDisabled(service.isBuildCheckDisabled())
                .withAppendLog(service.isAppendLog())
                .withReadableTimestamp(service.isReadableTimestamp())
                .withLoglevel(service.getLogLevel())
                .withSilent(service.isSilent())
                .withVerbose(service.isVerbose())
                .withAllowedListIps(service.getAllowedListIps());
    }

    @Override
    void buildCapabilities() {
        final Configuration.Drivers drivers = configuration.getDrivers();
        final Configuration.Drivers.Edge edge = drivers.getEdge();

        capabilities = new EdgeOptions().addArguments(edge.getArgs());

        edge.getCapabilities().forEach(capabilities::setCapability);
        edge.getExperimentalOptions().forEach(capabilities::setExperimentalOption);
        setLoggingPreferencesFrom(drivers.getLogs());
        activateBiDi(capabilities, configuration, edge);
    }
}
