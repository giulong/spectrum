package com.github.giulong.spectrum.utils.events;

import com.github.giulong.spectrum.pojos.events.Event;
import org.openqa.selenium.WebDriver;

import static com.github.giulong.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class WebDriverHandler extends EventHandler {

    public void handle(final Event event) throws InterruptedException {
        Thread.sleep(1000);
        event.getContext().getStore(GLOBAL).get(WEB_DRIVER, WebDriver.class).quit();
    }
}
