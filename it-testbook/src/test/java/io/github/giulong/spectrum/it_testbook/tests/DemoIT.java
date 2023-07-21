package io.github.giulong.spectrum.it_testbook.tests;

import io.github.giulong.spectrum.SpectrumTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Demo test")
public class DemoIT extends SpectrumTest<Void> {

    @Test
    @DisplayName("Sending custom events")
    public void events() {
        webDriver.get(configuration.getApplication().getBaseUrl());
        eventsDispatcher.fire("primaryId", "custom-event");
        eventsDispatcher.fire("primaryId", "secondReason");
    }

    @Test
    @DisplayName("This one should fail for demonstration purposes")
    public void failing() {
        webDriver.get(configuration.getApplication().getBaseUrl());
        throw new RuntimeException("Exception thrown to demonstrate how failed tests will be displayed");
    }

    @Test
    @DisplayName("Skipped Test")
    @Disabled("for demonstration purposes")
    public void skipped() {
        // this one will be skipped
    }
}
