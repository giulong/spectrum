package integration_tests.tests;

import com.github.giulong.spectrum.SpectrumTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Demo test")
public class DemoIT extends SpectrumTest<Void> {

    @Test
    @DisplayName("Sending custom events")
    public void events() {
        //eventsDispatcher.dispatch(getClass().getSimpleName(), "events", CLIENT, Set.of(TEST));
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