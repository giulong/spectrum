package io.github.giulong.spectrum.utils;

import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.interfaces.JsonSchemaTypes;
import io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators.ConfigurationInterpolator;
import io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators.EnvironmentInterpolator;
import io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators.PropertiesInterpolator;
import io.github.giulong.spectrum.utils.environments.Environment;
import io.github.giulong.spectrum.utils.events.EventsConsumer;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import io.github.giulong.spectrum.utils.tests_comparators.TestsComparator;
import io.github.giulong.spectrum.utils.video.Video;

import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.openqa.selenium.chromium.ChromiumDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;

@SuppressWarnings("unused")
@Getter
@NoArgsConstructor(access = PRIVATE)
public class Configuration {

    private static final Configuration INSTANCE = new Configuration();

    @Setter
    @JsonIgnore
    @JsonPropertyDescription("Generic configuration")
    private Config config;

    @JsonPropertyDescription("Common vars to interpolate other String values in the configuration")
    private Map<String, Object> vars;

    @JsonPropertyDescription("Variables related to the runtime environment, meaning the machine where the tests will run, for example your local pc or a remote server")
    private Runtime runtime;

    @JsonPropertyDescription("Application under test")
    private Application application;

    @JsonPropertyDescription("Execution video recording")
    private Video video;

    @JsonPropertyDescription("Execution summary")
    private Summary summary;

    @JsonPropertyDescription("Extent Report configuration")
    private Extent extent;

    @JsonPropertyDescription("Environments configuration")
    private Environments environments;

    @JsonPropertyDescription("Drivers configuration")
    private Drivers drivers;

    @JsonPropertyDescription("Data models")
    private Data data;

    @JsonPropertyDescription("TestBook (coverage)")
    @JsonSerialize(using = ToStringSerializer.class)
    private TestBook testBook;

    @JsonPropertyDescription("FreeMarker template engine configuration. See https://freemarker.apache.org/")
    private FreeMarker freeMarker;

    @JsonPropertyDescription("Datafaker configuration. See https://www.datafaker.net/documentation/getting-started/")
    private Faker faker;

    @JsonPropertyDescription("Events consumers, such as those to send email notifications, for example")
    private List<EventsConsumer> eventsConsumers;

    public static Configuration getInstance() {
        return INSTANCE;
    }

    @Getter
    @Generated
    public static class Config {

        @JsonPropertyDescription("Configuration keys interpolators")
        private Interpolators interpolators;

        @Getter
        @Generated
        public static class Interpolators {

            @JsonPropertyDescription("Environment variables interpolator")
            private EnvironmentInterpolator environment;

            @JsonPropertyDescription("Properties interpolator")
            private PropertiesInterpolator properties;

            @JsonPropertyDescription("In-place configuration file interpolator")
            private ConfigurationInterpolator configuration;
        }
    }

    @Getter
    @Generated
    public static class Runtime {

        @JsonPropertyDescription("Profiles to be activated. By default, it's 'local'")
        private String profiles;

        @JsonSerialize(using = ToStringSerializer.class)
        @JsonSchemaTypes(String.class)
        @JsonPropertyDescription("Active runtime environment")
        private Environment environment;

        @JsonSerialize(using = ToStringSerializer.class)
        @JsonSchemaTypes(String.class)
        @JsonPropertyDescription("Driver to use")
        private Driver<?, ?, ?> driver;

        @JsonPropertyDescription("Folder where you will store files to be checked against downloaded ones")
        private String filesFolder;

        @JsonPropertyDescription("Destination folder for files downloaded during the execution")
        private String downloadsFolder;

        @JsonPropertyDescription("Cache folder for storing Spectrum internal cross-executions data")
        private String cacheFolder;
    }

    @Getter
    @Generated
    public static class Application {

        @JsonPropertyDescription("Application's under test base url")
        private String baseUrl;

        @JsonPropertyDescription("Highlight the web elements the test interacts with. Useful to visually debug the execution")
        private Highlight highlight;

        @Getter
        @Generated
        public static class Highlight {

            @JsonIgnore
            @JacksonInject("enabledFromClient")
            private boolean enabled;

            @JsonPropertyDescription("Path to the js used to highlight. Relative to the resources folder")
            private String js;
        }
    }

