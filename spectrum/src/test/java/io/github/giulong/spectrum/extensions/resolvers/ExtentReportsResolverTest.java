package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.utils.ExtentReporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.Function;

import static io.github.giulong.spectrum.extensions.resolvers.ExtentReportsResolver.EXTENT_REPORTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class ExtentReportsResolverTest {

    @MockSingleton
    @SuppressWarnings("unused")
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

    @Test
    @DisplayName("resolveParameter should return the ExtentReports from SpectrumSessionListener")
    void resolveParameter() {
        when(extentReporter.getExtentReports()).thenReturn(extentReports);

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);

        extentReportsResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).computeIfAbsent(eq(EXTENT_REPORTS), functionArgumentCaptor.capture(), eq(ExtentReports.class));
        Function<String, ExtentReports> function = functionArgumentCaptor.getValue();
        final ExtentReports actual = function.apply("value");

        assertEquals(extentReports, actual);
    }
}
