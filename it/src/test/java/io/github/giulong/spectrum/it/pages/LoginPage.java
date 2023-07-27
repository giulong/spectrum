package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.it.data.Data;
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
public class LoginPage extends SpectrumPage<LoginPage, Void> {

    @FindBy(id = "flash")
    private WebElement errorMessage;

    @FindBy(id = "username")
    private WebElement username;

    @FindBy(id = "password")
    private WebElement password;

    @FindBy(id = "login")
    private WebElement form;

    @Override
    public LoginPage waitForPageLoading() {
        log.info("Wait for page loading: waiting for errorMessage to disappear");
        implicitWait.until((ExpectedCondition<Boolean>) webDriver -> isNotPresent(id("flash")));

        return this;
    }

    public LoginPage loginWith(final Data.User user) {
        clearAndSendKeys(username, user.getName());
        clearAndSendKeys(password, user.getPassword());

        form.submit();
        return this;
    }
}