    @Getter
    @Generated
    public static class Extent {

        @JsonPropertyDescription("Title of the html page")
        private String documentTitle;

        @JsonPropertyDescription("Where to generate the report")
        private String reportFolder;

        @JsonPropertyDescription("Name shown in the header of the report")
        private String reportName;

        @JsonPropertyDescription("Name of the report file .You can use the ${timestamp} placeholder, which will be resolved at runtime")
        private String fileName;

        @JsonPropertyDescription("Theme used. Can be STANDARD or DARK")
        private String theme;

        @JsonPropertyDescription("Timestamp of each test's start-time and end-time")
        private String timeStampFormat;

        @JsonPropertyDescription("Regex to extract the WebElement's selector, when the driver fires an event")
        private String locatorRegex;

        @JsonPropertyDescription("Retention rules configuration")
        private Retention retention;

        @JsonPropertyDescription("Set to true if you want the report to be automatically opened when the suite execution is finished")
        private boolean openAtEnd;

        @JsonPropertyDescription("Path to the custom css to apply. Relative to the resources folder")
        private String css;

        @JsonPropertyDescription("Path to the custom js to apply. Relative to the resources folder")
        private String js;

        @JsonPropertyDescription("How to sort tests in the produced report")
        private TestsComparator sort;
    }

    @Getter
    @Generated
    public static class Drivers {

        @JsonPropertyDescription("Whether to keep the driver open after the execution")
        private boolean keepOpen;

        @JsonPropertyDescription("Whether to enable the BiDi protocol instead of CDP")
        private boolean biDi;

        @JsonPropertyDescription("Driver's fluent waits")
        private Waits waits;

        @JsonPropertyDescription("Chrome capabilities. See: https://www.selenium.dev/documentation/webdriver/browsers/chrome/")
        private Chrome chrome;

        @JsonPropertyDescription("Firefox capabilities. See: https://www.selenium.dev/documentation/webdriver/browsers/firefox/")
        private Firefox firefox;

        @JsonPropertyDescription("Edge capabilities. See: https://www.selenium.dev/documentation/webdriver/browsers/edge/")
        private Edge edge;

        @JsonPropertyDescription("Safari capabilities. See: https://www.selenium.dev/documentation/webdriver/browsers/safari/")
        private Safari safari;

        @JsonPropertyDescription("Android UiAutomator2 capabilities. See: https://github.com/appium/appium-uiautomator2-driver#capabilities")
        private UiAutomator2 uiAutomator2;

        @JsonPropertyDescription("Android Espresso capabilities. See: https://github.com/appium/appium-espresso-driver#capabilities")
        private Espresso espresso;

        @JsonPropertyDescription("XCUITest capabilities. See: https://github.com/appium/appium-xcuitest-driver")
        private XCUITest xcuiTest;

        @JsonPropertyDescription("Windows capabilities. See: https://github.com/appium/appium-windows-driver")
        private Windows windows;

        @JsonPropertyDescription("Mac2 capabilities. See: https://github.com/appium/appium-mac2-driver")
        private Mac2 mac2;

        @JsonPropertyDescription("Appium generic capabilities. See: https://github.com/appium/java-client#drivers-support")
        private AppiumGeneric appiumGeneric;

        @JsonPropertyDescription("Driver's internal logging levels")
        private Logs logs;

        @JsonPropertyDescription("Events fired by the driver, automatically logged and added to the report according to the log level set when running the suite")
        private Events events;

        @Getter
        @Generated
        public static class Waits {

            @JsonPropertyDescription("Seconds Selenium waits before throwing a NoSuchElementException when an element isn't found")
            @JsonSchemaTypes(int.class)
            private Duration implicit;

            @JsonPropertyDescription("Seconds that Selenium waits before throwing an exception because the page wasn't fully loaded yet")
            @JsonSchemaTypes(int.class)
            private Duration pageLoadTimeout;

            @JsonPropertyDescription("Seconds that Selenium waits before throwing a ScriptTimeoutException")
            @JsonSchemaTypes(int.class)
            private Duration scriptTimeout;

            @JsonPropertyDescription("WebDriverWait injected in test classes/pages that you can use on file download")
            @JsonSchemaTypes(int.class)
            private Duration downloadTimeout;

