package io.github.giulong.spectrum.it_testbook.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_testbook.data.Data;
import io.github.giulong.spectrum.it_testbook.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;

@DisplayName("Login Form")
@SuppressWarnings("unused")
public class LoginFormIT extends SpectrumTest<Data> {

    private LoginPage loginPage;

    @DisplayName("Login Form leveraging the data.yaml")
    @ParameterizedTest(name = "with user {0} we expect login to be successful: {1}")
    @MethodSource("valuesProvider")
    public void shouldRunSuccessfully(final String userName, final boolean expected, final String endpoint) {
        loginPage.open();
        assertTrue(isNotPresent(id("flash")));

        loginPage
                .getForm()
                .submit();

        assertTrue(loginPage
                .getErrorMessage()
                .isDisplayed());

        loginPage
                .screenshotInfo("An error should be displayed if no username nor password is provided")
                .loginWith(data.getUsers().get(userName))
                .screenshotInfo("After successful login");

        pageLoadWait.until(urlContains(endpoint));
        assertEquals(expected, Objects.requireNonNull(driver.getCurrentUrl()).endsWith("/secure"));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("tom", true, "/secure"),
                arguments("giulio", false, "/login")
        );
    }
}
