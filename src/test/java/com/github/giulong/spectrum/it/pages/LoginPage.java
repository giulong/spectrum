package com.github.giulong.spectrum.it.pages;

import com.github.giulong.spectrum.SpectrumPage;
import com.github.giulong.spectrum.interfaces.Endpoint;
import com.github.giulong.spectrum.it.data.Data;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@Endpoint("login")
public class LoginPage extends SpectrumPage<LoginPage, Void> {

    @FindBy(id = "flash")
    private WebElement errorMessage;

    @FindBy(id = "username")
    private WebElement username;

    @FindBy(id = "password")
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
