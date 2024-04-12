package io.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.extensions.resolvers.*;
import io.github.giulong.spectrum.extensions.watchers.EventsWatcher;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.types.*;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Slf4j
public abstract class SpectrumTest<Data> extends SpectrumEntity<SpectrumTest<Data>, Data> {

    @RegisterExtension
    public static final EventsWatcher EVENTS_WATCHER = new EventsWatcher();

    @RegisterExtension
    public static final ConfigurationResolver CONFIGURATION_RESOLVER = new ConfigurationResolver();

    @RegisterExtension
    public static final EventsDispatcherResolver EVENTS_DISPATCHER_RESOLVER = new EventsDispatcherResolver();

    @RegisterExtension
    public static final ExtentReportsResolver EXTENT_REPORTS_RESOLVER = new ExtentReportsResolver();

    @RegisterExtension
    public static final ExtentTestResolver EXTENT_TEST_RESOLVER = new ExtentTestResolver();

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
    public final DataResolver<Data> dataResolver = new DataResolver<>();

    protected List<SpectrumPage<?, Data>> spectrumPages;

    @BeforeEach
    @SuppressWarnings({"checkstyle:ParameterNumber", "unused"})
    public void beforeEach(final Configuration configuration, final TestData testData, final ExtentTest extentTest, final WebDriver driver,
                           final ImplicitWait implicitWait, final PageLoadWait pageLoadWait, final ScriptWait scriptWait, final DownloadWait downloadWait,
                           final ExtentReports extentReports, final Actions actions, final EventsDispatcher eventsDispatcher, final Data data) {
        this.configuration = configuration;
        this.driver = driver;
        this.implicitWait = implicitWait;
        this.pageLoadWait = pageLoadWait;
        this.scriptWait = scriptWait;
        this.downloadWait = downloadWait;
        this.extentReports = extentReports;
        this.extentTest = extentTest;
        this.actions = actions;
        this.eventsDispatcher = eventsDispatcher;
        this.testData = testData;
        this.data = data;

        initPages();
    }

    public void initPages() {
        final Class<?> clazz = this.getClass();
        log.debug("Initializing pages of test '{}'", clazz.getSimpleName());

        final List<Field> fields = new ArrayList<>(asList(clazz.getDeclaredFields()));

        Class<?> superClazz = clazz.getSuperclass();
        while (superClazz.getSuperclass() != SpectrumEntity.class) {
            log.debug("Initializing also pages in superclass {}", superClazz.getSimpleName());
            fields.addAll(asList(superClazz.getDeclaredFields()));
            superClazz = superClazz.getSuperclass();
        }

        spectrumPages = fields
                .stream()
                .filter(f -> SpectrumPage.class.isAssignableFrom(f.getType()))
                .map(this::initPage)
                .collect(toList());
    }

    @SneakyThrows
    public SpectrumPage<?, Data> initPage(final Field spectrumPageField) {
        log.debug("Initializing page {}", spectrumPageField.getName());

        @SuppressWarnings("unchecked") final SpectrumPage<?, Data> spectrumPage = (SpectrumPage<?, Data>) spectrumPageField.getType().getDeclaredConstructor().newInstance();
        @SuppressWarnings("unchecked") final Class<SpectrumPage<?, Data>> spectrumPageClass = (Class<SpectrumPage<?, Data>>) spectrumPage.getClass();
        Reflections.setField(spectrumPageField, this, spectrumPage);

        final String className = spectrumPageClass.getSimpleName();
        log.debug("Injecting already resolved fields into an instance of {}", className);

        final Endpoint endpointAnnotation = spectrumPageClass.getAnnotation(Endpoint.class);
        final String endpointValue = endpointAnnotation != null ? endpointAnnotation.value() : "";

        log.debug("The endpoint of page '{}' is '{}'", className, endpointValue);
        spectrumPage.setEndpoint(endpointValue);
        getSharedFields().forEach(sharedField -> Reflections.copyField(sharedField, this, spectrumPage));

        PageFactory.initElements(driver, spectrumPage);

        return spectrumPage;
    }
}
