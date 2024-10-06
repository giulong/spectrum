package io.github.giulong.spectrum.utils.web_driver_events;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

import static com.aventstack.extentreports.markuputils.ExtentColor.YELLOW;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static org.slf4j.event.Level.WARN;

@Slf4j
@Builder
public class HtmlReportConsumer implements Consumer<WebDriverEvent> {

    private StatefulExtentTest statefulExtentTest;

    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        final ExtentTest currentNode = statefulExtentTest.getCurrentNode();
        final String message = webDriverEvent.getMessage();

        if (WARN.equals(webDriverEvent.getLevel())) {
            final Markup label = createLabel(message, YELLOW);
            log.trace("Logging {} at warn level", label);
            currentNode.warning(label);
        } else {
            log.trace("Logging {} at info level", message);
            currentNode.info(message);
        }
    }
}
