package io.github.giulong.spectrum.it_generated;

import io.github.giulong.spectrum.SpectrumTest;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

class GeneratedIT extends SpectrumTest<Void> {

    @Test
    void test() {
        driver.get("https://the-internet.herokuapp.com/");
        driver.findElement(By.xpath("id(\"content\")/ul[1]/li[2]/a[1]")).click();
        driver.findElement(By.xpath("id(\"content\")/div[@class=\"example\"]/button[1]")).click();
        driver.findElement(By.xpath("id(\"elements\")/button[@class=\"added-manually\"]")).click();
        driver.findElement(By.xpath("id(\"content\")/div[@class=\"example\"]/button[1]")).click();
        driver.findElement(By.xpath("id(\"elements\")/button[@class=\"added-manually\"]")).click();
    }
}