            @JsonPropertyDescription("Auto-wait configuration")
            private AutoWait auto;

            @Getter
            @Generated
            public static class AutoWait {

                @JsonPropertyDescription("Whether to enable the auto-wait. True by default")
                private boolean enabled;

                @JsonPropertyDescription("Timeout in seconds")
                @JsonSchemaTypes(int.class)
                public Duration timeout;
            }
        }

        public interface BiDiDriverConfiguration {
            boolean isBiDi();
        }

        @Getter
        @Generated
        public static class Chrome implements BiDiDriverConfiguration {

            @JsonPropertyDescription("Whether to enable the BiDi protocol instead of CDP")
            private boolean biDi;

            @JsonPropertyDescription("Chrome's args")
            private List<String> args;

            @JsonPropertyDescription("Chrome's capabilities")
            private Map<String, Object> capabilities;

            @JsonPropertyDescription("Chrome's experimental options")
            private Map<String, Object> experimentalOptions;

            @JsonPropertyDescription("Chrome service options")
            private Service service;

            @Getter
            @Generated
            public static class Service {
                private boolean buildCheckDisabled;
                private boolean appendLog;
                private boolean readableTimestamp;
                private ChromiumDriverLogLevel logLevel;
                private boolean silent;
                private boolean verbose;
                private String allowedListIps;
            }
        }

        @Getter
        @Generated
        public static class Firefox implements BiDiDriverConfiguration {

            @JsonPropertyDescription("Whether to enable the BiDi protocol instead of CDP")
            private boolean biDi;

            @JsonPropertyDescription("Absolute path to the custom Firefox binary to use")
            private String binary;

            @JsonPropertyDescription("Firefox's args")
            private List<String> args;

            @JsonPropertyDescription("Firefox's preferences")
            private Map<String, Object> preferences;

            @JsonPropertyDescription("Firefox's capabilities")
            private Map<String, Object> capabilities;

            @JsonPropertyDescription("Firefox service options")
            private Service service;

            @Getter
            @Generated
            public static class Service {
                private String allowHosts;
                private FirefoxDriverLogLevel logLevel;
                private boolean truncatedLogs;

                @JsonSchemaTypes(String.class)
                private File profileRoot;
            }
        }

        @Getter
        @Generated
        public static class Edge implements BiDiDriverConfiguration {

            @JsonPropertyDescription("Whether to enable the BiDi protocol instead of CDP")
            private boolean biDi;

            @JsonPropertyDescription("Edge's args")
            private List<String> args;

            @JsonPropertyDescription("Edge's capabilities")
            private Map<String, Object> capabilities;

            @JsonPropertyDescription("Edge's experimental options")
            private Map<String, Object> experimentalOptions;

            @JsonPropertyDescription("Edge service options")
            private Chrome.Service service;
        }

        @Getter
        @Generated
        public static class Safari {

            @JsonPropertyDescription("Safari service options")
            private Service service;

            @Getter
            @Generated
            public static class Service {
                private boolean logging;
            }
        }

        @Getter
        @Generated
        public static class UiAutomator2 {

            @JsonPropertyDescription("Android UiAutomator2's capabilities")
            private Map<String, Object> capabilities;
        }

        @Getter
        @Generated
        public static class Espresso {

            @JsonPropertyDescription("Android Espresso's capabilities")
            private Map<String, Object> capabilities;
        }

        @Getter
        @Generated
        public static class XCUITest {

            @JsonPropertyDescription("iOS XCUITest's capabilities")
            private Map<String, Object> capabilities;
        }

        @Getter
        @Generated
        public static class Windows {

            @JsonPropertyDescription("Windows' capabilities")
            private Map<String, Object> capabilities;
        }

        @Getter
        @Generated
        public static class Mac2 {

            @JsonPropertyDescription("Mac2's capabilities")
            private Map<String, Object> capabilities;
        }

        @Getter
        @Generated
        public static class AppiumGeneric {

            @JsonPropertyDescription("Appium generic's capabilities")
            private Map<String, Object> capabilities;
        }

        @Getter
        @Generated
        public static class Logs {

