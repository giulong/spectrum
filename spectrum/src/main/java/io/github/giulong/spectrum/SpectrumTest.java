package io.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentReports;
import io.github.giulong.spectrum.extensions.interceptors.SpectrumInterceptor;
import io.github.giulong.spectrum.extensions.resolvers.*;
import io.github.giulong.spectrum.extensions.watchers.EventsWatcher;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.interfaces.JsWebElement;
import io.github.giulong.spectrum.types.*;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@Slf4j
public abstract class SpectrumTest<Data> extends SpectrumEntity<SpectrumTest<Data>, Data> {

    @RegisterExtension
    public static final EventsWatcher EVENTS_WATCHER = new EventsWatcher();

    @RegisterExtension
    public static final TestContextResolver TEST_CONTEXT_RESOLVER = new TestContextResolver();

    @RegisterExtension
    public static final ConfigurationResolver CONFIGURATION_RESOLVER = new ConfigurationResolver();

    @RegisterExtension
    public static final EventsDispatcherResolver EVENTS_DISPATCHER_RESOLVER = new EventsDispatcherResolver();

    @RegisterExtension
    public static final ExtentReportsResolver EXTENT_REPORTS_RESOLVER = new ExtentReportsResolver();

    @RegisterExtension
    public static final StatefulExtentTestResolver STATEFUL_EXTENT_TEST_RESOLVER = new StatefulExtentTestResolver();

    @RegisterExtension
    public static final TestDataResolver TEST_DATA_RESOLVER = new TestDataResolver();

    @RegisterExtension
    public static final DriverResolver WEB_DRIVER_RESOLVER = new DriverResolver();

    @RegisterExtension
    public static final ImplicitWaitResolver IMPLICIT_WAIT_RESOLVER = new ImplicitWaitResolver();

    @RegisterExtension
    public static final PageLoadWaitResolver PAGE_LOAD_WAIT_RESOLVER = new PageLoadWaitResolver();

    @RegisterExtension
    public static final DownloadWaitResolver DOWNLOAD_WAIT_RESOLVER = new DownloadWaitResolver();

    @RegisterExtension
    public static final ScriptWaitResolver SCRIPT_WAIT_RESOLVER = new ScriptWaitResolver();

    @RegisterExtension
    public static final ActionsResolver ACTIONS_RESOLVER = new ActionsResolver();

    @RegisterExtension
    public static final JsResolver JS_RESOLVER = new JsResolver();

    @RegisterExtension
    public static final SpectrumInterceptor SPECTRUM_INTERCEPTOR = new SpectrumInterceptor();

    @RegisterExtension
    public static final JsWebElementProxyBuilderResolver JS_WEB_ELEMENT_PROXY_BUILDER_RESOLVER = new JsWebElementProxyBuilderResolver();

    @RegisterExtension
    public final DataResolver<Data> dataResolver = new DataResolver<>();

    protected List<SpectrumPage<?, ?>> spectrumPages;

    private JsWebElementProxyBuilder jsWebElementProxyBuilder;

    private final YamlUtils yamlUtils = YamlUtils.getInstance();

    @BeforeEach
    @SuppressWarnings({"checkstyle:ParameterNumber", "checkstyle:HiddenField", "unused"})
    void beforeEach(final TestContext testContext, final Configuration configuration, final TestData testData, final StatefulExtentTest statefulExtentTest,
                           final WebDriver driver, final ImplicitWait implicitWait, final PageLoadWait pageLoadWait, final ScriptWait scriptWait, final DownloadWait downloadWait,
                           final ExtentReports extentReports, final Actions actions, final EventsDispatcher eventsDispatcher, final Js js,
                           final JsWebElementProxyBuilder jsWebElementProxyBuilder, final Data data) {
        this.configuration = configuration;
        this.driver = driver;
        this.implicitWait = implicitWait;
        this.pageLoadWait = pageLoadWait;
        this.scriptWait = scriptWait;
        this.downloadWait = downloadWait;
        this.extentReports = extentReports;
        this.statefulExtentTest = statefulExtentTest;
        this.extentTest = statefulExtentTest.getCurrentNode();
        this.actions = actions;
        this.eventsDispatcher = eventsDispatcher;
        this.testData = testData;
        this.js = js;
        this.jsWebElementProxyBuilder = jsWebElementProxyBuilder;
        this.data = data;

        initPages();
    }

