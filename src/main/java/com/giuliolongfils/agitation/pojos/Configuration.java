package com.giuliolongfils.agitation.pojos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@SuppressWarnings("unused")
@Getter
@EqualsAndHashCode
public class Configuration {

	private Application application;
	private Extent extent;
	private WebDriver webDriver;
	private Data data;
	private SeleniumLogs seleniumLogs;
	private Events events;

	@Getter
	@EqualsAndHashCode
	public static class Application {
		private String baseUrl;
		private String driversPath;
		private String filesFolder;
		private String downloadsFolder;
	}

	@Getter
	@EqualsAndHashCode
	public static class Extent {
		private String documentTitle;
		private String reportFolder;
		private String reportName;
		private String fileName;
		private String theme;
		private String timeStampFormat;
	}

	@Getter
	@EqualsAndHashCode
	public static class WebDriver {

		private long waitTimeout;
		private long pageLoadingWaitTimeout;
		private long downloadWaitTimeout;
		private long scriptWaitTimeout;
		private boolean defaultEventListenerEnabled;
		private Grid grid;
		private Chrome chrome;
		private Firefox firefox;
		private InternetExplorer ie;
		private Edge edge;

		@Getter
		@EqualsAndHashCode
		public static class Grid {
			private URL url;
			private Map<String, String> capabilities;
		}

		@Getter
		@EqualsAndHashCode
		public static class Chrome {
			private Map<String, Object> capabilities;
			private List<String> arguments;
			private Map<String, Object> experimentalOptions;
		}

		@Getter
		@EqualsAndHashCode
		public static class Firefox {
			private String binary;
			private List<String> args;
			private FirefoxDriverLogLevel logLevel;
			private Map<String, Object> preferences;
		}

		@Getter
		@EqualsAndHashCode
		public static class InternetExplorer {
			private Map<String, Object> capabilities;
		}

		@Getter
		@EqualsAndHashCode
		public static class Edge {
			private Map<String, Object> capabilities;
		}
	}

	@Getter
	@EqualsAndHashCode
	public static class Data {
		private String fqdn;
	}

	@Getter
	@EqualsAndHashCode
	public static class SeleniumLogs {
		private Level browser;
		private Level client;
		private Level driver;
		private Level performance;
		private Level profiler;
		private Level server;
	}

	@Getter
	@EqualsAndHashCode
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
	@EqualsAndHashCode
	public static class Event {
		private ch.qos.logback.classic.Level level;
		private String message;
	}
}
