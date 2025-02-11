package io.github.giulong.spectrum.it_bidi.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_bidi.pages.CheckboxPage;
import io.github.giulong.spectrum.it_bidi.pages.LandingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.AddInterceptParameters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.openqa.selenium.bidi.network.InterceptPhase.BEFORE_REQUEST_SENT;

@SuppressWarnings("unused")
@DisplayName("Checkbox Page")
public class BiDiCheckboxIT extends SpectrumTest<Void> {

    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    @Test
    public void testWithNoDisplayName() {
        try (Network network = new Network(driver)) {
            String intercept = network.addIntercept(new AddInterceptParameters(BEFORE_REQUEST_SENT));
            assertNotNull(intercept);
        }
    }
}
