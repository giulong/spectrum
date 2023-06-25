package com.github.giulong.spectrum.it.tests;

import com.github.giulong.spectrum.SpectrumTest;
import com.github.giulong.spectrum.it.data.Data;
import com.github.giulong.spectrum.it.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Login Form")
public class LoginFormIT extends SpectrumTest<Data> {

    private LoginPage loginPage;

    // Let's try with JUnit's parameterized tests
    @DisplayName("Login Form leveraging the data.yaml")
    @ParameterizedTest(name = "with user {0} we expect login to be successful: {1}")
    @MethodSource("valuesProvider")
    public void shouldRunSuccessfully(final String userName, final boolean expected) {
        loginPage.open();
        assertTrue(isNotPresent(By.id("flash")));

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

        assertEquals(expected, webDriver.getCurrentUrl().endsWith("/secure"));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("tom", true),
                arguments("giulio", false)
        );
    }
}
