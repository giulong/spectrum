package integration_tests.pages;

import com.github.giulong.spectrum.SpectrumPage;
import com.github.giulong.spectrum.interfaces.Endpoint;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@Endpoint("upload")
public class UploadPage extends SpectrumPage<UploadPage, Void> {

    @FindBy(id = "file-upload")
    private WebElement fileUpload;

    @FindBy(id = "file-submit")
    private WebElement submit;

    @FindBy(id = "uploaded-files")
    private WebElement uploadedFiles;
}
