package io.github.giulong.spectrum.generation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.sun.net.httpserver.HttpServer;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.generation.generators.SpectrumTestGenerator;
import io.github.giulong.spectrum.generation.server.ActionHandler;
import io.github.giulong.spectrum.generation.server.Server;
import io.github.giulong.spectrum.generation.server.actions.Action;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Drivers;
import io.github.giulong.spectrum.utils.Configuration.Runtime;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ScriptKey;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.RequestData;
import org.openqa.selenium.bidi.network.ResponseData;
import org.openqa.selenium.bidi.network.ResponseDetails;

class RecordingTest {

    private final String navigationId = "navigationId";
    private final String className = "className";

    private MockedStatic<SpectrumTestGenerator> spectrumTestGeneratorMockedStatic;
    private MockedStatic<ActionHandler> actionHandlerMockedStatic;
    private MockedStatic<Server> serverMockedStatic;
    private MockedStatic<HttpServer> httpServerMockedStatic;

    @Mock
    private Configuration configuration;

    @Mock
    private Runtime runtime;

    @Mock
    private Driver<?, ?, ?> driverConfiguration;

    @Mock
    private Drivers drivers;

    @Mock
    private Drivers recordingDrivers;

    @Mock
    private Configuration.Recording recordingConfiguration;

    @Mock
    private ScriptKey scriptKey;

    @Mock(extraInterfaces = JavascriptExecutor.class)
    private WebDriver driver;

    @Mock
    private InetSocketAddress inetSocketAddress;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private Recording.RecordingBuilder recordingBuilder;

    @Mock
    private Recording recordingMock;

    @Mock
    private Server.ServerBuilder serverBuilder;

    @Mock
    private SpectrumTestGenerator.SpectrumTestGeneratorBuilder spectrumTestGeneratorBuilder;

    @Mock
    private HttpServer httpServer;

    @Mock
    private SpectrumTestGenerator spectrumTestGenerator;

    @Mock
    private List<Action> actions;

    @Mock
    private Path packagePath;

    @Mock
    private Server server;

    @Mock
    private ResponseDetails responseDetails;

    @Mock
    private RequestData requestData;

    @Mock
    private ResponseData responseData;

    @Captor
    private ArgumentCaptor<List<Action>> actionsArgumentCaptor;

    @Captor
    private ArgumentCaptor<Consumer<ResponseDetails>> responseDetailsArgumentCaptor;

    @Captor
    private ArgumentCaptor<ActionHandler> actionHandlerArgumentCaptor;

    @InjectMocks
    private Recording recording = new Recording(actions, server, driver, packagePath, className, false);

    @BeforeEach
    void beforeEach() {
        spectrumTestGeneratorMockedStatic = mockStatic();
        actionHandlerMockedStatic = mockStatic();
        serverMockedStatic = mockStatic();
        httpServerMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        spectrumTestGeneratorMockedStatic.close();
        actionHandlerMockedStatic.close();
        serverMockedStatic.close();
        httpServerMockedStatic.close();
    }

    @Test
    @DisplayName("parseProperties should throw an exception if the provided fqdn is not valid")
    void parsePropertiesFqdnNotValid() {
        final String fqdn = "not valid";

        Reflections.setField("configuration", recording, configuration);

        when(configuration.getRecording()).thenReturn(recordingConfiguration);
        when(recordingConfiguration.getFqdn()).thenReturn(fqdn);

        final RuntimeException exception = assertThrows(IllegalArgumentException.class, () -> recording.parseProperties());
        assertEquals("Fqdn '" + fqdn + "' is not a valid fully qualified class name!", exception.getMessage());
    }

    @Test
    @DisplayName("parseProperties should throw an exception if the provided fqdn is not valid")
    void parseProperties() {
        final String fqdn = "com.a.b.C.java";

        Reflections.setField("configuration", recording, configuration);

        when(configuration.getRecording()).thenReturn(recordingConfiguration);
        when(recordingConfiguration.getFqdn()).thenReturn(fqdn);

        assertEquals(recording, recording.parseProperties());
        assertEquals(Path.of("com.a.b".replace(".", File.separator)), recording.getPackagePath());
        assertEquals("C.java", recording.getClassName());
    }

    @Test
    @DisplayName("buildDriver should build the driver with the recording capabilities")
    void buildDriver() {
        Reflections.setField("configuration", recording, configuration);

        when(configuration.getRuntime()).thenReturn(runtime);
        doReturn(driverConfiguration).when(runtime).getDriver();
        doReturn(driverConfiguration).when(driverConfiguration).buildCapabilities();
        doReturn(driverConfiguration).when(driverConfiguration).mergeCapabilitiesWith(drivers);
        doReturn(driverConfiguration).when(driverConfiguration).mergeCapabilitiesWith(recordingDrivers);

        when(configuration.getDrivers()).thenReturn(drivers);
        when(configuration.getRecording()).thenReturn(recordingConfiguration);
        when(recordingConfiguration.getDrivers()).thenReturn(recordingDrivers);

        assertEquals(recording, recording.buildDriver());
    }

