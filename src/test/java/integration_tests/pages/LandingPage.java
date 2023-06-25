package integration_tests.pages;

import com.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
public class LandingPage extends SpectrumPage<LandingPage, Void> {

    @FindBy(tagName = "h1")
    private WebElement title;

    @FindBy(linkText = "Checkboxes")
    private WebElement checkboxLink;
}
