package io.github.giulong.spectrum.utils.environments;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.giulong.spectrum.browsers.Browser;
import io.github.giulong.spectrum.interfaces.SessionHook;
import org.openqa.selenium.WebDriver;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @Type(value = LocalEnvironment.class, name = "local"),
        @Type(value = GridEnvironment.class, name = "grid"),
        @Type(value = AppiumEnvironment.class, name = "appium"),
})
public abstract class Environment implements SessionHook {

    public abstract WebDriver setupFor(Browser<?, ?, ?> browser);

    public abstract void shutdown();
}
