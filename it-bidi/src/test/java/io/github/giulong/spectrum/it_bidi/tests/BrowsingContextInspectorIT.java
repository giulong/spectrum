package io.github.giulong.spectrum.it_bidi.tests;

import io.github.giulong.spectrum.SpectrumTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.bidi.browsingcontext.NavigationInfo;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.bidi.browsingcontext.ReadinessState.COMPLETE;

public class BrowsingContextInspectorIT extends SpectrumTest<Void> {

    @Test
    @SneakyThrows
    void canListenToWindowBrowsingContextCreatedEvent() {
        final CompletableFuture<NavigationInfo> future = new CompletableFuture<>();

        browsingContextInspector.onDomContentLoaded(future::complete);
        browsingContext.navigate("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html", COMPLETE);

        final NavigationInfo navigationInfo = future.get(5, SECONDS);
        assertTrue(navigationInfo.getUrl().contains("bidi/logEntryAdded"));
    }
}
