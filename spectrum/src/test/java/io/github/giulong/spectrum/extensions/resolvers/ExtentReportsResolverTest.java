package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import io.github.giulong.spectrum.utils.ExtentReporter;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;

import java.util.function.Function;

import static io.github.giulong.spectrum.extensions.resolvers.ExtentReportsResolver.EXTENT_REPORTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class ExtentReportsResolverTest {

    private static MockedStatic<ExtentReporter> extentReporterMockedStatic;

    @Mock
    private ExtentReporter extentReporter;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private ExtentReports extentReports;

    @Captor
    private ArgumentCaptor<Function<String, ExtentReports>> functionArgumentCaptor;

    @InjectMocks
    private ExtentReportsResolver extentReportsResolver;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("extentReporter", extentReportsResolver, extentReporter);
        extentReporterMockedStatic = mockStatic(ExtentReporter.class);
    }

    @AfterEach
    void afterEach() {
        extentReporterMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return the ExtentReports from SpectrumSessionListener")
    void resolveParameter() {
        when(extentReporter.getExtentReports()).thenReturn(extentReports);

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);

        extentReportsResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).getOrComputeIfAbsent(eq(EXTENT_REPORTS), functionArgumentCaptor.capture(), eq(ExtentReports.class));
        Function<String, ExtentReports> function = functionArgumentCaptor.getValue();
        final ExtentReports actual = function.apply("value");

        assertEquals(extentReports, actual);
    }
}
