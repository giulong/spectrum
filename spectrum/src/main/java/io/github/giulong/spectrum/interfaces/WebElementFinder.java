package io.github.giulong.spectrum.interfaces;

import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public interface WebElementFinder {

    WebElement findElement(JavascriptExecutor driver, WebElement context, String locatorValue);

    List<WebElement> findElements(JavascriptExecutor driver, WebElement context, String locatorValue);
}
