package io.github.giulong.spectrum.it_macos.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

@Getter
@Endpoint("checkboxes")
@SuppressWarnings("unused")
public class CheckboxPage extends SpectrumPage<CheckboxPage, Void> {

    @FindBys({
            @FindBy(id = "checkboxes"),
            @FindBy(tagName = "input")
    })
    private List<WebElement> checkboxes;

    @Override
    public CheckboxPage waitForPageLoading() {
        pageLoadWait.until(and(
                urlToBe("https://the-internet.herokuapp.com/checkboxes"),
                visibilityOfAllElements(checkboxes)));

        return this;
    }
}
