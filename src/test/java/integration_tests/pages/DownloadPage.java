package integration_tests.pages;

import com.github.giulong.spectrum.SpectrumPage;
import com.github.giulong.spectrum.interfaces.Endpoint;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.util.List;

@Getter
@Endpoint("download")
public class DownloadPage extends SpectrumPage<DownloadPage, Void> {

    @FindBys({
            @FindBy(id = "content"),
            @FindBy(className = "example"),
            @FindBy(tagName = "a"),
    })
    private List<WebElement> downloadLinks;
}
