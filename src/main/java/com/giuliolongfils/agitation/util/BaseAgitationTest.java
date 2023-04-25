package com.giuliolongfils.agitation.util;

import com.giuliolongfils.agitation.client.Data;
import com.giuliolongfils.agitation.extensions.AgitationExtension;
import com.giuliolongfils.agitation.extensions.resolvers.*;
import com.giuliolongfils.agitation.interfaces.Endpoint;
import com.giuliolongfils.agitation.internal.EventsListener;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
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
public abstract class BaseAgitationTest extends TakesScreenshots {

    @RegisterExtension
    public static final AgitationExtension AGITATION_EXTENSION = new AgitationExtension();

    @RegisterExtension
    public static final SystemPropertiesResolver SYSTEM_PROPERTIES_RESOLVER = new SystemPropertiesResolver();

    @RegisterExtension
    public static final AgitationUtilResolver AGITATION_UTIL_RESOLVER = new AgitationUtilResolver(
            SYSTEM_PROPERTIES_RESOLVER.getSystemProperties()
    );

    @RegisterExtension
    public static final ConfigurationResolver CONFIGURATION_RESOLVER = new ConfigurationResolver(
            SYSTEM_PROPERTIES_RESOLVER.getSystemProperties()
    );

    @RegisterExtension
    public static final ExtentReportsResolver EXTENT_REPORTS_RESOLVER = new ExtentReportsResolver(
            SYSTEM_PROPERTIES_RESOLVER.getSystemProperties(),
            CONFIGURATION_RESOLVER.getConfiguration().getExtent()
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
    public static final ExtentTestResolver EXTENT_TEST_RESOLVER = new ExtentTestResolver(
            EXTENT_REPORTS_RESOLVER.getExtentReports(),
            AGITATION_UTIL_RESOLVER.getAgitationUtil()
    );

    @RegisterExtension
    public static final DataResolver DATA_RESOLVER = new DataResolver();

    @RegisterExtension
    public static final ActionsResolver ACTIONS_RESOLVER = new ActionsResolver();

    protected static Configuration configuration;
    protected static Data data;
    protected static EventsListener eventsListener;
    protected static SystemProperties systemProperties;
    protected List<AgitationPage> agitationPages;

    public void initPages() {
        final Class<?> clazz = this.getClass();
        log.debug("Initializing pages of test '{}'", clazz.getSimpleName());

        final List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

        Class<?> superClazz = clazz.getSuperclass();
        while (superClazz.getSuperclass() != BaseAgitationTest.class) {
            log.debug("Initializing also pages in superclass {}", superClazz.getSimpleName());
            fields.addAll(Arrays.asList(superClazz.getDeclaredFields()));
            superClazz = superClazz.getSuperclass();
        }

        agitationPages = fields
                .stream()
                .filter(f -> AgitationPage.class.isAssignableFrom(f.getType()))
                .map(this::initPage)
                .collect(toList());
    }

    @SneakyThrows
    public AgitationPage initPage(final Field f) {
        log.debug("Initializing page {}", f.getName());
        final AgitationPage agitationPage = (AgitationPage) f.getType().getDeclaredConstructor().newInstance();

        f.setAccessible(true);
        f.set(this, agitationPage);

        final String className = agitationPage.getClass().getSimpleName();
        log.debug("BeforeAll hook: injecting already resolved fields into an instance of {}", className);

        agitationPage.agitationUtil = agitationUtil;
        agitationPage.configuration = configuration;
        agitationPage.data = data;
        agitationPage.systemProperties = systemProperties;

        final Endpoint endpointAnnotation = agitationPage.getClass().getAnnotation(Endpoint.class);
        final String endpointValue = endpointAnnotation != null ? endpointAnnotation.value() : "";
        log.debug("The endpoint of the page {} is '{}'", className, endpointValue);
        agitationPage.endpoint = endpointValue;

        return agitationPage;
    }
}
