package io.github.giulong.spectrum.extensions.resolvers.bidi;

import lombok.Getter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.BrowsingContextInspector;

@Getter
public class BrowsingContextInspectorResolver extends BiDiTypeBasedParameterResolver<BrowsingContextInspector> {

    public static final String BROWSING_CONTEXT_INSPECTOR = "BROWSING_CONTEXT_INSPECTOR";

    private final String key = BROWSING_CONTEXT_INSPECTOR;

    private final Class<BrowsingContextInspector> type = BrowsingContextInspector.class;

    @Override
    public BrowsingContextInspector resolveParameterFor(final WebDriver driver) {
        return new BrowsingContextInspector(driver);
    }
}
