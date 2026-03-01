package io.github.giulong.spectrum.generation;

import static org.openqa.selenium.bidi.network.InterceptPhase.RESPONSE_STARTED;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpServer;

import io.github.giulong.spectrum.generation.generators.SpectrumTestGenerator;
import io.github.giulong.spectrum.generation.server.ActionHandler;
import io.github.giulong.spectrum.generation.server.Server;
import io.github.giulong.spectrum.generation.server.actions.Action;
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
import org.openqa.selenium.bidi.network.AddInterceptParameters;
import org.openqa.selenium.bidi.network.ResponseDetails;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Slf4j
@Getter
@Builder
public class Recording {

    private final FileUtils fileUtils = FileUtils.getInstance();
    private final Pattern fqdnPattern = Pattern.compile("^(?<package>[\\w$.]*\\.)(?<class>[\\w$.]+\\.java)$");

    private List<Action> actions;
    private Server server;
    private WebDriver driver;
    private Path destination;
    private String fqdn;
    private Path packagePath;
    private String className;

    Recording parseProperties() {
        log.debug("Parse properties");

        this.fqdn = System.getProperty("fqdn", "it_generated.GeneratedIT.java");

        final Matcher matcher = fqdnPattern.matcher(fqdn);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Fqdn '" + fqdn + "' is not a valid fully qualified class name!");
        }

        this.destination = Path.of(System.getProperty("destination", "src/test/java"));
        this.packagePath = Path.of(matcher.group("package").replace(".", File.separator));
        this.className = matcher.group("class");

        log.trace("Fqdn: {}", fqdn);
        log.trace("Destination: {}", destination);
        log.trace("Package: {}", packagePath);
        log.trace("Class: {}", className);

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
            network.addIntercept(new AddInterceptParameters(RESPONSE_STARTED));
            network.onResponseCompleted(r -> {
                if (isNavigation(r)) {
                    final String url = r.getResponseData().getUrl();
                    if (r.getRedirectCount() == 0) {
                        server.addNavigationTo(url);
                    }

                    log.debug("Injecting interceptor.js into page '{}'", url);
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
                    Thread.sleep(1000);
                } catch (final InterruptedException | WebDriverException ignored) {
                    log.debug("Driver is closed");
                    Thread.currentThread().interrupt();
                }
            } while (!Thread.interrupted());
        });
    }

    boolean isNavigation(final ResponseDetails details) {
        return details.getNavigationId() != null
                && "GET".equals(details.getRequest().getMethod())
                && "text/html".equals(details.getResponseData().getMimeType());
    }

    Recording tearDown() {
        log.debug("Tear down");
        server.stop();

        return this;
    }

    void generate() {
        log.debug("Generate");

        SpectrumTestGenerator
                .builder()
                .actions(actions)
                .destination(destination)
                .packagePath(packagePath)
                .className(className)
                .build()
                .generate();
    }

    @SneakyThrows
    public static void main(final String[] args) {
        final List<Action> actions = new ArrayList<>();
        final ChromeOptions options = new ChromeOptions().addArguments("--disable-web-security");
        options.setCapability("webSocketUrl", true);

        Recording
                .builder()
                .actions(actions)
                .server(Server
                        .builder()
                        .actions(actions)
                        .handler(ActionHandler
                                .builder()
                                .actions(actions)
                                .build())
                        .httpServer(HttpServer.create(new InetSocketAddress(0), 0))
                        .build())
                .driver(new ChromeDriver(options))
                .build()
                .parseProperties()
                .setup()
                .record()
                .tearDown()
                .generate();
    }
}
