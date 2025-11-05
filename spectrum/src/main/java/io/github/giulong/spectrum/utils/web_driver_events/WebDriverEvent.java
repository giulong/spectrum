package io.github.giulong.spectrum.utils.web_driver_events;

import java.util.List;

import io.github.giulong.spectrum.enums.Frame;

import lombok.Builder;
import lombok.Getter;

import org.openqa.selenium.WebElement;
import org.slf4j.event.Level;

@Getter
@Builder
public class WebDriverEvent {

    private static final String TAG = "<.*?>";

    private Frame frame;
    private Level level;
    private String message;
    private List<Object> args;

    String removeTagsFromMessage() {
        return message.replaceAll(TAG, "");
    }

    List<WebElement> findWebElementsInArgs() {
        return args
                .stream()
                .filter(WebElement.class::isInstance)
                .map(WebElement.class::cast)
                .toList();
    }
}
