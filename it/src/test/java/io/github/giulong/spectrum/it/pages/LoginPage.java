package io.github.giulong.spectrum.it.pages;

import static org.openqa.selenium.By.id;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.interfaces.Secured;
import io.github.giulong.spectrum.it.data.Data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

@Getter
@Slf4j
@Endpoint("login")
@SuppressWarnings("unused")
public class LoginPage extends SpectrumPage<LoginPage, Data> {

    @FindBy(id = "flash")
    private WebElement errorMessage;

    @FindBy(id = "username")
    private WebElement username;

    @FindBy(id = "password")
    @Secured
    private WebElement password;

    @FindBy(id = "login")
    private WebElement form;

    @FindBy(id = "content")
    private WebElement contentDiv;

    @FindBy(className = "subheader")
    private WebElement subHeader;

    @Override
    public LoginPage waitForPageLoading() {
        log.info("Wait for page loading: waiting for errorMessage to disappear");
        pageLoadWait.until((ExpectedCondition<Boolean>) driver -> isNotPresent(id(data.getFlashMessageId())));

        return this;
    }

    public LoginPage loginWith(final Data.User user) {
        return loginWith(user.getName(), user.getPassword());
    }

    public LoginPage loginWith(final String name, final String pwd) {
        clearAndSendKeys(username, name);
        clearAndSendKeys(password, pwd);

        form.submit();
        return this;
    }
}