    void initPages() {
        final Class<?> clazz = this.getClass();
        log.debug("Initializing pages of test '{}'", clazz.getSimpleName());

        final List<Field> fields = new ArrayList<>(asList(clazz.getDeclaredFields()));
        final List<Field> sharedFields = getSharedFields();

        Class<?> superClazz = clazz.getSuperclass();
        while (superClazz.getSuperclass() != SpectrumEntity.class) {
            log.debug("Initializing also pages in superclass {}", superClazz.getSimpleName());
            fields.addAll(asList(superClazz.getDeclaredFields()));
            superClazz = superClazz.getSuperclass();
        }

        spectrumPages = fields
                .stream()
                .filter(f -> SpectrumPage.class.isAssignableFrom(f.getType()))
                .map(f -> initPage(f, sharedFields))
                .collect(toList());

        injectDataInPages();
    }

    @SneakyThrows
    SpectrumPage<?, Data> initPage(final Field spectrumPageField, final List<Field> sharedFields) {
        log.debug("Initializing page {}", spectrumPageField.getName());

        @SuppressWarnings("unchecked") final SpectrumPage<?, Data> spectrumPage = (SpectrumPage<?, Data>) spectrumPageField.getType().getDeclaredConstructor().newInstance();
        @SuppressWarnings("unchecked") final Class<SpectrumPage<?, Data>> spectrumPageClass = (Class<SpectrumPage<?, Data>>) spectrumPage.getClass();
        Reflections.setField(spectrumPageField, this, spectrumPage);

        final String className = spectrumPageClass.getSimpleName();
        log.debug("Injecting already resolved fields into an instance of {}", className);

        final Endpoint endpointAnnotation = spectrumPageClass.getAnnotation(Endpoint.class);
        final String endpointValue = endpointAnnotation != null ? endpointAnnotation.value() : "";

        log.debug("The endpoint of page '{}' is '{}'", className, endpointValue);
        Reflections.setField("endpoint", spectrumPage, endpointValue);
        sharedFields.forEach(sharedField -> Reflections.copyField(sharedField, this, spectrumPage));

        PageFactory.initElements(driver, spectrumPage);
        initJsWebElements(spectrumPage);

        return spectrumPage;
    }

    void initJsWebElements(final SpectrumPage<?, Data> spectrumPage) {
        final String className = spectrumPage.getClass().getSimpleName();

        Arrays.stream(spectrumPage.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(JsWebElement.class))
                .peek(f -> log.debug("Field {}.{} is annotated with @JsWebElement", className, f.getName()))
                .peek(f -> f.setAccessible(true))
                .forEach(f -> setJsWebElementProxy(f, spectrumPage));
    }

    @SneakyThrows
    void setJsWebElementProxy(final Field field, final SpectrumPage<?, Data> spectrumPage) {
        final Object value = field.get(spectrumPage);

        if (value instanceof List<?>) {
            log.debug("Field {} is a list. Cannot build proxy eagerly", field.getName());
            @SuppressWarnings("unchecked") final Object webElementProxy = Proxy.newProxyInstance(
                    List.class.getClassLoader(),
                    new Class<?>[]{List.class},
                    JsWebElementListInvocationHandler
                            .builder()
                            .jsWebElementProxyBuilder(jsWebElementProxyBuilder)
                            .webElements((List<WebElement>) value)
                            .build());

            field.set(spectrumPage, webElementProxy);
            return;
        }

        field.set(spectrumPage, jsWebElementProxyBuilder.buildFor(value));
    }

    void injectDataInPages() {
        if (data != null) {
            log.debug("Data field was already injected from SpectrumTest");
            return;
        }

        final List<SpectrumPage<?, ?>> dataSpectrumPages = spectrumPages
                .stream()
                .filter(not(spectrumPage -> Void.class.equals(Reflections.getGenericSuperclassOf(spectrumPage.getClass(), SpectrumPage.class).getActualTypeArguments()[1])))
                .toList();

        if (!dataSpectrumPages.isEmpty()) {
            final Type type = Reflections.getGenericSuperclassOf(dataSpectrumPages.getFirst().getClass(), SpectrumPage.class).getActualTypeArguments()[1];
            final String typeName = type.getTypeName();

            @SuppressWarnings("unchecked") final Class<Data> dataClass = (Class<Data>) type;
            final Data data = yamlUtils.read(String.format("%s/data.yaml", configuration.getData().getFolder()), dataClass);

            dataSpectrumPages
                    .stream()
                    .peek(dataSpectrumPage -> log.trace("Running SpectrumTest<Void> with {}<{}>. Injecting data field.", dataSpectrumPage.getClass().getTypeName(), typeName))
                    .forEach(dataSpectrumPage -> Reflections.setField("data", dataSpectrumPage, data));
        }
    }
}
