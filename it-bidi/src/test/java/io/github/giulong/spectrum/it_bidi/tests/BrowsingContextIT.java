package io.github.giulong.spectrum.it_bidi.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.bidi.browsingcontext.ReadinessState.COMPLETE;

import java.util.List;

import io.github.giulong.spectrum.SpectrumTest;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContextInfo;

public class BrowsingContextIT extends SpectrumTest<Void> {

    @Test
    void testGetTreeWithAChild() {
        browsingContext.navigate("https://www.selenium.dev/selenium/web/iframes.html", COMPLETE);

        final List<BrowsingContextInfo> contextInfoList = browsingContext.getTree();

        assertEquals(1, contextInfoList.size());

        final BrowsingContextInfo info = contextInfoList.getFirst();
        assertEquals(1, info.getChildren().size());
        assertEquals(driver.getWindowHandle(), info.getId());
        assertTrue(info.getChildren().getFirst().getUrl().contains("formPage.html"));
    }
}
