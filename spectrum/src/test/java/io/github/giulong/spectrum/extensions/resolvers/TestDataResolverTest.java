package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.types.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestDataResolver")
class TestDataResolverTest {

    private static MockedStatic<TestData> testDataMockedStatic;

    private static final String REPORTS_FOLDER = "reportsFolder";
    private static final String CLASS_NAME = "className";
    private static final String METHOD_NAME = "methodName";

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Extent extent;

    @Mock
    private TestData.TestDataBuilder testDataBuilder;

    @Mock
    private TestData testData;

    @InjectMocks
    private TestDataResolver testDataResolver;

    @BeforeEach
    public void beforeEach() throws IOException {
        testDataMockedStatic = mockStatic(TestData.class);
        final Path path = Files.createTempDirectory(REPORTS_FOLDER);
        path.toFile().deleteOnExit();
    }

    @AfterEach
    public void afterEach() {
        testDataMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of testData")
    public void resolveParameter() throws NoSuchMethodException {
        final Class<String> clazz = String.class;
        final String className = clazz.getSimpleName();
        final String methodName = "resolveParameter";
        final Path path = Path.of(REPORTS_FOLDER, "screenshots", className, methodName).toAbsolutePath();
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(REPORTS_FOLDER);
        doReturn(String.class).when(extensionContext).getRequiredTestClass();
        when(extensionContext.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod(methodName));

        when(TestData.builder()).thenReturn(testDataBuilder);
        when(testDataBuilder.className(className)).thenReturn(testDataBuilder);
        when(testDataBuilder.methodName(methodName)).thenReturn(testDataBuilder);
        when(testDataBuilder.screenshotFolderPath(path)).thenReturn(testDataBuilder);
        when(testDataBuilder.build()).thenReturn(testData);

        final TestData actual = testDataResolver.resolveParameter(parameterContext, extensionContext);

        assertTrue(Files.exists(path));
        assertEquals(testData, actual);
        verify(store).put(TEST_DATA, actual);
    }

    @Test
    @DisplayName("getScreenshotFolderPathForCurrentTest should return the path for the current test and create the dirs")
    public void getScreenshotFolderPathForCurrentTest() {
        final Path path = Path.of(REPORTS_FOLDER, "screenshots", CLASS_NAME, METHOD_NAME).toAbsolutePath();
        assertEquals(path, testDataResolver.getScreenshotFolderPathForCurrentTest(REPORTS_FOLDER, CLASS_NAME, METHOD_NAME));

        assertTrue(Files.exists(path));
    }
}