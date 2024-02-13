package io.github.giulong.spectrum.utils.environments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.drivers.Appium;
import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.interfaces.JsonSchemaTypes;
import io.github.giulong.spectrum.internals.AppiumLog;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@SuppressWarnings("unused")
public class AppiumEnvironment extends Environment {

    @JsonIgnore
    private final Configuration configuration = Configuration.getInstance();

    @JsonIgnore
    private AppiumDriverLocalService driverService;

    @JsonSchemaTypes(String.class)
    @JsonPropertyDescription("Url of the appium server. By default, it's http://127.0.0.1:4723/")
    private URL url;

    @JsonPropertyDescription("IP address of the Appium Server. By default, it's 127.0.0.1")
    private String ipAddress;

    @JsonPropertyDescription("Specific port to bind the Appium Server. If not provided, the Appium's default 4723 is used")
    private int port;

    @JsonPropertyDescription("Capabilities specific of the Appium Server")
    private final Map<String, Object> capabilities = new HashMap<>();

    @Override
    public void sessionOpened() {
        final AppiumServiceBuilder appiumServiceBuilder = (AppiumServiceBuilder) configuration
                .getRuntime()
                .getDriver()
                .getDriverServiceBuilder();

        driverService = AppiumDriverLocalService.buildService(appiumServiceBuilder
                .withCapabilities(new DesiredCapabilities(capabilities))
                .withIPAddress(ipAddress)
                .usingPort(port)
        );

        driverService.clearOutPutStreams();
        driverService.addOutPutStream(AppiumLog
                .builder()
                .level(configuration.getWebDriver().getLogs().getLevel())
                .build());
        driverService.start();
    }

    @Override
    public void sessionClosed() {
        driverService.stop();
    }

    @Override
    public WebDriver setupFor(final Driver<?, ?, ?> driver) {
        log.info("Running with appium");

        return ((Appium<?, ?>) driver).buildDriverFor(url);
    }

    @Override
    public void shutdown() {
        driverService.close();
    }
}
