package com.giuliolongfils.agitation.interfaces;

import com.giuliolongfils.agitation.extensions.TestExtension;
import com.giuliolongfils.agitation.extensions.TestWatcherExtension;
import com.giuliolongfils.agitation.extensions.resolvers.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@ExtendWith({
        TestExtension.class,
        TestWatcherExtension.class,
        WebDriverResolver.class,
        WebDriverWaitsResolver.class,
        ExtentTestResolver.class,
        AgitationUtilResolver.class,
        ConfigurationResolver.class,
        SystemPropertiesResolver.class,
        DataResolver.class,
        ActionsResolver.class,
})
public @interface AgitationExtension {
}
