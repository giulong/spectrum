package io.github.giulong.spectrum.it_grid.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.giulong.spectrum.SpectrumTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;

@DisplayName("Proxy Test")
class ProxyIT extends SpectrumTest<Void> {

    @Test
    @DisplayName("should prove that connections towards domains in the proxy bypass list are allowed, while others are not reachable")
    void proxyShouldAllowOnlyCertainDomains() {
        driver.get("https://the-internet.herokuapp.com");
        assertThrows(WebDriverException.class, () -> driver.get("https://www.google.com"));
    }
}
