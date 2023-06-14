package com.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.github.giulong.spectrum.extensions.resolvers.*;
import com.github.giulong.spectrum.extensions.watchers.EventsWatcher;
import com.github.giulong.spectrum.extensions.watchers.ExtentReportsWatcher;
import com.github.giulong.spectrum.interfaces.Endpoint;
import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.types.DownloadWait;
import com.github.giulong.spectrum.types.ImplicitWait;
import com.github.giulong.spectrum.types.PageLoadWait;
import com.github.giulong.spectrum.types.ScriptWait;
import com.github.giulong.spectrum.utils.events.EventsDispatcher;
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
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
public abstract class SpectrumTest<Data> extends SpectrumEntity<Data> {

    @RegisterExtension
    public static final EventsWatcher EVENTS_WATCHER = new EventsWatcher();

    @RegisterExtension
    public static final ExtentReportsWatcher EXTENT_REPORTS_WATCHER = new ExtentReportsWatcher();

    @RegisterExtension
    public static final ConfigurationResolver CONFIGURATION_RESOLVER = new ConfigurationResolver();

    @RegisterExtension
    public static final EventsDispatcherResolver EVENTS_DISPATCHER_RESOLVER = new EventsDispatcherResolver();

    @RegisterExtension
    public static final ExtentReportsResolver EXTENT_REPORTS_RESOLVER = new ExtentReportsResolver();

    @RegisterExtension
    public static final ExtentTestResolver EXTENT_TEST_RESOLVER = new ExtentTestResolver();

    @RegisterExtension
    public static final WebDriverResolver WEB_DRIVER_RESOLVER = new WebDriverResolver();

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

    protected List<SpectrumPage<Data>> spectrumPages;

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
    public SpectrumPage<Data> initPage(final Field f) {
        log.debug("Initializing page {}", f.getName());

        @SuppressWarnings("unchecked")
        final SpectrumPage<Data> spectrumPage = (SpectrumPage<Data>) f.getType().getDeclaredConstructor().newInstance();
        @SuppressWarnings("unchecked")
        final Class<SpectrumPage<Data>> spectrumPageClass = (Class<SpectrumPage<Data>>) spectrumPage.getClass();

        f.setAccessible(true);
        f.set(this, spectrumPage);

        final String className = spectrumPageClass.getSimpleName();
        log.debug("BeforeAll hook: injecting already resolved fields into an instance of {}", className);

        final Endpoint endpointAnnotation = spectrumPageClass.getAnnotation(Endpoint.class);
        final String endpointValue = endpointAnnotation != null ? endpointAnnotation.value() : "";
        log.debug("The endpoint of the page {} is '{}'", className, endpointValue);
        spectrumPage.endpoint = endpointValue;

        final List<Field> sharedFields = getSharedFields();
        final Map<String, Field> targetFieldsMap = sharedFields
                .stream()
                .collect(toMap(Field::getName, Function.identity()));

        sharedFields.forEach(s -> setSharedField(spectrumPage, s, targetFieldsMap));
        PageFactory.initElements(spectrumPage.webDriver, spectrumPage);

        return spectrumPage;
    }

    @SneakyThrows
    protected void setSharedField(final SpectrumPage<Data> spectrumPage, final Field spectrumTestField, final Map<String, Field> spectrumPageFieldsMap) {
        final Field spectrumPageField = spectrumPageFieldsMap.get(spectrumTestField.getName());
        spectrumPageField.setAccessible(true);
        spectrumPageField.set(spectrumPage, spectrumTestField.get(this));
    }

    @BeforeEach
    @SuppressWarnings("checkstyle:ParameterNumber")
    public void beforeEach(final Configuration configuration, final WebDriver webDriver, final ImplicitWait implicitWait, final PageLoadWait pageLoadWait,
                           final ScriptWait scriptWait, final DownloadWait downloadWait, final ExtentReports extentReports, final ExtentTest extentTest,
                           final Actions actions, final EventsDispatcher eventsDispatcher, final Data data) {
        this.configuration = configuration;
        this.webDriver = webDriver;
        this.implicitWait = implicitWait;
        this.pageLoadWait = pageLoadWait;
        this.scriptWait = scriptWait;
        this.downloadWait = downloadWait;
        this.extentReports = extentReports;
        this.extentTest = extentTest;
        this.actions = actions;
        this.eventsDispatcher = eventsDispatcher;
        this.data = data;

        initPages();
    }
}
