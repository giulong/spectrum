package io.github.giulong.spectrum.extensions.resolvers.bidi;

import lombok.Getter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.LogInspector;

@Getter
public class LogInspectorResolver extends BiDiTypeBasedParameterResolver<LogInspector> {

    public static final String LOG_INSPECTOR = "LOG_INSPECTOR";

    private final String key = LOG_INSPECTOR;

    private final Class<LogInspector> type = LogInspector.class;

    @Override
    public LogInspector resolveParameterFor(final WebDriver driver) {
        return new LogInspector(driver);
    }
}
