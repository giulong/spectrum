package io.github.giulong.spectrum.it_testbook.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.it_testbook.data.Data;
import io.github.giulong.spectrum.interfaces.Secured;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@Endpoint("login")
@SuppressWarnings("unused")
public class LoginPage extends SpectrumPage<LoginPage, Void> {

    @FindBy(id = "flash")
    private WebElement errorMessage;

    @FindBy(id = "username")
    private WebElement username;

    @FindBy(id = "password")
    @Secured
    private WebElement password;

    @FindBy(id = "login")
    private WebElement form;

    public LoginPage loginWith(final Data.User user) {
        clearAndSendKeys(username, user.getName());
        clearAndSendKeys(password, user.getPassword());

        form.submit();
        return this;
    }
}
