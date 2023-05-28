package com.giuliolongfils.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.spectrum.extensions.resolvers.*;
import com.giuliolongfils.spectrum.extensions.watchers.ExtentReportsWatcher;
import com.giuliolongfils.spectrum.extensions.watchers.TestBookWatcher;
import com.giuliolongfils.spectrum.interfaces.Endpoint;
import com.giuliolongfils.spectrum.interfaces.Shared;
import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import com.giuliolongfils.spectrum.types.DownloadWait;
import com.giuliolongfils.spectrum.types.ImplicitWait;
import com.giuliolongfils.spectrum.types.PageLoadWait;
import com.giuliolongfils.spectrum.types.ScriptWait;
import com.giuliolongfils.spectrum.utils.FileReader;
import com.giuliolongfils.spectrum.utils.testbook.TestBook;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import static com.giuliolongfils.spectrum.pojos.testbook.TestBookResult.Status.NOT_RUN;
import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
public abstract class SpectrumTest<Data> extends SpectrumEntity<Data> {

    private static final Lock LOCK = new ReentrantLock();

    private static volatile boolean suiteInitialised;

    @RegisterExtension
    public static final TestBookWatcher TEST_BOOK_RESOLVER = new TestBookWatcher();

    @RegisterExtension
    public static final ExtentReportsWatcher EXTENT_REPORTS_WATCHER = new ExtentReportsWatcher();

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
    public final DataResolver<Data> dataResolver = new DataResolver<>();

    protected List<SpectrumPage<Data>> spectrumPages;

    public void initPages() {
        final Class<?> clazz = this.getClass();
        log.debug("Initializing pages of test '{}'", clazz.getSimpleName());

        final List<Field> fields = new ArrayList<>(asList(clazz.getDeclaredFields()));

        // TODO getFields invece di declared
        Class<?> superClazz = clazz.getSuperclass();
        while (superClazz.getSuperclass() != SpectrumEntity.class) {
            log.debug("Initializing also pages in superclass {}", superClazz.getSimpleName());
            fields.addAll(asList(superClazz.getDeclaredFields()));
            superClazz = superClazz.getSuperclass();
        }

        final List<Field> sharedFields = Arrays
                .stream(getClass().getFields())
                .filter(f -> f.isAnnotationPresent(Shared.class))
                .toList();

        spectrumPages = fields
                .stream()
                .filter(f -> SpectrumPage.class.isAssignableFrom(f.getType()))
                .map(f -> initPage(f, sharedFields))
                .collect(toList());
    }

    @SneakyThrows
    public SpectrumPage<Data> initPage(final Field f, final List<Field> sharedFields) {
        log.debug("Initializing page {}", f.getName());

        //noinspection unchecked
        final SpectrumPage<Data> spectrumPage = (SpectrumPage<Data>) f.getType().getDeclaredConstructor().newInstance();
        //noinspection unchecked
        final Class<SpectrumPage<Data>> spectrumPageClass = (Class<SpectrumPage<Data>>) spectrumPage.getClass();

        f.setAccessible(true);
        f.set(this, spectrumPage);

        final String className = spectrumPageClass.getSimpleName();
        log.debug("BeforeAll hook: injecting already resolved fields into an instance of {}", className);

        final Endpoint endpointAnnotation = spectrumPageClass.getAnnotation(Endpoint.class);
        final String endpointValue = endpointAnnotation != null ? endpointAnnotation.value() : "";
        log.debug("The endpoint of the page {} is '{}'", className, endpointValue);
        spectrumPage.endpoint = endpointValue;

        final Map<String, Field> targetFieldsMap = Arrays
                .stream(spectrumPageClass.getFields())
                .filter(field -> field.isAnnotationPresent(Shared.class))
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

    @BeforeAll
    public static void beforeAll(final Configuration configuration, final ExtentReports extentReports) {
        LOCK.lock();

        try {
            if (!suiteInitialised) {
                suiteInitialised = true;
                SpectrumEntity.configuration = configuration;
                SpectrumEntity.extentReports = extentReports;

                final FileReader fileReader = FileReader.getInstance();
                final Properties spectrumProperties = fileReader.readProperties("/spectrum.properties");
                log.info(String.format(Objects.requireNonNull(fileReader.read("/banner.txt")), spectrumProperties.getProperty("version")));

                final TestBook testBook = configuration.getApplication().getTestBook();
                testBook.getTests().putAll(testBook
                        .getParser()
                        .parse()
                        .stream()
                        .collect(toMap(identity(), testName -> new TestBookResult(NOT_RUN))));
            }
        } finally {
            LOCK.unlock();
        }
    }

    @BeforeEach
    @SuppressWarnings("checkstyle:ParameterNumber")
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
    }

    @AfterAll
    public static void afterAll(final ExtentReports extentReports, final Configuration configuration) {
        LOCK.lock();

        try {
            configuration.getApplication().getTestBook().flush();
            extentReports.flush();
        } finally {
            LOCK.unlock();
        }
    }
}
