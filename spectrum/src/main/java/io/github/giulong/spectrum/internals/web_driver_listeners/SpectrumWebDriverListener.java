package io.github.giulong.spectrum.internals.web_driver_listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.SuperBuilder;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.support.ui.WebDriverWait;

@SuperBuilder
public abstract class SpectrumWebDriverListener implements WebDriverListener {

    private final Dimension noSize = new Dimension(0, 0);
    private final Point noLocation = new Point(0, 0);
    private Actions actions;
    private WebDriverWait webDriverWait;
    private Pattern locatorPattern;

    String extractSelectorFrom(final WebElement webElement) {
        final String fullWebElement = webElement.toString();
        final Matcher matcher = locatorPattern.matcher(fullWebElement);

        final List<String> locators = new ArrayList<>();
        while (matcher.find()) {
            locators.add(matcher.group(1));
        }

        return String.join(" -> ", locators);
    }
}
