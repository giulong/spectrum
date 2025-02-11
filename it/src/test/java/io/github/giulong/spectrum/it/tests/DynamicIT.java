package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.it.pages.DynamicControlsPage;
import io.github.giulong.spectrum.it.pages.DynamicLoadingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dynamic elements")
class DynamicIT extends BaseIT {

    @SuppressWarnings("unused")
    private DynamicLoadingPage dynamicLoadingPage;

    @SuppressWarnings("unused")
    private DynamicControlsPage dynamicControlsPage;

    @Test
    @DisplayName("navigation to prove auto-wait helps a lot")
    void autoWait() {
        dynamicLoadingPage.open().openExample1().getStart().click();

        assertTrue(dynamicLoadingPage.getLoading().isDisplayed());
        assertEquals("div", dynamicLoadingPage.getFinish().getTagName());
        assertEquals("Hello World!", dynamicLoadingPage.getFinish().getText());
        assertFalse(dynamicLoadingPage.getLoading().isDisplayed());

        driver.navigate().back();
        dynamicLoadingPage.openExample2().getStart().click();

        assertTrue(dynamicLoadingPage.getLoading().isDisplayed());
        assertEquals("div", dynamicLoadingPage.getFinish().getTagName());
        assertEquals("Hello World!", dynamicLoadingPage.getFinish().getText());
        assertFalse(dynamicLoadingPage.getLoading().isDisplayed());

        dynamicControlsPage.open();
        assertTrue(dynamicControlsPage.getCheckbox().isDisplayed());

        dynamicControlsPage.getAddRemove().click();
        assertTrue(dynamicControlsPage.getLoading().isDisplayed());

        // This works thanks to the auto-wait, since the message appears after a few seconds.
        // Without the auto-wait, the same could be achieved with the explicit expected condition commented below.
        assertEquals("It's gone!", dynamicControlsPage.getMessage().getText());
        //pageLoadWait.until(textToBePresentInElement(dynamicControlsPage.getMessage(), "It's gone!"));
        assertFalse(dynamicControlsPage.getLoading().isDisplayed());

        dynamicControlsPage.getAddRemove().click();
        assertTrue(dynamicControlsPage.getLoading().isDisplayed());

        // This works thanks to the auto-wait, since the message appears after a few seconds.
        // Without the auto-wait, the same could be achieved with the explicit expected condition commented below.
        assertEquals("It's back!", dynamicControlsPage.getMessage().getText());
        //pageLoadWait.until(textToBePresentInElement(dynamicControlsPage.getMessage(), "It's back!"));
        assertFalse(dynamicControlsPage.getLoading().isDisplayed());

        assertFalse(dynamicControlsPage.getInput().isEnabled());
        dynamicControlsPage.getEnableDisable().click();

        // This works thanks to the auto-wait, since the message appears after a few seconds.
        // Without the auto-wait, the same could be achieved with the explicit expected condition commented below.
        assertEquals("It's enabled!", dynamicControlsPage.getMessage().getText());
        //pageLoadWait.until(textToBePresentInElement(dynamicControlsPage.getMessage(), "It's enabled!"));

        dynamicControlsPage.getEnableDisable().click();

        // This works thanks to the auto-wait, since the message appears after a few seconds.
        // Without the auto-wait, the same could be achieved with the explicit expected condition commented below.
        assertEquals("It's disabled!", dynamicControlsPage.getMessage().getText());
        //pageLoadWait.until(textToBePresentInElement(dynamicControlsPage.getMessage(), "It's disabled!"));
    }
}
