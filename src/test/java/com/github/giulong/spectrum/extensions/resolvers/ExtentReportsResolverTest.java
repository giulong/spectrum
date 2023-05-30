package com.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ExtentSparkReporterConfig;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.function.Function;

import static com.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.github.giulong.spectrum.extensions.resolvers.ExtentReportsResolver.EXTENT_REPORTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtentReportsResolver")
class ExtentReportsResolverTest {

    private static MockedStatic<FileUtils> fileUtilsMockedStatic;

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
    private FileUtils fileUtils;

    @Captor
    private ArgumentCaptor<Function<String, ExtentReports>> functionArgumentCaptor;

    @Captor
    private ArgumentCaptor<ExtentReports> extentReportsArgumentCaptor;

    @InjectMocks
    private ExtentReportsResolver extentReportsResolver;

    @BeforeEach
    public void beforeEach() {
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
    }

    @AfterEach
    public void afterEach() {
        fileUtilsMockedStatic.close();
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
        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.read("/css/report.css")).thenReturn(css);
        when(fileUtils.interpolateTimestampFrom(fileName)).thenReturn(fileName);

        extentReportsResolver.resolveParameter(parameterContext, extensionContext);

        MockedConstruction<ExtentReports> extentReportsMockedConstruction = mockConstruction(ExtentReports.class);
        MockedConstruction<ExtentSparkReporter> extentSparkReporterMockedConstruction = mockConstruction(ExtentSparkReporter.class, (mock, context) -> {
            assertEquals(Path.of(System.getProperty("user.dir"), reportFolder, fileName).toString().replace("\\", "/"), context.arguments().get(0));
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

    @Test
    @DisplayName("getReportsPathFrom should return the full path of the report")
    public void getReportsPathFrom() {
        final String reportFolder = "reportFolder";
        final String fileName = "fileName";
        final String expected = reportFolder + "/" + fileName;

        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.interpolateTimestampFrom(fileName)).thenReturn(fileName);

        final String actual = ExtentReportsResolver.getReportsPathFrom(reportFolder, fileName);
        assertTrue(actual.matches(Path.of(System.getProperty("user.dir"), expected).toString().replace("\\", "/")));
    }
}
