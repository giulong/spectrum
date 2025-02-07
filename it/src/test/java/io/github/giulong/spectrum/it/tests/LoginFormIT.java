package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.it.pages.InputsPage;
import io.github.giulong.spectrum.it.pages.LoginPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebElement;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.Keys.ARROW_UP;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;

@Slf4j
@DisplayName("Login Form")
@SuppressWarnings("unused")
class LoginFormIT extends BaseIT {

    private LoginPage loginPage;
    private InputsPage inputsPage;

    @BeforeEach
    void beforeEach() {
        log.info("Here just to check we're not overriding the internal beforeEach");
    }

    // Let's try with JUnit's parameterized tests
    @DisplayName("leveraging the data.yaml")
    @ParameterizedTest(name = "with user {0} we expect login to be successful {1}")
    @MethodSource("valuesProvider")
    void shouldRunSuccessfully(final String userName, final boolean expected, final String endpoint) {
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
                .open()
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

    @Test
    @DisplayName("even if we have pages with @Secure web elements (password in LoginPage), tests that don't use them must not fail")
    void inputs() {
        final String number = "2";
        final WebElement input = inputsPage.getInput();

        inputsPage.open();

        input.sendKeys(number);
        input.sendKeys(ARROW_UP);

        assertEquals("3", input.getDomProperty("value"));
    }
}
