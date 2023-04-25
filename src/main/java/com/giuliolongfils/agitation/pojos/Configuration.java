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
		private ch.qos.logback.classic.Level beforeAnyWebElementCall;
		private ch.qos.logback.classic.Level afterAnyWebElementCall;
	}
}
