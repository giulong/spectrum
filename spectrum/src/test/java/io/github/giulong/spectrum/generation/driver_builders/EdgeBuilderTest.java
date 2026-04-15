package io.github.giulong.spectrum.generation.driver_builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mockConstruction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

class EdgeBuilderTest {

    @Mock
    private EdgeOptions options;

    @InjectMocks
    private EdgeBuilder builder;

    @Test
    @DisplayName("getOptions should return an instance of EdgeOptions")
    void getOptions() {
        assertInstanceOf(EdgeOptions.class, builder.getOptions());
    }

    @Test
    @DisplayName("getDriver should return an instance of EdgeDriver with the provided EdgeOptions")
    void getDriver() {
        try (MockedConstruction<EdgeDriver> mockedConstruction = mockConstruction((mock, context) -> assertEquals(options, context.arguments().getFirst()))) {
            final EdgeDriver actual = builder.getDriver(options);

            assertEquals(mockedConstruction.constructed().getFirst(), actual);
        }
    }
}
