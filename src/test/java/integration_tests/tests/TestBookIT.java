package integration_tests.tests;

import com.github.giulong.spectrum.SpectrumTest;
import com.github.giulong.spectrum.utils.testbook.reporters.HtmlTestBookReporter;
import integration_tests.pages.TestBookPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TestBook")
public class TestBookIT extends SpectrumTest<Void> {

    private TestBookPage testBookPage;

    @Test
    @DisplayName("should check the testbook")
    public void testbook() {
        final HtmlTestBookReporter htmlTestBookReporter = (HtmlTestBookReporter) configuration
                .getTestBook()
                .getReporters()
                .stream()
                .filter(testBookReporter -> testBookReporter.getClass().equals(HtmlTestBookReporter.class))
                .findFirst()
                .orElseThrow();

        webDriver.get(Path.of(htmlTestBookReporter.getOutput()).toAbsolutePath().toString());

        assertEquals("TestBook Results", testBookPage.getTitle().getText());

        // STATISTICS
        assertEquals("4", testBookPage.getTitle().getText());
        assertEquals("8", testBookPage.getMappedTests().getText());
        assertEquals("4 + 8 = 12", testBookPage.getUnmappedTests().getText());
        assertEquals("5", testBookPage.getGrandTotal().getText());
        assertEquals("5 + 8 = 13", testBookPage.getTotalWeighted().getText());
    }
}
