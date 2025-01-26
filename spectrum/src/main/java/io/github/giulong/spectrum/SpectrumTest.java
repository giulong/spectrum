package io.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentReports;
import io.github.giulong.spectrum.extensions.interceptors.SpectrumInterceptor;
import io.github.giulong.spectrum.extensions.resolvers.*;
import io.github.giulong.spectrum.extensions.watchers.EventsWatcher;
import io.github.giulong.spectrum.interfaces.Shared;
import io.github.giulong.spectrum.types.*;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.js.Js;
import io.github.giulong.spectrum.utils.js.JsWebElementProxyBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import static java.util.function.Predicate.not;

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
    public static final FakerResolver FAKER_RESOLVER = new FakerResolver();

    @RegisterExtension
    public static final SpectrumInterceptor SPECTRUM_INTERCEPTOR = new SpectrumInterceptor();

    @RegisterExtension
    public static final JsWebElementProxyBuilderResolver JS_WEB_ELEMENT_PROXY_BUILDER_RESOLVER = new JsWebElementProxyBuilderResolver();

    @RegisterExtension
    public final DataResolver<Data> dataResolver = new DataResolver<>();

    private final YamlUtils yamlUtils = YamlUtils.getInstance();

    @BeforeAll
    static void beforeAll(final Configuration configuration, final EventsDispatcher eventsDispatcher, final ExtentReports extentReports, final Faker faker) {
        SpectrumTest.configuration = configuration;
        SpectrumTest.eventsDispatcher = eventsDispatcher;
        SpectrumTest.extentReports = extentReports;
        SpectrumTest.faker = faker;
    }

    @BeforeEach
    @SuppressWarnings({"checkstyle:ParameterNumber"})
    void beforeEach(final TestContext testContext, final TestData testData, final StatefulExtentTest statefulExtentTest, final WebDriver driver,
                    final ImplicitWait implicitWait, final PageLoadWait pageLoadWait, final ScriptWait scriptWait, final DownloadWait downloadWait,
                    final Actions actions, final Js js, final JsWebElementProxyBuilder jsWebElementProxyBuilder, final Data data) {
        this.driver = driver;
        this.implicitWait = implicitWait;
        this.pageLoadWait = pageLoadWait;
        this.scriptWait = scriptWait;
        this.downloadWait = downloadWait;
        this.statefulExtentTest = statefulExtentTest;
        this.extentTest = statefulExtentTest.getCurrentNode();
        this.actions = actions;
        this.testData = testData;
        this.js = js;
        this.jsWebElementProxyBuilder = jsWebElementProxyBuilder;
        this.data = data;
        this.testContext = testContext;

        injectDataIn(injectPages());
    }

    List<? extends SpectrumPage<?, ?>> injectPages() {
        final Class<?> clazz = this.getClass();
        log.debug("Initializing pages of test '{}'", clazz.getSimpleName());

        final List<Field> sharedFields = Reflections.getAnnotatedFields(SpectrumEntity.class, Shared.class);

        return Reflections
                .getFieldsOf(clazz, SpectrumTest.class)
                .stream()
                .filter(f -> SpectrumPage.class.isAssignableFrom(f.getType()))
                .peek(f -> log.debug("Initializing page {}", f.getName()))
                .map(this::injectPageInto)
                .peek(spectrumPage -> sharedFields.forEach(sharedField -> Reflections.copyField(sharedField, this, spectrumPage)))
                .map(SpectrumPage::init)
                .toList();
    }

    @SneakyThrows
    SpectrumPage<?, Data> injectPageInto(final Field field) {
        @SuppressWarnings("unchecked") final SpectrumPage<?, Data> spectrumPage = (SpectrumPage<?, Data>) field.getType().getDeclaredConstructor().newInstance();
        Reflections.setField(field, this, spectrumPage);

        return spectrumPage;
    }

    void injectDataIn(final List<? extends SpectrumPage<?, ?>> spectrumPages) {
        if (data != null) {
            log.debug("Data field was already injected from SpectrumTest");
            return;
        }

        final List<? extends SpectrumPage<?, ?>> dataSpectrumPages = spectrumPages
                .stream()
                .filter(not(spectrumPage -> Void.class.equals(Reflections.getGenericSuperclassOf(spectrumPage.getClass(), SpectrumPage.class).getActualTypeArguments()[1])))
                .toList();

        if (dataSpectrumPages.isEmpty()) {
            return;
        }

        final Type type = Reflections.getGenericSuperclassOf(dataSpectrumPages.getFirst().getClass(), SpectrumPage.class).getActualTypeArguments()[1];
        final String typeName = type.getTypeName();

        @SuppressWarnings("unchecked") final Class<Data> dataClass = (Class<Data>) type;
        final Data data = yamlUtils.readClient(String.format("%s/data.yaml", configuration.getData().getFolder()), dataClass);

        dataSpectrumPages
                .stream()
                .peek(dataSpectrumPage -> log.trace("Running SpectrumTest<Void> with {}<{}>. Injecting data field.", dataSpectrumPage.getClass().getTypeName(), typeName))
                .forEach(dataSpectrumPage -> Reflections.setField("data", dataSpectrumPage, data));
    }
}
