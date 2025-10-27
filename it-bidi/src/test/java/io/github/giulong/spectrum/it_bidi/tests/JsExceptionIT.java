package io.github.giulong.spectrum.it_bidi.tests;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.bidi.log.LogLevel.ERROR;

import java.util.concurrent.CompletableFuture;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_bidi.pages.JavascriptErrorPage;

import lombok.SneakyThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.bidi.log.JavascriptLogEntry;

@SuppressWarnings("unused")
@DisplayName("Js Exception")
public class JsExceptionIT extends SpectrumTest<Void> {

    private JavascriptErrorPage javascriptErrorPage;

    @Test
    @SneakyThrows
    public void onload() {
        final CompletableFuture<JavascriptLogEntry> future = new CompletableFuture<>();
        logInspector.onJavaScriptLog(future::complete);

        javascriptErrorPage.open();

        final JavascriptLogEntry logEntry = future.get(5, SECONDS);

        assertTrue(logEntry.getText().startsWith("TypeError: "));
        assertEquals("javascript", logEntry.getType());
        assertEquals(ERROR, logEntry.getLevel());
        assertEquals(2, logEntry.getStackTrace().getCallFrames().size());
    }

    @Test
    @SneakyThrows
    void testListenToJavascriptLog() {
        final CompletableFuture<JavascriptLogEntry> future = new CompletableFuture<>();
        logInspector.onJavaScriptLog(future::complete);

        driver.get("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        driver.findElement(By.id("jsException")).click();

        final JavascriptLogEntry logEntry = future.get(5, SECONDS);

        assertEquals("Error: Not working", logEntry.getText());
        assertEquals("javascript", logEntry.getType());
        assertEquals(ERROR, logEntry.getLevel());
    }

    @Test
    @SneakyThrows
    void testListenToLogsWithMultipleConsumers() {
        final CompletableFuture<JavascriptLogEntry> completableFuture1 = new CompletableFuture<>();
        logInspector.onJavaScriptLog(completableFuture1::complete);

        final CompletableFuture<JavascriptLogEntry> completableFuture2 = new CompletableFuture<>();
        logInspector.onJavaScriptLog(completableFuture2::complete);

        driver.get("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        driver.findElement(By.id("jsException")).click();

        JavascriptLogEntry logEntry = completableFuture1.get(5, SECONDS);

        assertEquals("Error: Not working", logEntry.getText());
        assertEquals("javascript", logEntry.getType());

        logEntry = completableFuture2.get(5, SECONDS);

        assertEquals("Error: Not working", logEntry.getText());
        assertEquals("javascript", logEntry.getType());
    }
}
