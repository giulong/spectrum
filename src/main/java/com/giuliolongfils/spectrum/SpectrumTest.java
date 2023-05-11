package com.giuliolongfils.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.spectrum.extensions.SpectrumExtension;
import com.giuliolongfils.spectrum.extensions.resolvers.*;
import com.giuliolongfils.spectrum.extensions.watchers.ExtentReportsWatcher;
import com.giuliolongfils.spectrum.extensions.watchers.TestBookWatcher;
import com.giuliolongfils.spectrum.interfaces.Endpoint;
import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.types.DownloadWait;
import com.giuliolongfils.spectrum.types.ImplicitWait;
import com.giuliolongfils.spectrum.types.PageLoadWait;
import com.giuliolongfils.spectrum.types.ScriptWait;
import com.giuliolongfils.spectrum.utils.testbook.TestBookParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
public abstract class SpectrumTest<Data> extends SpectrumEntity<Data> {

    @RegisterExtension
    public static final TestBookWatcher TEST_BOOK_RESOLVER = new TestBookWatcher();

    @RegisterExtension
    public static final ExtentReportsWatcher EXTENT_REPORTS_WATCHER = new ExtentReportsWatcher();

    @RegisterExtension
    public static final SpectrumExtension SPECTRUM_EXTENSION = new SpectrumExtension();

    @RegisterExtension
    public static final ConfigurationResolver CONFIGURATION_RESOLVER = new ConfigurationResolver();

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
    public static final TestBookParserResolver TEST_BOOK_PARSER_RESOLVER = new TestBookParserResolver();

    @RegisterExtension
    public final DataResolver<Data> dataResolver = new DataResolver<>();

    protected List<SpectrumPage<Data>> spectrumPages;

    public void initPages() {
        final Class<?> clazz = this.getClass();
        log.debug("Initializing pages of test '{}'", clazz.getSimpleName());

        final List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

        Class<?> superClazz = clazz.getSuperclass();
        while (superClazz.getSuperclass() != SpectrumEntity.class) {
            log.debug("Initializing also pages in superclass {}", superClazz.getSimpleName());
            fields.addAll(Arrays.asList(superClazz.getDeclaredFields()));
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

        f.setAccessible(true);
        f.set(this, spectrumPage);

        final String className = spectrumPage.getClass().getSimpleName();
        log.debug("BeforeAll hook: injecting already resolved fields into an instance of {}", className);

        final Endpoint endpointAnnotation = spectrumPage.getClass().getAnnotation(Endpoint.class);
        final String endpointValue = endpointAnnotation != null ? endpointAnnotation.value() : "";
        log.debug("The endpoint of the page {} is '{}'", className, endpointValue);
        spectrumPage.endpoint = endpointValue;

        return spectrumPage;
    }

    @BeforeAll
    public static void beforeAll(final Configuration configuration, final ExtentReports extentReports, final TestBookParser testBookParser) {
        SpectrumEntity.configuration = configuration;
        SpectrumEntity.extentReports = extentReports;
        SpectrumEntity.testBookParser = testBookParser;
    }

    @BeforeEach
    public void beforeEach(final WebDriver webDriver, final ImplicitWait implicitWait, final PageLoadWait pageLoadWait, final ScriptWait scriptWait,
                           final DownloadWait downloadWait, final ExtentTest extentTest, final Actions actions, final Data data) {
        this.webDriver = webDriver;
        this.implicitWait = implicitWait;
        this.pageLoadWait = pageLoadWait;
        this.scriptWait = scriptWait;
        this.downloadWait = downloadWait;
        this.extentTest = extentTest;
        this.actions = actions;
        this.data = data;

        initPages();

        // TODO ci serve questo ciclo facendo già l'initPages?
        spectrumPages.forEach(spectrumPage -> {
            spectrumPage.webDriver = this.webDriver;
            spectrumPage.implicitWait = this.implicitWait;
            spectrumPage.pageLoadWait = this.pageLoadWait;
            spectrumPage.scriptWait = this.scriptWait;
            spectrumPage.downloadWait = this.downloadWait;
            spectrumPage.extentTest = this.extentTest;
            spectrumPage.eventsListener = this.eventsListener;
            spectrumPage.actions = this.actions;
            spectrumPage.data = this.data;

            PageFactory.initElements(spectrumPage.webDriver, spectrumPage);
        });
    }
}
