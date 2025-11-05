package io.github.giulong.spectrum.it_appium.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_appium.data.Data;
import io.github.giulong.spectrum.it_appium.pages.AndroidPage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

@SuppressWarnings("unused")
class AndroidIT extends SpectrumTest<Data> {

    private AndroidPage androidPage;

    @RepeatedTest(2)
    @DisplayName("Android test")
    void androidTest() {
        assertEquals(data.getText(), androidPage.getTextView().getText());
    }
}
