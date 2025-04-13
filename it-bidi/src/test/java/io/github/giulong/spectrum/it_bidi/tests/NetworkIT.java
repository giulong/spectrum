package io.github.giulong.spectrum.it_bidi.tests;

import io.github.giulong.spectrum.SpectrumTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.bidi.network.AddInterceptParameters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.openqa.selenium.bidi.network.InterceptPhase.BEFORE_REQUEST_SENT;

public class NetworkIT extends SpectrumTest<Void> {

    @Test
    void canRemoveIntercept() {
        String intercept = network.addIntercept(new AddInterceptParameters(BEFORE_REQUEST_SENT));
        assertNotNull(intercept);
        network.removeIntercept(intercept);
    }
}
