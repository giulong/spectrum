package com.github.giulong.spectrum.pojos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.giulong.spectrum.utils.events.EventHandler;
import com.github.giulong.spectrum.utils.testbook.TestBook;
import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.utils.webdrivers.Environment;
import lombok.Getter;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

@SuppressWarnings("unused")
@Getter
public class Configuration {
	private Map<String, String> vars;
	private Runtime runtime;
	private Application application;
	private Extent extent;
	private WebDriver webDriver;
	private Data data;
	private SeleniumLogs seleniumLogs;
	private FreeMarker freeMarker;
	private Events events;
	private List<EventHandler> eventHandlers;

	@Getter
	public static class Runtime {
		private String env;

		@JsonSerialize(using = ToStringSerializer.class)
		private Browser<?> browser;
		private Environment environment;
		private String filesFolder;
		private String downloadsFolder;
	}

	@Getter
	public static class Application {
		private String baseUrl;

		@JsonSerialize(using = ToStringSerializer.class)
		private TestBook testBook;
	}

	@Getter
	public static class Extent {
		private String documentTitle;
		private String reportFolder;
		private String reportName;
		private String fileName;
		private String theme;
		private String timeStampFormat;
	}

	@Getter
	public static class WebDriver {
		private Waits waits;
		private Grid grid;
		private Chrome chrome;
		private Firefox firefox;
		private Edge edge;

		@Getter
		public static class Waits {
			private Duration implicit;
			private Duration pageLoadTimeout;
			private Duration downloadTimeout;
			private Duration scriptTimeout;
		}

		@Getter
		public static class Grid {
			private URL url;
			private Map<String, String> capabilities;
		}

		@Getter
		public static class Chrome {
			private Map<String, Object> capabilities;
			private List<String> arguments;
			private Map<String, Object> experimentalOptions;
		}

		@Getter
		public static class Firefox {
			private List<String> args;
			private FirefoxDriverLogLevel logLevel;
			private Map<String, Object> preferences;
		}

		@Getter
		public static class Edge {
			private Map<String, Object> capabilities;
		}
	}

	@Getter
	public static class Data {
		private String fqdn;
	}

	@Getter
	public static class SeleniumLogs {
		private Level browser;
		private Level client;
		private Level driver;
		private Level performance;
		private Level profiler;
		private Level server;
	}

	@Getter
	public static class FreeMarker {
		private String version;
		private Locale locale;
		private String numberFormat;
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
		private ch.qos.logback.classic.Level level;
		private String message;
	}
}
