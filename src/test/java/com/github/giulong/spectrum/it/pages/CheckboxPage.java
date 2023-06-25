package com.github.giulong.spectrum.it.pages;

import com.github.giulong.spectrum.SpectrumPage;
import com.github.giulong.spectrum.interfaces.Endpoint;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.util.List;

@Getter
@Endpoint("checkboxes")
public class CheckboxPage extends SpectrumPage<CheckboxPage, Void> {

    @FindBys({
            @FindBy(id = "checkboxes"),
            @FindBy(tagName = "input")
    })
    private List<WebElement> checkboxes;
}
