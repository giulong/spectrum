package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.it.data.Data;

import lombok.Getter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

@Getter
@Endpoint("dynamic_controls")
@SuppressWarnings("unused")
public class DynamicControlsPage extends SpectrumPage<DynamicControlsPage, Data> {

    @FindBy(id = "loading")
    private WebElement loading;

    @FindBys({
            @FindBy(id = "checkbox"),
            @FindBy(tagName = "input"),
    })
    private WebElement checkbox;

    @FindBys({
            @FindBy(id = "checkbox-example"),
            @FindBy(tagName = "button"),
    })
    private WebElement addRemove;

    @FindBy(id = "message")
    private WebElement message;

    @FindBys({
            @FindBy(id = "input-example"),
            @FindBy(tagName = "input"),
    })
    private WebElement input;

    @FindBys({
            @FindBy(id = "input-example"),
            @FindBy(tagName = "button"),
    })
    private WebElement enableDisable;
}
