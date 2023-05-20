package com.giuliolongfils.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.spectrum.extensions.resolvers.*;
import com.giuliolongfils.spectrum.extensions.watchers.ExtentReportsWatcher;
import com.giuliolongfils.spectrum.extensions.watchers.TestBookWatcher;
import com.giuliolongfils.spectrum.interfaces.Endpoint;
import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.testbook.TestBook;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookStatistics;
import com.giuliolongfils.spectrum.types.DownloadWait;
import com.giuliolongfils.spectrum.types.ImplicitWait;
import com.giuliolongfils.spectrum.types.PageLoadWait;
import com.giuliolongfils.spectrum.types.ScriptWait;
import com.giuliolongfils.spectrum.utils.FileReader;
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

import static com.giuliolongfils.spectrum.pojos.testbook.TestBookResult.Status.NOT_RUN;
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

    @AfterAll
    public static void afterAll(final ExtentReports extentReports, final Configuration configuration) {
        LOCK.lock();

        try {
            final TestBook testBook = configuration.getApplication().getTestBook();
            final TestBookStatistics statistics = testBook.getStatistics();
            final TestBookStatistics.Percentages percentages = statistics.getPercentages();
            final int total = testBook.getTests().size();
            final int unmappedTestsTotal = testBook.getUnmappedTests().size();
            final int grandTotal = total + unmappedTestsTotal;
            log.debug("Updating testBook percentages");

            final double successful = statistics.getSuccessful().doubleValue();
            final double failed = statistics.getFailed().doubleValue();
            final double aborted = statistics.getAborted().doubleValue();
            final double disabled = statistics.getDisabled().doubleValue();
            final double grandTotalSuccessful = statistics.getGrandTotalSuccessful().doubleValue();
            final double grandTotalFailed = statistics.getGrandTotalFailed().doubleValue();
            final double grandTotalAborted = statistics.getGrandTotalAborted().doubleValue();
            final double grandTotalDisabled = statistics.getGrandTotalDisabled().doubleValue();

            statistics.getGrandTotal().set(grandTotal);
            statistics.getNotRun().set((int) (total - successful - failed - aborted - disabled));
            statistics.getGrandTotalNotRun().set((int) (grandTotal - grandTotalSuccessful - grandTotalFailed - grandTotalAborted - grandTotalDisabled));

            final double successfulPercentage = successful / total * 100;
            final double failedPercentage = failed / total * 100;
            final double abortedPercentage = aborted / total * 100;
            final double disabledPercentage = disabled / total * 100;
            final double notRunPercentage = statistics.getNotRun().doubleValue() / total * 100;
            final double grandTotalSuccessfulPercentage = grandTotalSuccessful / grandTotal * 100;
            final double grandTotalFailedPercentage = grandTotalFailed / grandTotal * 100;
            final double grandTotalAbortedPercentage = grandTotalAborted / grandTotal * 100;
            final double grandTotalDisabledPercentage = grandTotalDisabled / grandTotal * 100;
            final double grandTotalNotRunPercentage = statistics.getNotRun().doubleValue() / grandTotal * 100;

            percentages.setTests(total);
            percentages.setUnmappedTests(unmappedTestsTotal);
            percentages.setSuccessful(successfulPercentage);
            percentages.setFailed(failedPercentage);
            percentages.setAborted(abortedPercentage);
            percentages.setDisabled(disabledPercentage);
            percentages.setNotRun(notRunPercentage);
            percentages.setGrandTotalSuccessful(grandTotalSuccessfulPercentage);
            percentages.setGrandTotalFailed(grandTotalFailedPercentage);
            percentages.setGrandTotalAborted(grandTotalAbortedPercentage);
            percentages.setGrandTotalDisabled(grandTotalDisabledPercentage);
            percentages.setGrandTotalNotRun(grandTotalNotRunPercentage);

            log.debug("Percentages are: successful {}, failed {}, aborted {}, disabled {}, not run {}",
                    successfulPercentage, failedPercentage, abortedPercentage, disabledPercentage, notRunPercentage);
            log.debug("Grand Total Percentages are: successful {}, failed {}, aborted {}, disabled {}, not run {}",
                    grandTotalSuccessfulPercentage, grandTotalFailedPercentage, grandTotalAbortedPercentage, grandTotalDisabledPercentage, grandTotalNotRunPercentage);

            testBook.getReporters().forEach(reporter -> reporter.updateWith(testBook));
            extentReports.flush();
        } finally {
            LOCK.unlock();
        }
    }
}
