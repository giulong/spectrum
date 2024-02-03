package io.github.giulong.spectrum.it_appium.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_appium.data.Data;
import io.github.giulong.spectrum.it_appium.pages.AndroidPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
@DisplayName("Android")
public class AndroidIT extends SpectrumTest<Data> {

    private AndroidPage androidPage;

    @RepeatedTest(2)
    @DisplayName("Android test")
    public void androidTest() {
        assertEquals(data.getText(), androidPage.getTextView().getText());
    }
}