    @Test
    @DisplayName("setup should just start the server")
    void setup() {
        assertEquals(recording, recording.setup());

        verify(server).start();
    }

    @Test
    @DisplayName("record should wrap the driver with a network interceptor and inject the js in every new page, intercepting navigation")
    void recording() {
        final String url = "url";
        final int port = 123;
        final boolean actualDriverClosedBefore = Reflections.getFieldValue("driverClosed", recording);

        assertFalse(actualDriverClosedBefore);

        Reflections.setField("fileUtils", recording, fileUtils);
        Reflections.setField("configuration", recording, configuration);

        when(responseDetails.getResponseData()).thenReturn(responseData);
        when(responseData.getUrl()).thenReturn(url);

        navigationTrueStubsForMime("text/html");
        recordStubsFor();

        when(driver.getCurrentUrl())
                .thenReturn(url)
                .thenThrow(new WebDriverException());

        final List<Runnable> runnable = new ArrayList<>();

        try (MockedConstruction<Network> networkMockedConstruction = mockConstruction();
                MockedConstruction<Thread> ignored = mockConstruction((mock, context) -> runnable.add((Runnable) context.arguments().getFirst()))) {
            assertEquals(recording, recording.record());

            recordVerificationsFor(networkMockedConstruction, runnable);

            verify((JavascriptExecutor) driver).executeScript(scriptKey, port);

            final boolean actualDriverClosedAfter = Reflections.getFieldValue("driverClosed", recording);
            assertTrue(actualDriverClosedAfter);
        }
    }

    @Test
    @DisplayName("record should wrap the driver with a network interceptor and inject the js in every new page")
    void recordNoNavigation() {
        final int port = 123;

        recordStubsFor();

        when(driver.getCurrentUrl()).thenThrow(new WebDriverException());

        final List<Runnable> runnable = new ArrayList<>();

        try (MockedConstruction<Network> networkMockedConstruction = mockConstruction();
                MockedConstruction<Thread> ignored = mockConstruction((mock, context) -> runnable.add((Runnable) context.arguments().getFirst()))) {
            assertEquals(recording, recording.record());

            recordVerificationsFor(networkMockedConstruction, runnable);

            verify((JavascriptExecutor) driver, never()).executeScript(scriptKey, port);
        }
    }

    @Test
    @DisplayName("record should wrap the driver with a network interceptor and inject the js in every new page, avoiding registering navigation for redirects")
    void recordNavigationRedirect() {
        final int port = 123;

        navigationTrueStubsForMime("text/html");
        recordStubsFor();

        when(driver.getCurrentUrl()).thenThrow(new WebDriverException());

        final List<Runnable> runnable = new ArrayList<>();

        try (MockedConstruction<Network> networkMockedConstruction = mockConstruction();
                MockedConstruction<Thread> ignored = mockConstruction((mock, context) -> runnable.add((Runnable) context.arguments().getFirst()))) {
            assertEquals(recording, recording.record());

            recordVerificationsFor(networkMockedConstruction, runnable);

            verify((JavascriptExecutor) driver).executeScript(scriptKey, port);
        }
    }

    @Test
    @DisplayName("record should wrap the driver with a network interceptor and inject the js in every new page, avoiding registering navigation for redirects")
    void recordDriverClosed() {
        recordStubsFor();

        try (MockedConstruction<Network> ignored = mockConstruction((mock, context) -> doThrow(new WebDriverException()).when(mock).onResponseCompleted(any()))) {
            assertEquals(recording, recording.record());
        }
    }

    @Test
    @DisplayName("isNavigation should return true when the provided ResponseDetails is a GET with mime text/html")
    void isNavigationTrue() {
        navigationTrueStubsForMime("text/html;something");

        assertTrue(recording.isNavigation(responseDetails));
    }

    @Test
    @DisplayName("isNavigation should return false when the provided ResponseDetails has no navigation id")
    void isNavigationNoNavigationId() {
        when(responseDetails.getNavigationId()).thenReturn(null);

        assertFalse(recording.isNavigation(responseDetails));
    }

    @Test
    @DisplayName("isNavigation should return false when the provided ResponseDetails is not a GET request")
    void isNavigationNoGet() {
        when(responseDetails.getNavigationId()).thenReturn(navigationId);
        when(responseDetails.getRequest()).thenReturn(requestData);
        when(requestData.getMethod()).thenReturn("nope");

        assertFalse(recording.isNavigation(responseDetails));
    }

    @Test
    @DisplayName("isNavigation should return false when the provided ResponseDetails mime type is not text/html")
    void isNavigationWrongMime() {
        when(responseDetails.getNavigationId()).thenReturn(navigationId);
        when(responseDetails.getRequest()).thenReturn(requestData);
        when(requestData.getMethod()).thenReturn("GET");
        when(responseDetails.getResponseData()).thenReturn(responseData);
        when(responseData.getMimeType()).thenReturn("nope");

        assertFalse(recording.isNavigation(responseDetails));
    }

    @Test
    @DisplayName("tearDown should just stop the server")
    void tearDown() {
        assertEquals(recording, recording.tearDown());

        verify(server).stop();
    }

