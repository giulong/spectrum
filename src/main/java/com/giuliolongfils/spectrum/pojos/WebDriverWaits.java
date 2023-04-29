package com.giuliolongfils.spectrum.pojos;

import lombok.Builder;
import lombok.Getter;
import org.openqa.selenium.support.ui.WebDriverWait;

@Builder
@Getter
public class WebDriverWaits {
    private WebDriverWait wait;
    private WebDriverWait pageLoadingWait;
    private WebDriverWait downloadWait;
    private WebDriverWait scriptWait;
    private WebDriverWait instantWait;
}
