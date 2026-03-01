package io.github.giulong.spectrum.generation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.bidi.network.InterceptPhase.RESPONSE_STARTED;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.sun.net.httpserver.HttpServer;

import io.github.giulong.spectrum.generation.generators.SpectrumTestGenerator;
import io.github.giulong.spectrum.generation.server.ActionHandler;
import io.github.giulong.spectrum.generation.server.Server;
import io.github.giulong.spectrum.generation.server.actions.Action;
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
import org.openqa.selenium.bidi.network.AddInterceptParameters;
import org.openqa.selenium.bidi.network.RequestData;
import org.openqa.selenium.bidi.network.ResponseData;
import org.openqa.selenium.bidi.network.ResponseDetails;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

class RecordingTest {

    private final String fqdnProperty = "fqdn";
    private final String navigationId = "navigationId";

    private MockedStatic<SpectrumTestGenerator> spectrumTestGeneratorMockedStatic;
    private MockedStatic<ActionHandler> actionHandlerMockedStatic;
    private MockedStatic<Server> serverMockedStatic;
    private MockedStatic<HttpServer> httpServerMockedStatic;

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
    private ActionHandler.ActionHandlerBuilder actionHandlerBuilder;

    @Mock
    private Server.ServerBuilder serverBuilder;

    @Mock
    private SpectrumTestGenerator.SpectrumTestGeneratorBuilder spectrumTestGeneratorBuilder;

    @Mock
    private HttpServer httpServer;

    @Mock
    private ActionHandler handler;

    @Mock
    private SpectrumTestGenerator spectrumTestGenerator;

    @Mock
    private Path destination;

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
    private ArgumentCaptor<AddInterceptParameters> addInterceptParametersArgumentCaptor;

    @Captor
    private ArgumentCaptor<Consumer<ResponseDetails>> responseDetailsArgumentCaptor;

    @InjectMocks
    private Recording recording;

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

