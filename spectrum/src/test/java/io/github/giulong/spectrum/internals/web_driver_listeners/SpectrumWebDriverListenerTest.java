package io.github.giulong.spectrum.internals.web_driver_listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import lombok.experimental.SuperBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.WebElement;

class SpectrumWebDriverListenerTest {

    @Mock
    private WebElement webElement;

    @Mock
    private Pattern locatorPattern;

    @Mock
    private Matcher matcher;

    @InjectMocks
    private SpectrumWebDriverListener spectrumWebDriverListener = new DummyWebDriverListener(DummyWebDriverListener.builder());

    @DisplayName("extractSelectorFrom should extract just the relevant info from the webElement")
    @ParameterizedTest(name = "with WebElement {0} we expect {1}")
    @MethodSource("valuesProvider")
    void extractSelectorFrom(final String fullWebElement, final String expected) {
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(expected);
        assertEquals(expected, spectrumWebDriverListener.extractSelectorFrom(webElement));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> id: message]",
                        "id: message"),
                arguments("[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #gettotal]] -> tag name: button]",
                        "css selector: #gettotal -> tag name: button"),
                arguments("[[[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> css selector: #get1-.total]] -> tag name: button]",
                        "css selector: #get1-.total -> tag name: button"));
    }

    @Test
    @DisplayName("extractSelectorFrom should extract just the relevant info from the webElement")
    void extractSelectorFromNoMatch() {
        final String fullWebElement = "fullWebElement";

        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(false);
        assertEquals("", spectrumWebDriverListener.extractSelectorFrom(webElement));
    }

    @SuperBuilder
    private static final class DummyWebDriverListener extends SpectrumWebDriverListener {
    }
}
