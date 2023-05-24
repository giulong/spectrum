package com.giuliolongfils.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ExtentSparkReporterConfig;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.utils.FileReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.giuliolongfils.spectrum.extensions.resolvers.ExtentReportsResolver.EXTENT_REPORTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtentReportsResolver")
class ExtentReportsResolverTest {

    private static MockedStatic<FileReader> fileReaderMockedStatic;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Extent extent;

    @Mock
    private ExtentSparkReporterConfig extentSparkReporterConfig;

    @Mock
    private FileReader fileReader;

    @Captor
    private ArgumentCaptor<Function<String, ExtentReports>> functionArgumentCaptor;

    @Captor
    private ArgumentCaptor<ExtentReports> extentReportsArgumentCaptor;

    @InjectMocks
    private ExtentReportsResolver extentReportsResolver;

    @BeforeEach
    public void beforeEach() {
        fileReaderMockedStatic = mockStatic(FileReader.class);
    }

    @AfterEach
    public void afterEach() {
        fileReaderMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should initialize and return the ExtentReports by reading it config")
    public void resolveParameter() {
        final String reportFolder = "reportFolder";
        final String fileName = "fileName";
        final String reportName = "reportName";
        final String documentTitle = "documentTitle";
        final String theme = "DARK";
        final String timeStampFormat = "timeStampFormat";
        final String css = "css";

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(reportFolder);
        when(extent.getFileName()).thenReturn(fileName);
        when(extent.getReportName()).thenReturn(reportName);
        when(extent.getDocumentTitle()).thenReturn(documentTitle);
        when(extent.getTheme()).thenReturn(theme);
        when(extent.getTimeStampFormat()).thenReturn(timeStampFormat);
        when(FileReader.getInstance()).thenReturn(fileReader);
        when(fileReader.read("/css/report.css")).thenReturn(css);

        extentReportsResolver.resolveParameter(parameterContext, extensionContext);

        MockedConstruction<ExtentReports> extentReportsMockedConstruction = mockConstruction(ExtentReports.class);
        MockedConstruction<ExtentSparkReporter> extentSparkReporterMockedConstruction = mockConstruction(ExtentSparkReporter.class, (mock, context) -> {
            assertEquals(Paths.get(System.getProperty("user.dir"), reportFolder, fileName).toString().replaceAll("\\\\", "/"), context.arguments().get(0));
            when(mock.config()).thenReturn(extentSparkReporterConfig);
        });
        verify(rootStore).getOrComputeIfAbsent(eq(EXTENT_REPORTS), functionArgumentCaptor.capture(), eq(ExtentReports.class));
        Function<String, ExtentReports> function = functionArgumentCaptor.getValue();
        final ExtentReports actual = function.apply("value");

        verify(extentSparkReporterConfig).setDocumentTitle(documentTitle);
        verify(extentSparkReporterConfig).setReportName(reportName);
        verify(extentSparkReporterConfig).setTheme(Theme.DARK);
        verify(extentSparkReporterConfig).setTimeStampFormat(timeStampFormat);
        verify(extentSparkReporterConfig).setCss(css);

        final ExtentReports extentReports = extentReportsMockedConstruction.constructed().get(0);
        verify(extentReports).attachReporter(extentSparkReporterMockedConstruction.constructed().toArray(new ExtentSparkReporter[0]));
        verify(rootStore).put(eq(EXTENT_REPORTS), extentReportsArgumentCaptor.capture());
        assertEquals(extentReports, actual);

        extentSparkReporterMockedConstruction.close();
        extentReportsMockedConstruction.close();

    }

    @DisplayName("getReportsPathFrom should return the full path of the report")
    @ParameterizedTest(name = "with folder {0} and fileName {1} we expect {2}")
    @MethodSource("valuesProvider")
    public void getReportsPathFrom(final String reportFolder, final String fileName, final String expected) {
        final String actual = ExtentReportsResolver.getReportsPathFrom(reportFolder, fileName);
        assertTrue(actual.matches(Paths.get(System.getProperty("user.dir"), expected).toString().replaceAll("\\\\", "/")));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("reportFolder", "fileName.html", "reportFolder/fileName.html"),
                arguments("reportFolder", "fileName-{timestamp}.html", "reportFolder/fileName-[0-9]{2}-[0-9]{2}-[0-9]{4}_[0-9]{2}-[0-9]{2}-[0-9]{2}.html"),
                arguments("reportFolder", "fileName-{timestamp:dd-MM-yyyy_HH-mm-ss}.html", "reportFolder/fileName-[0-9]{2}-[0-9]{2}-[0-9]{4}_[0-9]{2}-[0-9]{2}-[0-9]{2}.html"),
                arguments("reportFolder", "fileName-{timestamp:dd-MM-yyyy}.html", "reportFolder/fileName-[0-9]{2}-[0-9]{2}-[0-9]{4}.html")
        );
    }
}
