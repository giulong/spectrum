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
	private SeleniumLogs seleniumLogs;
	private Events events;

	@Getter
	@EqualsAndHashCode
	public static class Application {
		private String baseUrl;
		private String driversPath;
	}

	@Getter
	@EqualsAndHashCode
	public static class Extent {
		private String documentTitle;
		private String reportName;
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
		private ch.qos.logback.classic.Level beforeAnyCall;
		private ch.qos.logback.classic.Level afterAnyCall;
		private ch.qos.logback.classic.Level onError;
		private ch.qos.logback.classic.Level beforeAnyWebDriverCall;
		private ch.qos.logback.classic.Level afterAnyWebDriverCall;
		private ch.qos.logback.classic.Level beforeGet;
		private ch.qos.logback.classic.Level afterGet;
		private ch.qos.logback.classic.Level beforeGetCurrentUrl;
		private ch.qos.logback.classic.Level afterGetCurrentUrl;
		private ch.qos.logback.classic.Level beforeGetTitle;
		private ch.qos.logback.classic.Level afterGetTitle;
		private ch.qos.logback.classic.Level beforeFindElement;
		private ch.qos.logback.classic.Level afterFindElement;
		private ch.qos.logback.classic.Level beforeFindElements;
		private ch.qos.logback.classic.Level afterFindElements;
		private ch.qos.logback.classic.Level beforeGetPageSource;
		private ch.qos.logback.classic.Level afterGetPageSource;
		private ch.qos.logback.classic.Level beforeClose;
		private ch.qos.logback.classic.Level afterClose;
		private ch.qos.logback.classic.Level beforeQuit;
		private ch.qos.logback.classic.Level afterQuit;
		private ch.qos.logback.classic.Level beforeGetWindowHandles;
		private ch.qos.logback.classic.Level afterGetWindowHandles;
		private ch.qos.logback.classic.Level beforeGetWindowHandle;
		private ch.qos.logback.classic.Level afterGetWindowHandle;
		private ch.qos.logback.classic.Level beforeExecuteScript;
		private ch.qos.logback.classic.Level afterExecuteScript;
		private ch.qos.logback.classic.Level beforeExecuteAsyncScript;
		private ch.qos.logback.classic.Level afterExecuteAsyncScript;
		private ch.qos.logback.classic.Level beforePerform;
		private ch.qos.logback.classic.Level afterPerform;
		private ch.qos.logback.classic.Level beforeResetInputState;
		private ch.qos.logback.classic.Level afterResetInputState;
		private ch.qos.logback.classic.Level beforeAnyWebElementCall;
		private ch.qos.logback.classic.Level afterAnyWebElementCall;
		private ch.qos.logback.classic.Level beforeClick;
		private ch.qos.logback.classic.Level afterClick;
		private ch.qos.logback.classic.Level beforeSubmit;
		private ch.qos.logback.classic.Level afterSubmit;
		private ch.qos.logback.classic.Level beforeSendKeys;
		private ch.qos.logback.classic.Level afterSendKeys;
		private ch.qos.logback.classic.Level beforeClear;
		private ch.qos.logback.classic.Level afterClear;
		private ch.qos.logback.classic.Level beforeGetTagName;
		private ch.qos.logback.classic.Level afterGetTagName;
		private ch.qos.logback.classic.Level beforeGetAttribute;
		private ch.qos.logback.classic.Level afterGetAttribute;
		private ch.qos.logback.classic.Level beforeIsSelected;
		private ch.qos.logback.classic.Level afterIsSelected;
		private ch.qos.logback.classic.Level beforeIsEnabled;
		private ch.qos.logback.classic.Level afterIsEnabled;
		private ch.qos.logback.classic.Level beforeGetText;
		private ch.qos.logback.classic.Level afterGetText;
		private ch.qos.logback.classic.Level beforeIsDisplayed;
		private ch.qos.logback.classic.Level afterIsDisplayed;
		private ch.qos.logback.classic.Level beforeGetLocation;
		private ch.qos.logback.classic.Level afterGetLocation;
		private ch.qos.logback.classic.Level beforeGetSize;
		private ch.qos.logback.classic.Level afterGetSize;
		private ch.qos.logback.classic.Level beforeGetCssValue;
		private ch.qos.logback.classic.Level afterGetCssValue;
		private ch.qos.logback.classic.Level beforeAnyNavigationCall;
		private ch.qos.logback.classic.Level afterAnyNavigationCall;
		private ch.qos.logback.classic.Level beforeTo;
		private ch.qos.logback.classic.Level afterTo;
		private ch.qos.logback.classic.Level beforeBack;
		private ch.qos.logback.classic.Level afterBack;
		private ch.qos.logback.classic.Level beforeForward;
		private ch.qos.logback.classic.Level afterForward;
		private ch.qos.logback.classic.Level beforeRefresh;
		private ch.qos.logback.classic.Level afterRefresh;
		private ch.qos.logback.classic.Level beforeAnyAlertCall;
		private ch.qos.logback.classic.Level afterAnyAlertCall;
		private ch.qos.logback.classic.Level beforeAccept;
		private ch.qos.logback.classic.Level afterAccept;
		private ch.qos.logback.classic.Level beforeDismiss;
		private ch.qos.logback.classic.Level afterDismiss;
		private ch.qos.logback.classic.Level beforeAnyOptionsCall;
		private ch.qos.logback.classic.Level afterAnyOptionsCall;
		private ch.qos.logback.classic.Level beforeAddCookie;
		private ch.qos.logback.classic.Level afterAddCookie;
		private ch.qos.logback.classic.Level beforeDeleteCookieNamed;
		private ch.qos.logback.classic.Level afterDeleteCookieNamed;
		private ch.qos.logback.classic.Level beforeDeleteCookie;
		private ch.qos.logback.classic.Level afterDeleteCookie;
		private ch.qos.logback.classic.Level beforeDeleteAllCookies;
		private ch.qos.logback.classic.Level afterDeleteAllCookies;
		private ch.qos.logback.classic.Level beforeGetCookies;
		private ch.qos.logback.classic.Level afterGetCookies;
		private ch.qos.logback.classic.Level beforeGetCookieNamed;
		private ch.qos.logback.classic.Level afterGetCookieNamed;
		private ch.qos.logback.classic.Level beforeAnyTimeoutsCall;
		private ch.qos.logback.classic.Level afterAnyTimeoutsCall;
		private ch.qos.logback.classic.Level beforeImplicitlyWait;
		private ch.qos.logback.classic.Level afterImplicitlyWait;
		private ch.qos.logback.classic.Level beforeSetScriptTimeout;
		private ch.qos.logback.classic.Level afterSetScriptTimeout;
		private ch.qos.logback.classic.Level beforePageLoadTimeout;
		private ch.qos.logback.classic.Level afterPageLoadTimeout;
		private ch.qos.logback.classic.Level beforeAnyWindowCall;
		private ch.qos.logback.classic.Level afterAnyWindowCall;
		private ch.qos.logback.classic.Level beforeSetSize;
		private ch.qos.logback.classic.Level afterSetSize;
		private ch.qos.logback.classic.Level beforeGetPosition;
		private ch.qos.logback.classic.Level afterGetPosition;
		private ch.qos.logback.classic.Level beforeSetPosition;
		private ch.qos.logback.classic.Level afterSetPosition;
		private ch.qos.logback.classic.Level beforeMaximize;
		private ch.qos.logback.classic.Level afterMaximize;
		private ch.qos.logback.classic.Level beforeFullscreen;
		private ch.qos.logback.classic.Level afterFullscreen;
	}
}
