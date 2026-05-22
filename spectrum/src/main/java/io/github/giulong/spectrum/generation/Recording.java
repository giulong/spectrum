package io.github.giulong.spectrum.generation;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpServer;

import io.github.giulong.spectrum.SpectrumSessionListener;
import io.github.giulong.spectrum.generation.generators.SpectrumTestGenerator;
import io.github.giulong.spectrum.generation.server.ActionHandler;
import io.github.giulong.spectrum.generation.server.Server;
import io.github.giulong.spectrum.generation.server.actions.Action;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.FileUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ScriptKey;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.ResponseDetails;

@Slf4j
@Getter
@Builder
public class Recording {

    @Getter
    private static Recording instance;

    private final FileUtils fileUtils = FileUtils.getInstance();
    private final Configuration configuration = Configuration.getInstance();
    private final Pattern fqdnPattern = Pattern.compile("^(?<package>[\\w$.]*\\.)(?<class>[\\w$.]+\\.java)$");

    private List<Action> actions;
    private Server server;
    private WebDriver driver;
    private Path packagePath;
    private String className;
    private boolean driverClosed;

    Recording parseProperties() {
        log.debug("Parse properties");

        final String fqdn = configuration.getRecording().getFqdn();
        final Matcher matcher = fqdnPattern.matcher(fqdn);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Fqdn '" + fqdn + "' is not a valid fully qualified class name!");
        }

        this.packagePath = Path.of(matcher.group("package").replace(".", File.separator));
        this.className = matcher.group("class");

        return this;
    }

    Recording buildDriver() {
        log.debug("Building Driver");

        this.driver = configuration.getRuntime()
                .getDriver()
                .buildCapabilities()
                .mergeCapabilitiesWith(configuration.getDrivers())
                .mergeCapabilitiesWith(configuration.getRecording().getDrivers())
                .build();

        return this;
    }

    Recording setup() {
        log.debug("Setup");
        server.start();

        return this;
    }

    Recording record() {
        log.debug("Recording");

        final JavascriptExecutor js = (JavascriptExecutor) driver;
        final ScriptKey scriptKey = js.pin(fileUtils.read("js/interceptor.js"));
        final Thread driverChecker = buildDriverChecker();
        final int port = server.getHttpServer().getAddress().getPort();

        try (Network network = new Network(driver)) {
            network.onResponseCompleted(r -> {
                if (isNavigation(r)) {
                    log.debug("Injecting interceptor.js into page '{}'", r.getResponseData().getUrl());
                    js.executeScript(scriptKey, port);
                }
            });

            driverChecker.start();
            driverChecker.join();
        } catch (final InterruptedException | WebDriverException ignored) {
            Thread.currentThread().interrupt();
        }

        log.debug("Recording done");
        return this;
    }

    @SuppressWarnings("BusyWait")
    Thread buildDriverChecker() {
        return new Thread(() -> {
            do {
                try {
                    driver.getCurrentUrl();
                    Thread.sleep(50);
                } catch (final InterruptedException | WebDriverException ignored) {
                    log.debug("Driver is unreachable");
                    driverClosed = true;
                    Thread.currentThread().interrupt();
                }
            } while (!Thread.interrupted());
        });
    }

    boolean isNavigation(final ResponseDetails details) {
        return details.getNavigationId() != null
                && "GET".equals(details.getRequest().getMethod())
                && "text/html".equals(details.getResponseData().getMimeType().split(";")[0]);
    }

    Recording tearDown() {
        log.debug("Tear down");
        server.stop();

        return this;
    }

    Recording generate() {
        log.debug("Generate");

        SpectrumTestGenerator
                .builder()
                .actions(actions)
                .destination(Path.of(configuration.getRecording().getDestination()))
                .packagePath(packagePath)
                .className(className)
                .build()
                .generate();

        return this;
    }

    void afterAll() {
        log.debug("After all");

        if (!driverClosed) {
            driver.quit();
        }
    }

    @SneakyThrows
    public static void main(final String[] args) {
        final List<Action> actions = new ArrayList<>();

        if (args == null || args.length == 0) {
            new SpectrumSessionListener()
                    .redirectJulToSlf4j()
                    .parseConfig()
                    .parseConfiguration();
        }

        instance = Recording
                .builder()
                .actions(actions)
                .server(Server
                        .builder()
                        .actions(actions)
                        .handler(new ActionHandler(actions))
                        .httpServer(HttpServer.create(new InetSocketAddress(0), 0))
                        .build())
                .build();

        instance
                .parseProperties()
                .buildDriver()
                .setup()
                .record()
                .tearDown()
                .generate()
                .afterAll();
    }
}
