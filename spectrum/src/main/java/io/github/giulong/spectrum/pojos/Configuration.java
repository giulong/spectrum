package io.github.giulong.spectrum.pojos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.github.giulong.spectrum.browsers.Browser;
import io.github.giulong.spectrum.interfaces.JsonSchemaTypes;
import io.github.giulong.spectrum.utils.Retention;
import io.github.giulong.spectrum.utils.events.EventsConsumer;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import io.github.giulong.spectrum.utils.video.Video;
import io.github.giulong.spectrum.utils.webdrivers.Environment;
import lombok.Getter;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import static ch.qos.logback.classic.Level.OFF;

@SuppressWarnings({"unused", "FieldMayBeFinal"})
@Getter
public class Configuration {

    @JsonPropertyDescription("Common vars to interpolate other String values in the configuration")
    private Map<String, Object> vars;

    @JsonPropertyDescription("Variables related to the runtime environment, meaning the machine where the tests will run, for example your local pc or a remote server")
    private Runtime runtime;

    @JsonPropertyDescription("Application under test")
    private Application application;

    @JsonPropertyDescription("Execution video recording")
    private Video video;

    @JsonPropertyDescription("Extent Report configuration")
    private Extent extent;

    @JsonPropertyDescription("WebDriver configuration")
    private WebDriver webDriver;

    @JsonPropertyDescription("Data models")
    private Data data;

    @JsonPropertyDescription("TestBook (coverage)")
    @JsonSerialize(using = ToStringSerializer.class)
    private TestBook testBook;

    @JsonPropertyDescription("FreeMarker template engine configuration. See https://freemarker.apache.org/")
    private FreeMarker freeMarker;

    @JsonPropertyDescription("Events consumers, such as those to send email notifications, for example")
    private List<EventsConsumer> eventsConsumers;

    @Getter
    public static class Runtime {

        @JsonPropertyDescription("profiles to be activated. By default, it's 'local'")
        private String profiles;

        @JsonSerialize(using = ToStringSerializer.class)
        @JsonSchemaTypes(String.class)
        @JsonPropertyDescription("browser to use")
        private Browser<?, ?, ?> browser;

        @JsonPropertyDescription("Runtime environment. Can be local or grid")
        private Environment environment;

        @JsonPropertyDescription("folder where you will store files to be checked against downloaded ones")
        private String filesFolder;

        @JsonPropertyDescription("destination folder for files downloaded during the execution")
        private String downloadsFolder;
    }

    @Getter
    public static class Application {

        @JsonPropertyDescription("Application's under test base url")
        private String baseUrl;
    }

    @Getter
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

        @JsonPropertyDescription("Regex to extract the WebElement's selector, when the webDriver fires an event")
        private String locatorRegex;

        @JsonPropertyDescription("Retention rules configuration")
        private Retention retention;
    }

    @Getter
    public static class WebDriver {

        @JsonPropertyDescription("WebDriver's fluent waits")
        private Waits waits;

        @JsonPropertyDescription("Chrome capabilities. See: https://chromedriver.chromium.org/capabilities")
        private Chrome chrome;

        @JsonPropertyDescription("Firefox capabilities. See: https://developer.mozilla.org/en-US/docs/Web/WebDriver/Capabilities/firefoxOptions")
        private Firefox firefox;

        @JsonPropertyDescription("Edge capabilities. See: https://learn.microsoft.com/en-us/microsoft-edge/webDriver-chromium/capabilities-edge-options")
        private Edge edge;

        @JsonPropertyDescription("WebDriver's internal logging levels")
        private Logs logs;

        @JsonPropertyDescription("Events fired by the webDriver, automatically logged and added to the report according to the log level set when running the suite")
        private Events events;

        @Getter
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

            @JsonPropertyDescription("FluentWait injected in test classes/pages that you can use on file download")
            @JsonSchemaTypes(int.class)
            private Duration downloadTimeout;
        }

        @Getter
        public static class Chrome {
            private List<String> args;
            private Map<String, Object> capabilities;
        }

        @Getter
        public static class Firefox {
            private List<String> args;
            private FirefoxDriverLogLevel logLevel;
            private Map<String, Object> preferences;
        }

        @Getter
        public static class Edge {
            private List<String> args;
            private Map<String, Object> capabilities;
        }

        @Getter
        public static class Logs {

            @JsonPropertyDescription("The level at which webDriver's logs will be logged in Spectrum (execution) logs")
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
            private Event beforeSetScriptTimeout;
            private Event afterSetScriptTimeout;
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
        }

        @Getter
        public static class Event {

            @JsonPropertyDescription("Level at which this event will be logged")
            @JsonSchemaTypes(value = String.class, valueList = {"OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"})
            private ch.qos.logback.classic.Level level = OFF;

            @JsonPropertyDescription("Message to be logged upon receiving this event")
            private String message;
        }
    }

    @Getter
    public static class Data {

        @JsonPropertyDescription("subfolder under src/test/resources where to find your data*.yaml")
        private String folder;

        @JsonPropertyDescription("you need to provide the fully qualified name of your Data class, meaning its package name AND class name")
        private String fqdn;
    }

    @Getter
    public static class FreeMarker {
        private String version;

        @JsonSchemaTypes(String.class)
        private Locale locale;

        private String numberFormat;
    }
}
