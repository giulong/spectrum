package io.github.giulong.spectrum.it.tests;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.concurrent.ExecutorService;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.generation.Recording;
import io.github.giulong.spectrum.it.pages.CheckboxPage;
import io.github.giulong.spectrum.it.pages.LandingPage;
import io.github.giulong.spectrum.utils.FileUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.ScriptKey;

@Slf4j
@DisplayName("Recording")
class RecordingIT extends SpectrumTest<Void> {

    private final FileUtils fileUtils = FileUtils.getInstance();

    @SuppressWarnings("unused")
    private LandingPage landingPage;

    @SuppressWarnings("unused")
    private CheckboxPage checkboxPage;

    @BeforeEach
    void beforeEach() {
        System.setProperty("destination", "../it-generated/src/test/java");
        System.setProperty("fqdn", "io.github.giulong.spectrum.it_generated.tests.GeneratedIT.java");
        System.setProperty("args", "--headless=new");
    }

    @AfterEach
    void afterEach() {
        System.clearProperty("destination");
        System.clearProperty("fqdn");
        System.clearProperty("args");
    }

    @Test
    void record() {
        try (ExecutorService service = newSingleThreadExecutor()) {
            service.submit(() -> Recording.main(null));

            final ScriptKey scriptKey = javascriptExecutor.pin(fileUtils.read("js/interceptor.js"));
            final int port = getRecordingServerPort();

            driver.get("https://the-internet.herokuapp.com/");

            javascriptExecutor.executeScript(scriptKey, port);
            landingPage.getCheckboxLink().click();

            javascriptExecutor.executeScript(scriptKey, port);
            checkboxPage.getCheckboxes().getFirst().click();
            checkboxPage.getCheckboxes().get(1).click();
            driver.navigate().back();

            driver.navigate().forward();

            Recording.getInstance().getDriver().quit();
            service.shutdown();
        }
    }

    @SneakyThrows
    @SuppressWarnings("BusyWait")
    int getRecordingServerPort() {
        while (getServerPort() == 0) {
            log.info("Sleeping .5s waiting for recording server port to be available");
            Thread.sleep(500);
        }

        final int port = getServerPort();
        log.info("Recording server port is: {}", port);
        return port;
    }

    int getServerPort() {
        final Recording recording = Recording.getInstance();
        return recording == null ? 0 : recording.getServer().getHttpServer().getAddress().getPort();
    }
}
