package io.github.giulong.spectrum.it_generated.tests;

import io.github.giulong.spectrum.SpectrumTest;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

class GeneratedIT extends SpectrumTest<Void> {

    @Test
    void test() {
        driver.get("https://the-internet.herokuapp.com/");
        driver.findElement(By.xpath("id(\"content\")/ul[1]/li[6]/a[1]")).click();
        driver.findElement(By.xpath("id(\"checkboxes\")/input[1]")).click();
        driver.findElement(By.xpath("id(\"checkboxes\")/input[2]")).click();

        driver.navigate().back();

        driver.navigate().forward();
    }
}
