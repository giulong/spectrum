package com.giuliolongfils.spectrum.util;

import com.giuliolongfils.spectrum.extensions.SpectrumExtension;
import com.giuliolongfils.spectrum.extensions.resolvers.*;
import com.giuliolongfils.spectrum.interfaces.Endpoint;
import com.giuliolongfils.spectrum.internal.EventsListener;
import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.SystemProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Slf4j
@TestInstance(PER_CLASS)
public abstract class BaseSpectrumTest<Data> extends TakesScreenshots {

    @RegisterExtension
    public static final SpectrumExtension SPECTRUM_EXTENSION = new SpectrumExtension();

    @RegisterExtension
    public static final SystemPropertiesResolver SYSTEM_PROPERTIES_RESOLVER = new SystemPropertiesResolver();

    @RegisterExtension
    public static final ConfigurationResolver CONFIGURATION_RESOLVER = new ConfigurationResolver(
            SYSTEM_PROPERTIES_RESOLVER.getSystemProperties()
    );

    @RegisterExtension
    public static final SpectrumUtilResolver SPECTRUM_UTIL_RESOLVER = new SpectrumUtilResolver(
            SYSTEM_PROPERTIES_RESOLVER.getSystemProperties(),
            CONFIGURATION_RESOLVER.getConfiguration()
    );

    @RegisterExtension
    public static final ExtentReportsResolver EXTENT_REPORTS_RESOLVER = new ExtentReportsResolver(
            CONFIGURATION_RESOLVER.getConfiguration().getExtent()
    );

    @RegisterExtension
    public static final ExtentTestResolver EXTENT_TEST_RESOLVER = new ExtentTestResolver(
            EXTENT_REPORTS_RESOLVER.getExtentReports(),
            SPECTRUM_UTIL_RESOLVER.getSpectrumUtil()
    );

    @RegisterExtension
    public static final WebDriverResolver WEB_DRIVER_RESOLVER = new WebDriverResolver(
            SYSTEM_PROPERTIES_RESOLVER.getSystemProperties(),
            CONFIGURATION_RESOLVER.getConfiguration()
    );

    @RegisterExtension
    public static final WebDriverWaitsResolver WEB_DRIVER_WAITS_RESOLVER = new WebDriverWaitsResolver(
            CONFIGURATION_RESOLVER.getConfiguration().getWebDriver()
    );

    @RegisterExtension
    public final DataResolver<Data> dataResolver = new DataResolver<>(
            CONFIGURATION_RESOLVER.getConfiguration().getData()
    );

    @RegisterExtension
    public static final ActionsResolver ACTIONS_RESOLVER = new ActionsResolver();

    protected static Configuration configuration;
    protected Data data;
    protected static EventsListener eventsListener;
    protected static SystemProperties systemProperties;
    protected List<SpectrumPage<Data>> spectrumPages;

    public void initPages() {
        final Class<?> clazz = this.getClass();
        log.debug("Initializing pages of test '{}'", clazz.getSimpleName());

        final List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

        Class<?> superClazz = clazz.getSuperclass();
        while (superClazz.getSuperclass() != BaseSpectrumTest.class) {
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

        spectrumPage.spectrumUtil = spectrumUtil;
        spectrumPage.configuration = configuration;
        spectrumPage.data = data;
        spectrumPage.systemProperties = systemProperties;

        final Endpoint endpointAnnotation = spectrumPage.getClass().getAnnotation(Endpoint.class);
        final String endpointValue = endpointAnnotation != null ? endpointAnnotation.value() : "";
        log.debug("The endpoint of the page {} is '{}'", className, endpointValue);
        spectrumPage.endpoint = endpointValue;

        return spectrumPage;
    }
}
