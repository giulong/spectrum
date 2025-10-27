package io.github.giulong.spectrum.it_testbook.pages;

import java.util.List;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;

import lombok.Getter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

@Getter
@Endpoint("download")
@SuppressWarnings("unused")
public class DownloadPage extends SpectrumPage<DownloadPage, Void> {

    @FindBys({
            @FindBy(id = "content"),
            @FindBy(className = "example"),
            @FindBy(tagName = "a"),
    })
    private List<WebElement> downloadLinks;
}