    @Test
    @DisplayName("generate should delegate to the SpectrumTestGenerator")
    void generate() {
        final String destination = "destination";

        Reflections.setField("configuration", recording, configuration);

        when(configuration.getRecording()).thenReturn(recordingConfiguration);
        when(recordingConfiguration.getDestination()).thenReturn(destination);

        when(SpectrumTestGenerator.builder()).thenReturn(spectrumTestGeneratorBuilder);
        when(spectrumTestGeneratorBuilder.actions(actions)).thenReturn(spectrumTestGeneratorBuilder);
        when(spectrumTestGeneratorBuilder.destination(Path.of(destination))).thenReturn(spectrumTestGeneratorBuilder);
        when(spectrumTestGeneratorBuilder.packagePath(packagePath)).thenReturn(spectrumTestGeneratorBuilder);
        when(spectrumTestGeneratorBuilder.className(className)).thenReturn(spectrumTestGeneratorBuilder);
        when(spectrumTestGeneratorBuilder.build()).thenReturn(spectrumTestGenerator);

        recording.generate();

        verify(spectrumTestGenerator).generate();
    }

    @Test
    @DisplayName("afterAll should quit the driver if it's not closed already")
    void afterAllQuit() {
        Reflections.setField("driverClosed", recording, false);

        recording.afterAll();

        verify(driver).quit();
    }

    @Test
    @DisplayName("afterAll should avoid quitting an already closed driver")
    void afterAllAvoidQuit() {
        Reflections.setField("driverClosed", recording, true);

        recording.afterAll();

        verify(driver, never()).quit();
    }

    @Test
    @DisplayName("main should create a Recording instance and act as the entry point to the record and playback feature")
    void mainTest() {
        try (MockedStatic<Recording> recordingMockedStatic = mockStatic();
                MockedConstruction<InetSocketAddress> ignored = mockConstruction((mock, context) -> {
                    assertEquals(0, context.arguments().getFirst());
                    when(HttpServer.create(mock, 0)).thenReturn(httpServer);
                });
                MockedConstruction<ActionHandler> ignored2 = mockConstruction((mock, context) -> assertEquals(actionsArgumentCaptor.getValue(), context.arguments().getFirst()))) {

            when(Server.builder()).thenReturn(serverBuilder);
            when(serverBuilder.actions(actionsArgumentCaptor.capture())).thenReturn(serverBuilder);
            when(serverBuilder.handler(actionHandlerArgumentCaptor.capture())).thenReturn(serverBuilder);
            when(serverBuilder.httpServer(httpServer)).thenReturn(serverBuilder);
            when(serverBuilder.build()).thenReturn(server);

            recordingMockedStatic.when(() -> Recording.main(null)).thenCallRealMethod();
            when(Recording.builder()).thenReturn(recordingBuilder);
            when(recordingBuilder.actions(actionsArgumentCaptor.capture())).thenReturn(recordingBuilder);
            when(recordingBuilder.server(server)).thenReturn(recordingBuilder);
            when(recordingBuilder.build()).thenReturn(recordingMock);

            when(recordingMock.parseProperties()).thenReturn(recordingMock);
            when(recordingMock.buildDriver()).thenReturn(recordingMock);
            when(recordingMock.setup()).thenReturn(recordingMock);
            when(recordingMock.record()).thenReturn(recordingMock);
            when(recordingMock.tearDown()).thenReturn(recordingMock);
            when(recordingMock.generate()).thenReturn(recordingMock);

            Recording.main(null);

            final List<List<Action>> actualActions = actionsArgumentCaptor.getAllValues();
            assertEquals(2, actualActions.size());
            assertEquals(List.of(), actualActions.getFirst());
            assertEquals(List.of(), actualActions.get(1));

            verify(recordingMock).generate();
        }
    }

    private void recordStubsFor() {
        final String interceptorJs = "interceptorJs";

        Reflections.setField("fileUtils", recording, fileUtils);

        when(fileUtils.read("js/interceptor.js")).thenReturn(interceptorJs);
        when(server.getHttpServer()).thenReturn(httpServer);
        when(httpServer.getAddress()).thenReturn(inetSocketAddress);
        when(inetSocketAddress.getPort()).thenReturn(123);
        when(((JavascriptExecutor) driver).pin(interceptorJs)).thenReturn(scriptKey);
    }

    private void recordVerificationsFor(final MockedConstruction<Network> networkMockedConstruction, final List<Runnable> runnable) {
        final Network constructedNetwork = networkMockedConstruction.constructed().getFirst();
        verify(constructedNetwork).onResponseCompleted(responseDetailsArgumentCaptor.capture());

        responseDetailsArgumentCaptor.getValue().accept(responseDetails);

        runnable.getFirst().run();
    }

    private void navigationTrueStubsForMime(final String mime) {
        when(responseDetails.getNavigationId()).thenReturn(navigationId);
        when(responseDetails.getRequest()).thenReturn(requestData);
        when(requestData.getMethod()).thenReturn("GET");
        when(responseDetails.getResponseData()).thenReturn(responseData);
        when(responseData.getMimeType()).thenReturn(mime);
    }
}