            @JsonPropertyDescription("The level at which driver's logs will be logged in Spectrum (execution) logs")
            @JsonSchemaTypes(value = String.class, valueList = {"ERROR", "WARN", "INFO", "DEBUG", "TRACE"})
            private org.slf4j.event.Level level;

            @JsonSchemaTypes(value = String.class, valueList = {"OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL"})
            private Level browser;

            @JsonSchemaTypes(value = String.class, valueList = {"OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL"})
            private Level driver;

            @JsonSchemaTypes(value = String.class, valueList = {"OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL"})
            private Level performance;
        }

        @Getter
        @Generated
        public static class Events {
            private Event beforeAnyCall;
            private Event afterAnyCall;
            private Event onError;
            private Event beforeAnyWebDriverCall;
            private Event afterAnyWebDriverCall;
            private Event beforeGet;
            private Event afterGet;
            private Event beforeGetCurrentUrl;
            private Event afterGetCurrentUrl;
            private Event beforeGetTitle;
            private Event afterGetTitle;
            private Event beforeFindElement;
            private Event afterFindElement;
            private Event beforeFindElements;
            private Event afterFindElements;
            private Event beforeGetPageSource;
            private Event afterGetPageSource;
            private Event beforeClose;
            private Event afterClose;
            private Event beforeQuit;
            private Event afterQuit;
            private Event beforeGetWindowHandles;
            private Event afterGetWindowHandles;
            private Event beforeGetWindowHandle;
            private Event afterGetWindowHandle;
            private Event beforeExecuteScript;
            private Event afterExecuteScript;
            private Event beforeExecuteAsyncScript;
            private Event afterExecuteAsyncScript;
            private Event beforePerform;
            private Event afterPerform;
            private Event beforeResetInputState;
            private Event afterResetInputState;
            private Event beforeAnyWebElementCall;
            private Event afterAnyWebElementCall;
            private Event beforeClick;
            private Event afterClick;
            private Event beforeSubmit;
            private Event afterSubmit;
            private Event beforeSendKeys;
            private Event afterSendKeys;
            private Event beforeClear;
            private Event afterClear;
            private Event beforeGetTagName;
            private Event afterGetTagName;
            private Event beforeGetAttribute;
            private Event afterGetAttribute;
            private Event beforeIsSelected;
            private Event afterIsSelected;
            private Event beforeIsEnabled;
            private Event afterIsEnabled;
            private Event beforeGetText;
            private Event afterGetText;
            private Event beforeFindWebElement;
            private Event afterFindWebElement;
            private Event beforeFindWebElements;
            private Event afterFindWebElements;
            private Event beforeIsDisplayed;
            private Event afterIsDisplayed;
            private Event beforeGetLocation;
            private Event afterGetLocation;
            private Event beforeGetSize;
            private Event afterGetSize;
            private Event beforeGetCssValue;
            private Event afterGetCssValue;
            private Event beforeAnyNavigationCall;
            private Event afterAnyNavigationCall;
            private Event beforeTo;
            private Event afterTo;
            private Event beforeBack;
            private Event afterBack;
            private Event beforeForward;
            private Event afterForward;
            private Event beforeRefresh;
            private Event afterRefresh;
            private Event beforeAnyAlertCall;
            private Event afterAnyAlertCall;
            private Event beforeAccept;
            private Event afterAccept;
            private Event beforeDismiss;
            private Event afterDismiss;
            private Event beforeAnyOptionsCall;
            private Event afterAnyOptionsCall;
            private Event beforeAddCookie;
            private Event afterAddCookie;
            private Event beforeDeleteCookieNamed;
            private Event afterDeleteCookieNamed;
            private Event beforeDeleteCookie;
            private Event afterDeleteCookie;
            private Event beforeDeleteAllCookies;
            private Event afterDeleteAllCookies;
            private Event beforeGetCookies;
            private Event afterGetCookies;
            private Event beforeGetCookieNamed;
            private Event afterGetCookieNamed;
            private Event beforeAnyTimeoutsCall;
            private Event afterAnyTimeoutsCall;
            private Event beforeImplicitlyWait;
            private Event afterImplicitlyWait;
            private Event beforeScriptTimeout;
            private Event afterScriptTimeout;
            private Event beforePageLoadTimeout;
            private Event afterPageLoadTimeout;
            private Event beforeAnyWindowCall;
            private Event afterAnyWindowCall;
            private Event beforeGetWindowSize;
            private Event afterGetWindowSize;
            private Event beforeSetSize;
            private Event afterSetSize;
            private Event beforeGetPosition;
            private Event afterGetPosition;
            private Event beforeSetPosition;
            private Event afterSetPosition;
            private Event beforeMaximize;
            private Event afterMaximize;
            private Event beforeFullscreen;
            private Event afterFullscreen;
            private Event beforeAnyTargetLocatorCall;
            private Event afterAnyTargetLocatorCall;
            private Event beforeFrame;
            private Event afterFrame;
            private Event beforeParentFrame;
            private Event afterParentFrame;
            private Event beforeWindow;
            private Event afterWindow;
            private Event beforeNewWindow;
            private Event afterNewWindow;
            private Event beforeDefaultContent;
            private Event afterDefaultContent;
            private Event beforeActiveElement;
            private Event afterActiveElement;
            private Event beforeAlert;
            private Event afterAlert;
        }

