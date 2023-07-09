package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import io.github.giulong.spectrum.SpectrumSessionListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static io.github.giulong.spectrum.extensions.resolvers.ExtentReportsResolver.EXTENT_REPORTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtentReportsResolver")
class ExtentReportsResolverTest {

    private static MockedStatic<SpectrumSessionListener> spectrumSessionListenerMockedStatic;

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

    @Captor
    private ArgumentCaptor<ExtentReports> extentReportsArgumentCaptor;

    @InjectMocks
    private ExtentReportsResolver extentReportsResolver;

    @BeforeEach
    public void beforeEach() {
        spectrumSessionListenerMockedStatic = mockStatic(SpectrumSessionListener.class);
    }

    @AfterEach
    public void afterEach() {
        spectrumSessionListenerMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return the ExtentReports from SpectrumSessionListener")
    public void resolveParameter() {
        when(SpectrumSessionListener.getExtentReports()).thenReturn(extentReports);

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);

        extentReportsResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).getOrComputeIfAbsent(eq(EXTENT_REPORTS), functionArgumentCaptor.capture(), eq(ExtentReports.class));
        Function<String, ExtentReports> function = functionArgumentCaptor.getValue();
        final ExtentReports actual = function.apply("value");

        verify(rootStore).put(eq(EXTENT_REPORTS), extentReportsArgumentCaptor.capture());
        assertEquals(extentReports, actual);
    }
}
