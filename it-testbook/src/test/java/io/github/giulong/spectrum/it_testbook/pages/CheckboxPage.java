package io.github.giulong.spectrum.it_testbook.pages;

import java.util.List;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;

import lombok.Getter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

@Getter
@Endpoint("checkboxes")
@SuppressWarnings("unused")
public class CheckboxPage extends SpectrumPage<CheckboxPage, Void> {

    @FindBys({
            @FindBy(id = "checkboxes"),
            @FindBy(tagName = "input")
    })
    private List<WebElement> checkboxes;
}
