package com.giuliolongfils.agitation.internal;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.agitation.client.Data;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import com.giuliolongfils.agitation.pojos.WebDriverWaits;
import com.giuliolongfils.agitation.util.AgitationUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import static com.giuliolongfils.agitation.extensions.TestExtension.EXTENT_REPORTS;
import static com.giuliolongfils.agitation.extensions.resolvers.ActionsResolver.ACTIONS;
import static com.giuliolongfils.agitation.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.giuliolongfils.agitation.extensions.resolvers.DataResolver.DATA;
import static com.giuliolongfils.agitation.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;
import static com.giuliolongfils.agitation.extensions.resolvers.AgitationUtilResolver.AGITATION_UTIL;
import static com.giuliolongfils.agitation.extensions.resolvers.SystemPropertiesResolver.SYSTEM_PROPERTIES;
import static com.giuliolongfils.agitation.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static com.giuliolongfils.agitation.extensions.resolvers.WebDriverWaitsResolver.WEB_DRIVER_WAITS;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public final class ContextManager {
    private static final ContextManager INSTANCE = new ContextManager();

    private ContextManager() {
    }

    public static ContextManager getInstance() {
        return INSTANCE;
    }

    public <T> T getObject(final String objectName, final ExtensionContext context, final Class<T> clazz) {
        log.debug("Retrieving object of type {} stored with key '{}' from extension context", clazz.getSimpleName(), objectName);
        return context.getStore(GLOBAL).get(objectName, clazz);
    }

    public void store(final String objectName, final Object object, final ExtensionContext context) {
        log.debug("Storing object of type {} with key '{}' in extension context", object.getClass().getSimpleName(), objectName);
        context.getStore(GLOBAL).put(objectName, object);
    }

    public String getTestName(final ExtensionContext context) {
        return context.getDisplayName();
    }

    public String getClassName(final ExtensionContext context) {
        final Class<?> clazz = getTestClass(context);
        final DisplayName displayName = clazz.getAnnotation(DisplayName.class);

        return displayName != null ? displayName.value() : clazz.getSimpleName();
    }

    public Class<?> getTestClass(final ExtensionContext context) {
        return context.getRequiredTestClass();
    }

    public WebDriver getWebDriver(final ExtensionContext context) {
        return getObject(WEB_DRIVER, context, WebDriver.class);
    }

    public WebDriverWaits getWebDriverWaits(final ExtensionContext context) {
        return getObject(WEB_DRIVER_WAITS, context, WebDriverWaits.class);
    }

    public ExtentTest getExtentTest(final ExtensionContext context) {
        return getObject(EXTENT_TEST, context, ExtentTest.class);
    }

    public Configuration getConfiguration(final ExtensionContext context) {
        return getObject(CONFIGURATION, context, Configuration.class);
    }

    public Data getData(final ExtensionContext context) {
        return getObject(DATA, context, Data.class);
    }

    public SystemProperties getSystemProperties(final ExtensionContext context) {
        return getObject(SYSTEM_PROPERTIES, context, SystemProperties.class);
    }

    public AgitationUtil getAgitationUtil(final ExtensionContext context) {
        return getObject(AGITATION_UTIL, context, AgitationUtil.class);
    }

    public ExtentReports getExtentReports(final ExtensionContext context) {
        return getObject(EXTENT_REPORTS, context, ExtentReports.class);
    }

    public ExtentTest createExtentTest(final ExtensionContext context, final ExtentReports extentReports) {
        final ExtentTest extentTest = extentReports.createTest(String.format("<div>%s</div>%s", getClassName(context), getTestName(context)));

        context.getStore(GLOBAL).put(EXTENT_TEST, extentTest);
        return extentTest;
    }

    public ExtentTest getOrCreateExtentTest(final ExtensionContext context) {
        final ExtentTest extentTest = getExtentTest(context);

        return extentTest != null ? extentTest : createExtentTest(context, getExtentReports(context));
    }

    public Actions getActions(final ExtensionContext context) {
        return getObject(ACTIONS, context, Actions.class);
    }
}
