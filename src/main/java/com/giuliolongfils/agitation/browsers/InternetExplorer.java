package com.giuliolongfils.agitation.browsers;

import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;

import static io.github.bonigarcia.wdm.WebDriverManager.iedriver;

public class InternetExplorer extends Browser<InternetExplorerOptions> {

    @Override
    public boolean exposesConsole() {
        return false;
    }

    @Override
    public boolean takesPartialScreenshots() {
        return false;
    }

    @Override
    public WebDriverManager getWebDriverManager() {
        return iedriver();
    }

    @Override
    public String getSystemPropertyName() {
        return "webDriver.ie.driver";
    }

    @Override
    public String getDriverName() {
        return "IEDriverServer.exe";
    }

    @Override
    public WebDriver buildWebDriverFrom(final Configuration configuration, final SystemProperties systemProperties) {
        capabilities = new InternetExplorerOptions();
        final Configuration.WebDriver.InternetExplorer ieConfig = configuration.getWebDriver().getIe();
        ieConfig.getCapabilities().forEach(capabilities::setCapability);

        return new InternetExplorerDriver(capabilities);
    }

    @Override
    public void mergeGridCapabilities(final Configuration.WebDriver.Grid gridConfiguration) {
        gridConfiguration.getCapabilities().forEach(capabilities::setCapability);
    }
}