        @Getter
        @Generated
        public static class Event {

            @JsonPropertyDescription("Level at which this event will be logged")
            @JsonSchemaTypes(value = String.class, valueList = {"ERROR", "WARN", "INFO", "DEBUG", "TRACE"})
            private org.slf4j.event.Level level;

            @JsonPropertyDescription("Message to be logged upon receiving this event")
            private String message;

            @JsonPropertyDescription("Milliseconds to wait before listening to this event")
            private long wait;
        }
    }

    @Getter
    @Generated
    public static class Environments {

        @JsonPropertyDescription("Local environment configuration")
        private Local local;

        @JsonPropertyDescription("Grid environment configuration")
        private Grid grid;

        @JsonPropertyDescription("Appium environment configuration")
        private Appium appium;

        @Getter
        @Generated
        public static class Local {
        }

        @Getter
        @Generated
        public static class Grid {

            @JsonSchemaTypes(String.class)
            @JsonPropertyDescription("Url of the selenium grid")
            private URL url;

            @JsonPropertyDescription("Capabilities dedicated to executions on the grid")
            private final Map<String, Object> capabilities = new HashMap<>();

            @JsonPropertyDescription("Whether to search for files to upload on the client machine or not")
            private boolean localFileDetector;
        }

        @Getter
        @Generated
        public static class Appium extends Grid {

            @JsonPropertyDescription("Set to true to redirect server logs to Spectrum's logs, at the level specified in the drivers.logs.level node")
            private boolean collectServerLogs;

            @JsonPropertyDescription("Appium service options")
            private Service service;

            @Getter
            @Generated
            public static class Service {

                @JsonPropertyDescription("IP address of the Appium server")
                private String ipAddress;

                @JsonPropertyDescription("Sets which port the appium server should be started on. A value of 0 indicates that any free port may be used")
                private int port;

                @JsonPropertyDescription("Sets timeout in seconds")
                @JsonSchemaTypes(int.class)
                private Duration timeout;
            }
        }
    }

    @Getter
    @Generated
    public static class Data {

        @JsonPropertyDescription("sub-folder under src/test/resources where to find your data*.yaml")
        private String folder;
    }

    @Getter
    @Generated
    public static class FreeMarker {

        @JsonPropertyDescription("FreeMarker version. See https://freemarker.apache.org/docs/app_versions.html")
        private String version;

        @JsonSchemaTypes(String.class)
        @JsonPropertyDescription("Locale to be used. See https://freemarker.apache.org/docs/ref_directive_setting.html")
        private Locale locale;

        @JsonPropertyDescription("Number format to be used. See https://freemarker.apache.org/docs/ref_directive_setting.html")
        private String numberFormat;
    }

    @Getter
    @Generated
    public static class Faker {

        @JsonSchemaTypes(String.class)
        @JsonPropertyDescription("Locale to be used. See https://www.datafaker.net/documentation/getting-started/")
        private Locale locale;

        @JsonSchemaTypes(int.class)
        @JsonPropertyDescription("Random seed to be used. See https://www.datafaker.net/documentation/getting-started/")
        private Random random;
    }
}
