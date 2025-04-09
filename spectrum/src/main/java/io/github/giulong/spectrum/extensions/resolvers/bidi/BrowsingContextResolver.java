package io.github.giulong.spectrum.extensions.resolvers.bidi;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;

@Getter
public class BrowsingContextResolver extends BiDiTypeBasedParameterResolver<BrowsingContext> {

    private final String key = "BROWSING_CONTEXT";

    private final Class<BrowsingContext> type = BrowsingContext.class;

    @Override
    public BrowsingContext resolveParameterFor(final WebDriver driver) {
        return new BrowsingContext(driver, driver.getWindowHandle());
    }
}
