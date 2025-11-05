package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.it.data.Data;

import lombok.Getter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

@Getter
@Endpoint("dynamic_loading")
@SuppressWarnings("unused")
public class DynamicLoadingPage extends SpectrumPage<DynamicLoadingPage, Data> {

    @FindBy(linkText = "Example 1: Element on page that is hidden")
    private WebElement example1;

    @FindBy(linkText = "Example 2: Element rendered after the fact")
    private WebElement example2;

    @FindBys({
            @FindBy(id = "start"),
            @FindBy(tagName = "button"),
    })
    private WebElement start;

    @FindBy(id = "finish")
    private WebElement finish;

    @FindBy(id = "loading")
    private WebElement loading;

    public DynamicLoadingPage openExample1() {
        example1.click();

        return this;
    }

    public DynamicLoadingPage openExample2() {
        example2.click();

        return this;
    }
}
