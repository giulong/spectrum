package io.github.giulong.spectrum.interfaces;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface WebElementFinder {

    WebElement findElement(JavascriptExecutor driver, WebElement context, String locatorValue);

    List<WebElement> findElements(JavascriptExecutor driver, WebElement context, String locatorValue);
}
