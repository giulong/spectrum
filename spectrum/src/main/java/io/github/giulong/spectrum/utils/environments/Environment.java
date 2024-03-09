package io.github.giulong.spectrum.utils.environments;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.interfaces.SessionHook;
import org.openqa.selenium.WebDriver;

public abstract class Environment implements SessionHook {

    public abstract WebDriver setupFor(Driver<?, ?, ?> driver);

    public abstract void shutdown();
}
