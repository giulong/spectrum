package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.interfaces.JsWebElement;
import io.github.giulong.spectrum.it.data.Data;
import io.github.giulong.spectrum.interfaces.Secured;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

import static org.openqa.selenium.By.id;

@Getter
@Slf4j
@Endpoint("login")
@SuppressWarnings("unused")
public class JsLoginPage extends SpectrumPage<JsLoginPage, Void> {

    @FindBy(id = "flash")
    @JsWebElement
    private WebElement errorMessage;

    @FindBy(id = "username")
    @JsWebElement
    private WebElement username;

    @FindBy(id = "password")
    @JsWebElement
    @Secured
    private WebElement password;

    @FindBy(id = "login")
    @JsWebElement
    private WebElement form;

    @FindBy(id = "content")
    @JsWebElement
    private WebElement contentDiv;

    @FindBy(className = "subheader")
    @JsWebElement
    private WebElement subHeader;

    @Override
    public JsLoginPage waitForPageLoading() {
        log.info("Wait for page loading: waiting for errorMessage to disappear");
        implicitWait.until((ExpectedCondition<Boolean>) driver -> isNotPresent(id("flash")));

        return this;
    }

    public JsLoginPage loginWith(final Data.User user) {
        clearAndSendKeys(username, user.getName());
        clearAndSendKeys(password, user.getPassword());

        form.submit();
        return this;
    }
}
