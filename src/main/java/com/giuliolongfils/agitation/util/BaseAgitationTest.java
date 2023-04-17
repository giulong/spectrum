package com.giuliolongfils.agitation.util;

import com.giuliolongfils.agitation.client.Data;
import com.giuliolongfils.agitation.interfaces.AgitationExtension;
import com.giuliolongfils.agitation.internal.EventListener;
import com.giuliolongfils.agitation.internal.Util;
import com.giuliolongfils.agitation.interfaces.Endpoint;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Slf4j
@TestInstance(PER_CLASS)
@AgitationExtension
public abstract class BaseAgitationTest extends TakesScreenshots {

    protected static Configuration configuration;
    protected static Data data;
    protected static EventListener eventListener;
    protected static SystemProperties systemProperties;
    protected List<AgitationPage> agitationPages;

    @AfterAll
    public void baseAgitationTestAfterAll() {
        log.info("After the execution, you'll find the '{}' report at file:///{}", configuration.getExtent().getReportName(), Util.getReportPath(systemProperties));
    }

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
