package io.github.giulong.spectrum;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpectrumPageTest {

    private final String endpoint = "/endpoint";

    @Mock
    private static Configuration configuration;

    @Mock
    private Configuration.Application application;

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private DummySpectrumPage<?> spectrumPage;

    @Test
    @DisplayName("open should get the configured base url and wait for the page to be loaded")
    public void open() {
        final String url = "url";
        Reflections.setField("endpoint", spectrumPage, endpoint);

        when(configuration.getApplication()).thenReturn(application);
        when(application.getBaseUrl()).thenReturn(url);

        assertEquals(spectrumPage, spectrumPage.open());
        verify(webDriver).get(url + endpoint);
    }

    @Test
    @DisplayName("waitForPageLoading should do nothing but return the page instance")
    public void waitForPageLoading() {
        assertEquals(spectrumPage, spectrumPage.waitForPageLoading());
    }

    @DisplayName("isLoaded should check if the current page url matches the endpoint")
    @ParameterizedTest(name = "with page url {0} and current url {1} we expect {2}")
    @MethodSource("valuesProvider")
    public void isLoaded(final String pageUrl, final String currentUrl, final boolean expected) {
        Reflections.setField("endpoint", spectrumPage, endpoint);

        when(webDriver.getCurrentUrl()).thenReturn(currentUrl + endpoint);
        when(configuration.getApplication()).thenReturn(application);
        when(application.getBaseUrl()).thenReturn(pageUrl);

        assertEquals(expected, spectrumPage.isLoaded());
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("current", "current", true),
                arguments("page", "current", false)
        );
    }

    private static class DummySpectrumPage<T> extends SpectrumPage<DummySpectrumPage<T>, T> {

        public DummySpectrumPage() {
            configuration = SpectrumPageTest.configuration;
        }
    }
}