        System.getProperties().clear();
    }

    @Test
    @DisplayName("parseProperties should throw an exception if the provided fqdn is not valid")
    void parsePropertiesFqdnNotValid() {
        final String fqdn = "not valid";

        System.setProperty(fqdnProperty, fqdn);

        final RuntimeException exception = assertThrows(IllegalArgumentException.class, () -> recording.parseProperties());
        assertEquals("Fqdn '" + fqdn + "' is not a valid fully qualified class name!", exception.getMessage());
    }

    @Test
    @DisplayName("parseProperties should throw an exception if the provided fqdn is not valid")
    void parseProperties() {
        final String fqdn = "com.a.b.C.java";
        final String destinationProperty = "destination";

        System.setProperty(fqdnProperty, fqdn);
        System.setProperty(destinationProperty, destinationProperty);

        assertEquals(recording, recording.parseProperties());
        assertEquals(fqdn, recording.getFqdn());
        assertEquals(Path.of(destinationProperty), recording.getDestination());
        assertEquals(Path.of("com.a.b".replace(".", File.separator)), recording.getPackagePath());
        assertEquals("C.java", recording.getClassName());
    }

    @Test
    @DisplayName("setup should just start the server")
    void setup() {
        assertEquals(recording, recording.setup());

        verify(server).start();
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("record should wrap the driver with a network interceptor and inject the js in every new page, intercepting navigation")
    void record() {
        final String url = "url";
        final int port = 123;

        Reflections.setField("fileUtils", recording, fileUtils);

        when(responseDetails.getRedirectCount()).thenReturn(0L);
        when(responseDetails.getResponseData()).thenReturn(responseData);
        when(responseData.getUrl()).thenReturn(url);

        navigationTrueStubs();
        recordStubsFor();

        when(driver.getCurrentUrl())
                .thenReturn(url)
                .thenThrow(new WebDriverException());

        final List<Runnable> runnable = new ArrayList<>();

        try (MockedConstruction<Network> networkMockedConstruction = mockConstruction();
                MockedConstruction<Thread> ignored = mockConstruction((mock, context) -> runnable.add((Runnable) context.arguments().getFirst()))) {
            assertEquals(recording, recording.record());

            recordVerificationsFor(networkMockedConstruction, runnable);

            verify(server).addNavigationTo(url);
            verify((JavascriptExecutor) driver).executeScript(scriptKey, port);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("record should wrap the driver with a network interceptor and inject the js in every new page")
    void recordNoNavigation() {
        final String url = "url";
        final int port = 123;

        recordStubsFor();

        when(driver.getCurrentUrl()).thenThrow(new WebDriverException());

        final List<Runnable> runnable = new ArrayList<>();

        try (MockedConstruction<Network> networkMockedConstruction = mockConstruction();
                MockedConstruction<Thread> ignored = mockConstruction((mock, context) -> runnable.add((Runnable) context.arguments().getFirst()))) {
            assertEquals(recording, recording.record());

            recordVerificationsFor(networkMockedConstruction, runnable);

            verify(server, never()).addNavigationTo(url);
            verify((JavascriptExecutor) driver, never()).executeScript(scriptKey, port);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("record should wrap the driver with a network interceptor and inject the js in every new page, avoiding registering navigation for redirects")
    void recordNavigationRedirect() {
        final String url = "url";
        final int port = 123;

        navigationTrueStubs();
        recordStubsFor();

        when(responseDetails.getRedirectCount()).thenReturn(123L);
        when(driver.getCurrentUrl()).thenThrow(new WebDriverException());

        final List<Runnable> runnable = new ArrayList<>();

        try (MockedConstruction<Network> networkMockedConstruction = mockConstruction();
                MockedConstruction<Thread> ignored = mockConstruction((mock, context) -> runnable.add((Runnable) context.arguments().getFirst()))) {
            assertEquals(recording, recording.record());

            recordVerificationsFor(networkMockedConstruction, runnable);

            verify(server, never()).addNavigationTo(url);
            verify((JavascriptExecutor) driver).executeScript(scriptKey, port);
        }
    }

    @Test
    @DisplayName("record should wrap the driver with a network interceptor and inject the js in every new page, avoiding registering navigation for redirects")
    void recordDriverClosed() {
        recordStubsFor();

        try (MockedConstruction<Network> ignored = mockConstruction((mock, context) -> when(mock.addIntercept(any())).thenThrow(new WebDriverException()))) {
            assertEquals(recording, recording.record());
        }
    }

    @Test
    @DisplayName("isNavigation should return true when the provided ResponseDetails is a GET with mime text/html")
    void isNavigationTrue() {
        navigationTrueStubs();

        assertTrue(recording.isNavigation(responseDetails));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("isNavigation should return false when the provided ResponseDetails has no navigation id")
    void isNavigationNoNavigationId() {
        when(responseDetails.getNavigationId()).thenReturn(null);

        assertFalse(recording.isNavigation(responseDetails));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("isNavigation should return false when the provided ResponseDetails is not a GET request")
    void isNavigationNoGet() {
        when(responseDetails.getNavigationId()).thenReturn(navigationId);
        when(responseDetails.getRequest()).thenReturn(requestData);
        when(requestData.getMethod()).thenReturn("nope");

        assertFalse(recording.isNavigation(responseDetails));
    }

    @SuppressWarnings("DataFlowIssue")
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
        final String className = "className";

        Reflections.setField("className", recording, className);
        Reflections.setField("destination", recording, destination);
        Reflections.setField("packagePath", recording, packagePath);

        when(SpectrumTestGenerator.builder()).thenReturn(spectrumTestGeneratorBuilder);
        when(spectrumTestGeneratorBuilder.actions(actions)).thenReturn(spectrumTestGeneratorBuilder);
        when(spectrumTestGeneratorBuilder.destination(destination)).thenReturn(spectrumTestGeneratorBuilder);
        when(spectrumTestGeneratorBuilder.packagePath(packagePath)).thenReturn(spectrumTestGeneratorBuilder);
        when(spectrumTestGeneratorBuilder.className(className)).thenReturn(spectrumTestGeneratorBuilder);
        when(spectrumTestGeneratorBuilder.build()).thenReturn(spectrumTestGenerator);

        recording.generate();

        verify(spectrumTestGenerator).generate();
    }

    @Test
    @DisplayName("main create a Recording instance and act as the entry point to the record and playback feature")
    void mainTest() {
        try (MockedStatic<Recording> recordingMockedStatic = mockStatic();
                MockedConstruction<InetSocketAddress> ignored = mockConstruction((mock, context) -> {
                    assertEquals(0, context.arguments().getFirst());
                    when(HttpServer.create(mock, 0)).thenReturn(httpServer);
                });

                MockedConstruction<ChromeOptions> optionsMockedConstruction = mockConstruction((mock, context) -> when(mock.addArguments("--disable-web-security")).thenReturn(
                        mock));
                MockedConstruction<ChromeDriver> ignored2 = mockConstruction((mock, context) -> {
                    assertEquals(optionsMockedConstruction.constructed().getFirst(), context.arguments().getFirst());
                    when(recordingBuilder.driver(mock)).thenReturn(recordingBuilder);
                })) {

            when(ActionHandler.builder()).thenReturn(actionHandlerBuilder);
            when(actionHandlerBuilder.actions(actionsArgumentCaptor.capture())).thenReturn(actionHandlerBuilder);
            when(actionHandlerBuilder.build()).thenReturn(handler);

            when(Server.builder()).thenReturn(serverBuilder);
            when(serverBuilder.actions(actionsArgumentCaptor.capture())).thenReturn(serverBuilder);
            when(serverBuilder.handler(handler)).thenReturn(serverBuilder);
            when(serverBuilder.httpServer(httpServer)).thenReturn(serverBuilder);
            when(serverBuilder.build()).thenReturn(server);

            recordingMockedStatic.when(() -> Recording.main(null)).thenCallRealMethod();
            when(Recording.builder()).thenReturn(recordingBuilder);
            when(recordingBuilder.actions(actionsArgumentCaptor.capture())).thenReturn(recordingBuilder);
            when(recordingBuilder.server(server)).thenReturn(recordingBuilder);
            when(recordingBuilder.build()).thenReturn(recordingMock);

            when(recordingMock.parseProperties()).thenReturn(recordingMock);
            when(recordingMock.setup()).thenReturn(recordingMock);
            when(recordingMock.record()).thenReturn(recordingMock);
            when(recordingMock.tearDown()).thenReturn(recordingMock);

            Recording.main(null);

            final List<List<Action>> actualActions = actionsArgumentCaptor.getAllValues();
            assertEquals(3, actualActions.size());
            assertEquals(List.of(), actualActions.getFirst());
            assertEquals(List.of(), actualActions.get(1));
            assertEquals(List.of(), actualActions.get(2));

            final ChromeOptions actualOptions = optionsMockedConstruction.constructed().getFirst();

            verify(actualOptions).setCapability("webSocketUrl", true);
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

    @SuppressWarnings("unchecked")
    private void recordVerificationsFor(final MockedConstruction<Network> networkMockedConstruction, final List<Runnable> runnable) {
        final Network constructedNetwork = networkMockedConstruction.constructed().getFirst();
        verify(constructedNetwork).addIntercept(addInterceptParametersArgumentCaptor.capture());
        verify(constructedNetwork).onResponseCompleted(responseDetailsArgumentCaptor.capture());

        assertIterableEquals(List.of(RESPONSE_STARTED.toString()), (List<AddInterceptParameters>) addInterceptParametersArgumentCaptor.getValue().toMap().get("phases"));

        responseDetailsArgumentCaptor.getValue().accept(responseDetails);

        runnable.getFirst().run();
    }

    @SuppressWarnings("DataFlowIssue")
    private void navigationTrueStubs() {
        when(responseDetails.getNavigationId()).thenReturn(navigationId);
        when(responseDetails.getRequest()).thenReturn(requestData);
        when(requestData.getMethod()).thenReturn("GET");
        when(responseDetails.getResponseData()).thenReturn(responseData);
        when(responseData.getMimeType()).thenReturn("text/html");
    }
}
