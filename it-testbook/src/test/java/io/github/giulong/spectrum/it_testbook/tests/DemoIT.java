package io.github.giulong.spectrum.it_testbook.tests;

import io.github.giulong.spectrum.SpectrumTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Demo test")
class DemoIT extends SpectrumTest<Void> {

    @Test
    @DisplayName("Sending custom events")
    void events() {
        driver.get(configuration.getApplication().getBaseUrl());
        eventsDispatcher.fire("primaryId", "custom-event");
        eventsDispatcher.fire("primaryId", "secondReason");
    }

    @Test
    @DisplayName("This one should fail for demonstration purposes")
    void failing() {
        driver.get(configuration.getApplication().getBaseUrl());
        throw new RuntimeException("Exception thrown to demonstrate how failed tests will be displayed");
    }

    @Test
    @DisplayName("Skipped Test")
    @Disabled("for demonstration purposes")
    void skipped() {
        throw new RuntimeException("This should not be thrown!!!");
    }
}
