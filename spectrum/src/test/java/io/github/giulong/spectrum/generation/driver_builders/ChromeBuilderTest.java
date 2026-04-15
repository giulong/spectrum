package io.github.giulong.spectrum.generation.driver_builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mockConstruction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

class ChromeBuilderTest {

    @Mock
    private ChromeOptions options;

    @InjectMocks
    private ChromeBuilder builder;

    @Test
    @DisplayName("getOptions should return an instance of ChromeOptions")
    void getOptions() {
        assertInstanceOf(ChromeOptions.class, builder.getOptions());
    }

    @Test
    @DisplayName("getDriver should return an instance of ChromeDriver with the provided ChromeOptions")
    void getDriver() {
        try (MockedConstruction<ChromeDriver> mockedConstruction = mockConstruction((mock, context) -> assertEquals(options, context.arguments().getFirst()))) {
            final ChromeDriver actual = builder.getDriver(options);

            assertEquals(mockedConstruction.constructed().getFirst(), actual);
        }
    }
}
