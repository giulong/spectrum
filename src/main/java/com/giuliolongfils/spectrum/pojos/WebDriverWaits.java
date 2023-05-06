package com.giuliolongfils.spectrum.pojos;

import lombok.Builder;
import lombok.Getter;
import org.openqa.selenium.support.ui.WebDriverWait;

@Builder
@Getter
public class WebDriverWaits {
    private WebDriverWait implicit;
    private WebDriverWait pageLoadTimeout;
    private WebDriverWait downloadTimeout;
    private WebDriverWait scriptTimeout;
    private WebDriverWait instantTimeout;
}
